package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletableFuture;

import com.just.core.functional.result.Result;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.search.SegmentedPathPlanner;

/**
 * Owns path planning, pending async path application, route segmentation, and target-change replanning.
 */
final class PathNavigationPlanningComponent {

    private final LevelReader level;

    private final PathNavigationStateComponent state;

    private final BLibPathFinder pathFinder;

    private final @Nullable SegmentedPathPlanner planner;

    private final PathNavigationTargetComponent targets;

    private final PathNavigationFeatureComponent featureControl;

    private final PathNavigationRuntimeConfigComponent runtimeConfig;

    private final PathNavigationFailureBackoff failureBackoff;

    private final PathNavigationWaypointFollower waypointFollower;

    private final PathNavigationProgressTracker progressTracker;

    private final Runnable progressResetter;

    private boolean needsRepath;

    PathNavigationPlanningComponent(
        LevelReader level,
        PathNavigationStateComponent state,
        BLibPathFinder pathFinder,
        @Nullable SegmentedPathPlanner planner,
        PathNavigationTargetComponent targets,
        PathNavigationFeatureComponent featureControl,
        PathNavigationRuntimeConfigComponent runtimeConfig,
        PathNavigationFailureBackoff failureBackoff,
        PathNavigationWaypointFollower waypointFollower,
        PathNavigationProgressTracker progressTracker,
        Runnable progressResetter
    ) {
        this.level = level;
        this.state = state;
        this.pathFinder = pathFinder;
        this.planner = planner;
        this.targets = targets;
        this.featureControl = featureControl;
        this.runtimeConfig = runtimeConfig;
        this.failureBackoff = failureBackoff;
        this.waypointFollower = waypointFollower;
        this.progressTracker = progressTracker;
        this.progressResetter = progressResetter;
    }

    Result<BLibPath, PathNavigationFailure> executeBlocking(
        BlockPos entityPos,
        BlockPos rawTarget,
        @Nullable PathfindingFeatures pathfindingFeatures,
        boolean stuckReplan
    ) {
        cancelPendingPath();

        if (!stuckReplan) {
            featureControl.setActivePathfindingFeatures(pathfindingFeatures);
        }

        var activePath = state.currentActivePathContext();
        var searchTarget = targets.resolveAndStoreTarget(entityPos, rawTarget);
        var request = state.requestContext(entityPos, rawTarget, searchTarget);
        state.enterPlanning(request, null, null, System.nanoTime(), activePath);

        if (
            !stuckReplan
                && failureBackoff.isInFailureCooldown(
                    state,
                    searchTarget,
                    targets::hasComputedTargetMovedForFailureCooldown
                )
        ) {
            var failure = failureCooldown(request);
            state.completePlanningWithFailed(request, failure);

            return Result.err(failure);
        }

        targets.recordComputedTarget(searchTarget, rawTarget);

        if (!stuckReplan) {
            progressTracker.clearStuckReplanHistory();
        }

        preparePathFinder();

        var startNanos = System.nanoTime();

        BLibPath path;

        if (shouldUsePlanner()) {
            markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
            path = planner.findPath(level, entityPos, searchTarget);
        } else {
            path = pathFinder.findPath(level, entityPos, searchTarget);
        }
        featureControl.markFeatureUsage(pathFinder.consumeFeatureUsageMask());

        return applyComputedPath(request, path, System.nanoTime() - startNanos);
    }

    CompletableFuture<Result<BLibPath, PathNavigationFailure>> executeAsync(
        BlockPos entityPos,
        BlockPos rawTarget,
        @Nullable PathfindingFeatures pathfindingFeatures
    ) {
        cancelPendingPath();
        featureControl.setActivePathfindingFeatures(pathfindingFeatures);

        var searchFeatures = activePathfindingFeatures();

        if (!searchFeatures.asyncPathfinding() || searchFeatures.blockBreaking()) {
            return CompletableFuture.completedFuture(executeBlocking(entityPos, rawTarget, pathfindingFeatures, false));
        }

        var activePath = state.currentActivePathContext();
        var searchTarget = targets.resolveAndStoreTarget(entityPos, rawTarget);
        var request = state.requestContext(entityPos, rawTarget, searchTarget);

        if (
            failureBackoff.isInFailureCooldown(
                state,
                searchTarget,
                targets::hasComputedTargetMovedForFailureCooldown
            )
        ) {
            var failure = failureCooldown(request);
            state.enterPlanning(request, null, null, System.nanoTime(), activePath);
            state.completePlanningWithFailed(request, failure);

            return CompletableFuture.completedFuture(Result.err(failure));
        }

        targets.recordComputedTarget(searchTarget, rawTarget);
        progressTracker.clearStuckReplanHistory();
        preparePathFinder();
        markFeatureUsed(PathfindingFeature.ASYNC_PATHFINDING);

        var asyncStartNanos = System.nanoTime();
        featureControl.setPendingPathfindingFeatures(searchFeatures);
        var pendingResult = new CompletableFuture<Result<BLibPath, PathNavigationFailure>>();
        var pendingPath = pathFinder.findPathAsync(level, entityPos, searchTarget);
        state.enterPlanning(request, pendingPath, pendingResult, asyncStartNanos, activePath);
        pendingPath.whenComplete((path, throwable) -> completeAsyncConstructionResult(
            pendingResult,
            request,
            path,
            throwable
        ));

        return pendingResult;
    }

