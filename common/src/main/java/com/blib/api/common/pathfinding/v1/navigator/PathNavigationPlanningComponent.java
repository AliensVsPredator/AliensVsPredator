package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    private long asyncStartNanos;

    private boolean needsRepath;

    private @Nullable BlockPos pendingEntityPos;

    private @Nullable BlockPos pendingRawTarget;

    private @Nullable BlockPos pendingSearchTarget;

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

        var searchTarget = targets.resolveAndStoreTarget(entityPos, rawTarget);

        if (
            !stuckReplan
                && failureBackoff.isInFailureCooldown(
                    state,
                    searchTarget,
                    targets::hasComputedTargetMovedForFailureCooldown
                )
        ) {
            return Result.err(failureCooldown(entityPos, rawTarget, searchTarget));
        }

        targets.recordComputedTarget(searchTarget, rawTarget);

        if (!stuckReplan) {
            progressTracker.clearStuckReplanHistory();
        }

        preparePathFinder();

        var startNanos = System.nanoTime();

        if (shouldUsePlanner()) {
            markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
            this.state.currentPath = planner.findPath(level, entityPos, searchTarget);
        } else {
            this.state.currentPath = pathFinder.findPath(level, entityPos, searchTarget);
        }
        featureControl.markFeatureUsage(pathFinder.consumeFeatureUsageMask());

        return applyComputedPath(entityPos, rawTarget, searchTarget, System.nanoTime() - startNanos);
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

        var searchTarget = targets.resolveAndStoreTarget(entityPos, rawTarget);

        if (
            failureBackoff.isInFailureCooldown(
                state,
                searchTarget,
                targets::hasComputedTargetMovedForFailureCooldown
            )
        ) {
            return CompletableFuture.completedFuture(Result.err(failureCooldown(entityPos, rawTarget, searchTarget)));
        }

        targets.recordComputedTarget(searchTarget, rawTarget);
        progressTracker.clearStuckReplanHistory();
        preparePathFinder();
        markFeatureUsed(PathfindingFeature.ASYNC_PATHFINDING);

        this.asyncStartNanos = System.nanoTime();
        featureControl.setPendingPathfindingFeatures(searchFeatures);
        var pendingResult = new CompletableFuture<Result<BLibPath, PathNavigationFailure>>();
        var pendingPath = pathFinder.findPathAsync(level, entityPos, searchTarget);
        this.pendingEntityPos = entityPos;
        this.pendingRawTarget = rawTarget;
        this.pendingSearchTarget = searchTarget;
        this.state.pendingPath = pendingPath;
        this.state.pendingPathResult = pendingResult;
        pendingPath.whenComplete((path, throwable) -> completeAsyncConstructionResult(
            pendingResult,
            entityPos,
            rawTarget,
            searchTarget,
            path,
            throwable
        ));

        return pendingResult;
    }

    void checkPendingPath() {
        if (state.pendingPath == null || !state.pendingPath.isDone()) {
            return;
        }

        var pendingPath = state.pendingPath;
        var pendingResult = state.pendingPathResult;
        var entityPos = pendingEntityPos;
        var rawTarget = pendingRawTarget;
        var searchTarget = pendingSearchTarget;
        clearPendingPathReferences();

        if (entityPos == null || rawTarget == null || searchTarget == null) {
            featureControl.clearPendingPathfindingFeatures();
            completePendingResult(pendingResult, Result.err(new PathNavigationFailure.Superseded()));
            return;
        }

        var path = joinPendingPath(pendingPath, pendingResult, entityPos, rawTarget, searchTarget);

        if (path == null && pendingPath.isCompletedExceptionally()) {
            return;
        }

        var searchFeatureUsage = pathFinder.consumeFeatureUsageMask();

        if (!featureControl.pendingPathfindingFeaturesMatchActive()) {
            featureControl.clearPendingPathfindingFeatures();
            completePendingResult(pendingResult, Result.err(new PathNavigationFailure.Superseded()));

            if (state.targetPos != null) {
                needsRepath = true;
            }
            return;
        }

        featureControl.clearPendingPathfindingFeatures();
        featureControl.markFeatureUsage(searchFeatureUsage);

        this.state.currentPath = path;
        applyComputedPath(entityPos, rawTarget, searchTarget, System.nanoTime() - asyncStartNanos);
    }

    void handleQueuedRepath(double entityX, double entityY, double entityZ) {
        if (!needsRepath) {
            return;
        }

        needsRepath = false;

        if (state.targetPos != null) {
            executeBlocking(
                targets.entityAnchorPos(entityX, entityY, entityZ),
                targets.activeRawTargetPos(),
                featureControl.activePathfindingFeaturesOverride(),
                false
            );
        }
    }

    void advanceSegmentIfNeeded(double entityX, double entityY, double entityZ) {
        if (state.currentPath == null || !state.currentPath.isDone() || !shouldUsePlanner() || !planner.hasActiveRoute()) {
            return;
        }

        if (state.currentPath.isReached()) {
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
        if (state.targetPos == null) {
            return;
        }

        if (state.tickCount - state.lastPathComputeTick < runtimeConfig.getPathRecalculateIntervalInTicks()) {
            return;
        }

        if (targets.hasTargetMovedForRecalculation()) {
            if (tryReuseCurrentPathPrefix(state.targetPos)) {
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
            if (state.currentPath != null && !state.currentPath.isDone()) {
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

        state.currentPath = null;
        state.currentTerrain = null;
        progressTracker.clearStuckReplanHistory();
        resetProgressTracking();

        if (state.targetPos != null) {
            needsRepath = true;
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
        if (
            !activePathfindingFeatures().pathPrefixReuse()
                || state.currentPath == null
                || state.currentPath.isDone()
                || !state.currentPath.isReached()
        ) {
            return false;
        }

        var targetIndex = findRemainingPathNodeIndex(target);

        if (targetIndex < state.currentPath.getCurrentNodeIndex()) {
            return false;
        }

        var reusedNodes = new ArrayList<PathNode>(targetIndex - state.currentPath.getCurrentNodeIndex() + 1);

        for (var index = state.currentPath.getCurrentNodeIndex(); index <= targetIndex; index++) {
            reusedNodes.add(state.currentPath.getNode(index));
        }

        state.currentPath = new BLibPath(reusedNodes, true);
        state.currentTerrain = state.currentPath.getCurrentNode().getTerrainType();
        targets.recordPrefixReuse(target);
        state.lastPathComputeTick = state.tickCount;
        state.lastPathComputeNanos = 0L;
        markFeatureUsed(PathfindingFeature.PATH_PREFIX_REUSE);
        resetProgressTracking();

        return true;
    }

    private int findRemainingPathNodeIndex(BlockPos target) {
        if (state.currentPath == null) {
            return -1;
        }

        for (var index = state.currentPath.getCurrentNodeIndex(); index < state.currentPath.getNodeCount(); index++) {
            var node = state.currentPath.getNode(index);

            if (node.getX() == target.getX() && node.getY() == target.getY() && node.getZ() == target.getZ()) {
                return index;
            }
        }

        return -1;
    }

    private void advanceToNextSegment(double entityX, double entityY, double entityZ) {
        var entityPos = targets.entityAnchorPos(entityX, entityY, entityZ);
        var startNanos = System.nanoTime();

        preparePathFinder();
        markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
        this.state.currentPath = planner.computeNextSegment(level, entityPos);
        featureControl.markFeatureUsage(pathFinder.consumeFeatureUsageMask());
        var searchTarget = state.targetPos != null ? state.targetPos : entityPos;
        var rawTarget = state.rawTargetPos != null ? state.rawTargetPos : searchTarget;
        applyComputedPath(entityPos, rawTarget, searchTarget, System.nanoTime() - startNanos);

        if (state.currentPath == null) {
            clearPlanner();
        }
    }

    private Result<BLibPath, PathNavigationFailure> applyComputedPath(
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget,
        long pathComputeNanos
    ) {
        this.state.lastPathComputeNanos = pathComputeNanos;
        this.state.lastPathComputeTick = state.tickCount;
        resetProgressTracking();

        if (isUsablePath(state.currentPath)) {
            var startNode = state.currentPath.getCurrentNode();

            this.state.currentTerrain = startNode.getTerrainType();
            failureBackoff.resetFailureCooldown(state);

            return Result.ok(state.currentPath);
        } else {
            this.state.currentPath = null;
            this.state.currentTerrain = null;
            failureBackoff.recordFailure(state);

            return Result.err(new PathNavigationFailure.NoPathFound(entityPos, rawTarget, searchTarget));
        }
    }

    private @Nullable BLibPath joinPendingPath(
        CompletableFuture<@Nullable BLibPath> pendingPath,
        @Nullable CompletableFuture<Result<BLibPath, PathNavigationFailure>> pendingResult,
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget
    ) {
        try {
            return pendingPath.join();
        } catch (CancellationException exception) {
            featureControl.clearPendingPathfindingFeatures();
            completePendingResult(pendingResult, Result.err(new PathNavigationFailure.Superseded()));
            return null;
        } catch (CompletionException exception) {
            featureControl.clearPendingPathfindingFeatures();
            var failure = new PathNavigationFailure.SearchFailed(
                entityPos,
                rawTarget,
                searchTarget,
                exception.getCause() != null ? exception.getCause() : exception
            );
            state.currentPath = null;
            state.currentTerrain = null;
            failureBackoff.recordFailure(state);
            completePendingResult(pendingResult, Result.err(failure));

            return null;
        }
    }

    private void completeAsyncConstructionResult(
        CompletableFuture<Result<BLibPath, PathNavigationFailure>> resultFuture,
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget,
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
            resultFuture.complete(Result.err(new PathNavigationFailure.SearchFailed(entityPos, rawTarget, searchTarget, cause)));
            return;
        }

        if (isUsableAsyncPath(path)) {
            resultFuture.complete(Result.ok(path));
        } else {
            resultFuture.complete(Result.err(new PathNavigationFailure.NoPathFound(entityPos, rawTarget, searchTarget)));
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

    private PathNavigationFailure.InFailureCooldown failureCooldown(
        BlockPos entityPos,
        BlockPos rawTarget,
        BlockPos searchTarget
    ) {
        return new PathNavigationFailure.InFailureCooldown(
            entityPos,
            rawTarget,
            searchTarget,
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
        if (state.pendingPath != null) {
            state.pendingPath.cancel(false);
        }

        completePendingResult(state.pendingPathResult, Result.err(new PathNavigationFailure.Superseded()));
        clearPendingPathReferences();
        featureControl.clearPendingPathfindingFeatures();
    }

    private void clearPendingPathReferences() {
        state.pendingPath = null;
        state.pendingPathResult = null;
        pendingEntityPos = null;
        pendingRawTarget = null;
        pendingSearchTarget = null;
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