    void checkPendingPath() {
        var planning = state.pendingPlanning();

        if (planning == null) {
            return;
        }

        var pendingPath = planning.pendingPath();

        if (pendingPath == null || !pendingPath.isDone()) {
            return;
        }

        var pendingResult = planning.resultFuture();
        var request = planning.request();

        var path = joinPendingPath(pendingPath, pendingResult, request);

        if (path == null && pendingPath.isCompletedExceptionally()) {
            return;
        }

        var searchFeatureUsage = pathFinder.consumeFeatureUsageMask();

        if (!featureControl.pendingPathfindingFeaturesMatchActive()) {
            featureControl.clearPendingPathfindingFeatures();
            completePendingResult(pendingResult, Result.err(new PathNavigationFailure.Superseded()));

            needsRepath = true;
            state.enterAwaitingRepath(request);
            return;
        }

        featureControl.clearPendingPathfindingFeatures();
        featureControl.markFeatureUsage(searchFeatureUsage);

        applyComputedPath(request, path, System.nanoTime() - planning.startNanos());
    }

    void handleQueuedRepath(double entityX, double entityY, double entityZ) {
        if (!needsRepath) {
            return;
        }

        needsRepath = false;

        var request = state.currentRequest();

        if (request != null) {
            executeBlocking(
                targets.entityAnchorPos(entityX, entityY, entityZ),
                request.rawTarget(),
                featureControl.activePathfindingFeaturesOverride(),
                false
            );
        }
    }

    void advanceSegmentIfNeeded(double entityX, double entityY, double entityZ) {
        if (state.pendingPlanning() != null) {
            return;
        }

        var path = state.currentPath();

        if (path == null || !path.isDone() || !shouldUsePlanner() || !planner.hasActiveRoute()) {
            return;
        }

        if (path.isReached()) {
            state.refreshTerminalLifecycle();
            planner.clear();
            return;
        }

        advanceToNextSegment(entityX, entityY, entityZ);
    }

    void checkRecalculate(
        BlockPos entityPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var target = state.currentSearchTarget();

        if (target == null) {
            return;
        }

        if (state.tickCount - state.lastPathComputeTick < runtimeConfig.getPathRecalculateIntervalInTicks()) {
            return;
        }

        if (targets.hasTargetMovedForRecalculation()) {
            if (tryReuseCurrentPathPrefix(target)) {
                waypointFollower.advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);
                return;
            }

            clearPlanner();

            executeBlocking(
                entityPos,
                targets.activeRawTargetPos(),
                featureControl.activePathfindingFeaturesOverride(),
                false
            );

            // Advance past any nodes the entity has already reached so the new
            // path doesn't briefly target the start node behind the entity.
            if (state.hasActivePath()) {
                waypointFollower.advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);
            }
        }
    }

    void replanAfterStuck(BlockPos entityAnchorPos) {
        clearPlanner();
        executeBlocking(
            entityAnchorPos,
            targets.activeRawTargetPos(),
            featureControl.activePathfindingFeaturesOverride(),
            true
        );
    }

    void invalidateActivePathForReplan() {
        cancelPendingPath();
        clearPlanner();

        progressTracker.clearStuckReplanHistory();
        resetProgressTracking();

        if (state.currentRequest() != null) {
            needsRepath = true;
            state.enterAwaitingRepath(requestForActiveTarget(anchorFromLastPosition()));
        } else {
            state.enterIdle();
        }
    }

    void stopPlanning() {
        cancelPendingPath();
        clearPlanner();
        needsRepath = false;
    }

    void recordFailure() {
        failureBackoff.recordFailure(state);
    }

    private boolean tryReuseCurrentPathPrefix(BlockPos target) {
        var path = state.activePath();

        if (
            !activePathfindingFeatures().pathPrefixReuse()
                || path == null
                || !path.isReached()
        ) {
            return false;
        }

        var targetIndex = findRemainingPathNodeIndex(path, target);

        if (targetIndex < path.getCurrentNodeIndex()) {
            return false;
        }

        var reusedNodes = new ArrayList<PathNode>(targetIndex - path.getCurrentNodeIndex() + 1);

        for (var index = path.getCurrentNodeIndex(); index <= targetIndex; index++) {
            reusedNodes.add(path.getNode(index));
        }

        var reusedPath = new BLibPath(reusedNodes, true);
        var terrain = reusedPath.getCurrentNode().getTerrainType();
        var entityStart = state.hasLastEntityPosition
            ? BlockPos.containing(state.lastEntityX, state.lastEntityY, state.lastEntityZ)
            : target;
        cancelPendingPath();
        state.replaceNavigatingPath(state.requestContext(entityStart, targets.activeRawTargetPos(), target), reusedPath, terrain);
        targets.recordPrefixReuse(target);
        state.lastPathComputeTick = state.tickCount;
        state.lastPathComputeNanos = 0L;
        markFeatureUsed(PathfindingFeature.PATH_PREFIX_REUSE);
        resetProgressTracking();

        return true;
    }

    private int findRemainingPathNodeIndex(BLibPath path, BlockPos target) {
        for (var index = path.getCurrentNodeIndex(); index < path.getNodeCount(); index++) {
            var node = path.getNode(index);

            if (node.getX() == target.getX() && node.getY() == target.getY() && node.getZ() == target.getZ()) {
                return index;
            }
        }

        return -1;
    }

    private void advanceToNextSegment(double entityX, double entityY, double entityZ) {
        var entityPos = targets.entityAnchorPos(entityX, entityY, entityZ);
        var request = requestForActiveTarget(entityPos);
        var activePath = state.currentActivePathContext();
        var startNanos = System.nanoTime();
        state.enterPlanning(request, null, null, startNanos, activePath);

        preparePathFinder();
        markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
        var path = planner.computeNextSegment(level, entityPos);
        featureControl.markFeatureUsage(pathFinder.consumeFeatureUsageMask());
        applyComputedPath(request, path, System.nanoTime() - startNanos);

        if (path == null) {
            clearPlanner();
        }
    }

    private Result<BLibPath, PathNavigationFailure> applyComputedPath(
        PathNavigationLifecycle.RequestContext request,
        @Nullable BLibPath path,
        long pathComputeNanos
    ) {
        state.requirePlanningFor(request);
        this.state.lastPathComputeNanos = pathComputeNanos;
        this.state.lastPathComputeTick = state.tickCount;
        resetProgressTracking();

        if (isUsablePath(path)) {
            var startNode = path.getCurrentNode();
            var terrain = startNode.getTerrainType();

            failureBackoff.resetFailureCooldown(state);

            if (path.isDone() && path.isReached()) {
                state.completePlanningWithReached(request, path, terrain);
            } else if (path.isDone() && shouldUsePlanner() && planner.hasActiveRoute()) {
                state.completePlanningWithAwaitingNextSegment(request, path, terrain);
            } else if (path.isDone()) {
                state.completePlanningWithExhausted(request, path, terrain);
            } else {
                state.completePlanningWithNavigating(request, path, terrain);
            }

            return Result.ok(path);
        } else {
            var failure = new PathNavigationFailure.NoPathFound(
                request.entityStart(),
                request.rawTarget(),
                request.searchTarget()
            );
            state.completePlanningWithFailed(request, failure);
            failureBackoff.recordFailure(state);

            return Result.err(failure);
        }
    }

    private @Nullable BLibPath joinPendingPath(
        CompletableFuture<@Nullable BLibPath> pendingPath,
        @Nullable CompletableFuture<Result<BLibPath, PathNavigationFailure>> pendingResult,
        PathNavigationLifecycle.RequestContext request
    ) {
        try {
            return pendingPath.join();
        } catch (CancellationException exception) {
            featureControl.clearPendingPathfindingFeatures();
            completePendingResult(pendingResult, Result.err(new PathNavigationFailure.Superseded()));
            state.clearPendingLifecycleData();
            return null;
        } catch (CompletionException exception) {
            featureControl.clearPendingPathfindingFeatures();
            var failure = new PathNavigationFailure.SearchFailed(
                request.entityStart(),
                request.rawTarget(),
                request.searchTarget(),
                exception.getCause() != null ? exception.getCause() : exception
            );
            state.completePlanningWithFailed(request, failure);
            failureBackoff.recordFailure(state);
            completePendingResult(pendingResult, Result.err(failure));

            return null;
        }
    }

    private void completeAsyncConstructionResult(
        CompletableFuture<Result<BLibPath, PathNavigationFailure>> resultFuture,
        PathNavigationLifecycle.RequestContext request,
        @Nullable BLibPath path,
        @Nullable Throwable throwable
    ) {
        if (throwable instanceof CancellationException) {
            resultFuture.complete(Result.err(new PathNavigationFailure.Superseded()));
            return;
        }

        if (throwable instanceof CompletionException completionException && completionException.getCause() instanceof CancellationException) {
            resultFuture.complete(Result.err(new PathNavigationFailure.Superseded()));
            return;
        }

        if (throwable != null) {
            var cause = throwable instanceof CompletionException completionException && completionException.getCause() != null
                ? completionException.getCause()
                : throwable;
            resultFuture.complete(Result.err(new PathNavigationFailure.SearchFailed(
                request.entityStart(),
                request.rawTarget(),
                request.searchTarget(),
                cause
            )));
            return;
        }

        if (isUsableAsyncPath(path)) {
            resultFuture.complete(Result.ok(path));
        } else {
            resultFuture.complete(Result.err(new PathNavigationFailure.NoPathFound(
                request.entityStart(),
                request.rawTarget(),
                request.searchTarget()
            )));
        }
    }

    private void completePendingResult(
        @Nullable CompletableFuture<Result<BLibPath, PathNavigationFailure>> pendingResult,
        Result<BLibPath, PathNavigationFailure> result
    ) {
        if (pendingResult != null) {
            pendingResult.complete(result);
        }
    }

    private PathNavigationFailure.InFailureCooldown failureCooldown(PathNavigationLifecycle.RequestContext request) {
        return new PathNavigationFailure.InFailureCooldown(
            request.entityStart(),
            request.rawTarget(),
            request.searchTarget(),
            state.getFailureCooldownRemainingTicks()
        );
    }

    private boolean isUsableAsyncPath(@Nullable BLibPath path) {
        return path != null && (path.isReached() || path.getNodeCount() > 1);
    }

    private boolean isUsablePath(@Nullable BLibPath path) {
        return path != null
            && (path.isReached()
                || path.getNodeCount() > 1
                || (shouldUsePlanner() && planner.hasActiveRoute()));
    }

    private void preparePathFinder() {
        pathFinder.setFeatures(activePathfindingFeatures());
        pathFinder.setExcludedTerrains(runtimeConfig.getExcludedTerrains());
    }

    private boolean shouldUsePlanner() {
        return activePathfindingFeatures().segmentedPathPlanning() && planner != null;
    }

    private PathfindingFeatures activePathfindingFeatures() {
        return featureControl.getPathfindingFeatures();
    }

    private void cancelPendingPath() {
        var planning = state.pendingPlanning();

        if (planning != null) {
            var pendingPath = planning.pendingPath();

            if (pendingPath != null) {
                pendingPath.cancel(false);
            }

            completePendingResult(planning.resultFuture(), Result.err(new PathNavigationFailure.Superseded()));
            state.clearPendingLifecycleData();
        }

        featureControl.clearPendingPathfindingFeatures();
    }

    private BlockPos anchorFromLastPosition() {
        return state.hasLastEntityPosition
            ? BlockPos.containing(state.lastEntityX, state.lastEntityY, state.lastEntityZ)
            : Objects.requireNonNullElse(state.currentSearchTarget(), BlockPos.ZERO);
    }

    private PathNavigationLifecycle.RequestContext requestForActiveTarget(BlockPos entityPos) {
        var currentRequest = state.currentRequest();

        if (currentRequest != null) {
            return state.requestContext(entityPos, currentRequest.rawTarget(), currentRequest.searchTarget());
        }

        return state.requestContext(entityPos, entityPos, entityPos);
    }

    private void clearPlanner() {
        if (planner != null) {
            planner.clear();
        }
    }

    private void resetProgressTracking() {
        progressResetter.run();
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureControl.markFeatureUsed(feature);
    }
}
