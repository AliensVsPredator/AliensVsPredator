package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.just.core.functional.result.Result;

import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Internal lifecycle-backed implementation of the public {@link PathNavigationState} projection.
 */
final class PathNavigationStateComponent implements PathNavigationState {

    private final Supplier<@Nullable PathSearchSnapshot> lastSearchSnapshotSupplier;

    private final Supplier<@Nullable Vec3> currentTargetCenterSupplier;

    private final BooleanSupplier canOpenDoorsSupplier;

    private final LongSupplier cooldownClockSupplier;

    private PathNavigationLifecycle lifecycle = new PathNavigationLifecycle.Idle();

    private enum TransitionReason {
        START_PLANNING,
        COMPLETE_PLANNING,
        CANCEL_PLANNING,
        INVALIDATE_FOR_REPATH,
        STOP,
        UPDATE_STATE_DATA,
        REFRESH_TERMINAL
    }

    int tickCount;

    int lastPathComputeTick;

    int lastProgressTick;

    long lastPathComputeNanos;

    int consecutiveFailures;

    int failureCooldownTicks;

    long lastFailureTick;

    double lastEntityX;

    double lastEntityY;

    double lastEntityZ;

    boolean hasLastEntityPosition;

    float lastEntityWidth = 1.0f;

    float lastEntityHeight = 2.0f;

    PathNavigationStateComponent(
        Supplier<@Nullable PathSearchSnapshot> lastSearchSnapshotSupplier,
        Supplier<@Nullable Vec3> currentTargetCenterSupplier,
        BooleanSupplier canOpenDoorsSupplier,
        LongSupplier cooldownClockSupplier
    ) {
        this.lastSearchSnapshotSupplier = lastSearchSnapshotSupplier;
        this.currentTargetCenterSupplier = currentTargetCenterSupplier;
        this.canOpenDoorsSupplier = canOpenDoorsSupplier;
        this.cooldownClockSupplier = cooldownClockSupplier;
    }

    @Override
    public boolean isPathPending() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Planning planning -> true;
            case PathNavigationLifecycle.Idle idle -> false;
            case PathNavigationLifecycle.Navigating navigating -> false;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> false;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> false;
            case PathNavigationLifecycle.Reached reached -> false;
            case PathNavigationLifecycle.Exhausted exhausted -> false;
            case PathNavigationLifecycle.Failed failed -> false;
        };
    }

    @Override
    public int getTickCount() {
        return tickCount;
    }

    @Override
    public int getLastPathComputeTick() {
        return lastPathComputeTick;
    }

    @Override
    public int getLastProgressTick() {
        return lastProgressTick;
    }

    @Override
    public long getLastPathComputeNanos() {
        return lastPathComputeNanos;
    }

    @Override
    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshotSupplier.get();
    }

    @Override
    public @Nullable PathNode getCurrentNode() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().path().isDone()
                ? null
                : navigating.activePath().path().getCurrentNode();
            case PathNavigationLifecycle.Planning planning -> planning.activePath() != null
                && !planning.activePath().path().isDone()
                    ? planning.activePath().path().getCurrentNode()
                    : null;
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Reached reached -> null;
            case PathNavigationLifecycle.Exhausted exhausted -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };
    }

    @Override
    public @Nullable BlockPos getCurrentTargetPos() {
        var node = getCurrentNode();

        if (node == null) {
            return null;
        }

        return new BlockPos(node.getX(), node.getY(), node.getZ());
    }

    @Override
    public @Nullable Vec3 getCurrentTargetCenter() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().path().isDone()
                ? null
                : currentTargetCenterSupplier.get();
            case PathNavigationLifecycle.Planning planning -> planning.activePath() != null
                && !planning.activePath().path().isDone()
                    ? currentTargetCenterSupplier.get()
                    : null;
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Reached reached -> null;
            case PathNavigationLifecycle.Exhausted exhausted -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };
    }

    @Override
    public boolean isNavigating() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> !navigating.activePath().path().isDone();
            case PathNavigationLifecycle.Planning planning -> planning.activePath() != null
                && !planning.activePath().path().isDone();
            case PathNavigationLifecycle.Idle idle -> false;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> false;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> false;
            case PathNavigationLifecycle.Reached reached -> false;
            case PathNavigationLifecycle.Exhausted exhausted -> false;
            case PathNavigationLifecycle.Failed failed -> false;
        };
    }

    @Override
    public boolean isDone() {
        return !isNavigating();
    }

    @Override
    public @Nullable BLibPath getCurrentPath() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().path();
            case PathNavigationLifecycle.Planning planning -> planning.activePath() != null
                ? planning.activePath().path()
                : null;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> awaitingNextSegment.completedSegment().path();
            case PathNavigationLifecycle.Reached reached -> reached.completedPath().path();
            case PathNavigationLifecycle.Exhausted exhausted -> exhausted.completedPath().path();
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };
    }

    @Override
    public @Nullable TerrainType getCurrentTerrain() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().currentTerrain();
            case PathNavigationLifecycle.Planning planning -> planning.activePath() != null
                ? planning.activePath().currentTerrain()
                : null;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> awaitingNextSegment.completedSegment().currentTerrain();
            case PathNavigationLifecycle.Reached reached -> reached.completedPath().currentTerrain();
            case PathNavigationLifecycle.Exhausted exhausted -> exhausted.completedPath().currentTerrain();
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };
    }

    @Override
    public boolean canOpenDoors() {
        return canOpenDoorsSupplier.getAsBoolean();
    }

    @Override
    public @Nullable BlockPos getTargetPos() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Planning planning -> planning.request().searchTarget();
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().request().searchTarget();
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> awaitingNextSegment.completedSegment().request().searchTarget();
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> awaitingRepath.request().searchTarget();
            case PathNavigationLifecycle.Reached reached -> reached.completedPath().request().searchTarget();
            case PathNavigationLifecycle.Exhausted exhausted -> exhausted.completedPath().request().searchTarget();
            case PathNavigationLifecycle.Failed failed -> failed.request().searchTarget();
            case PathNavigationLifecycle.Idle idle -> null;
        };
    }

    @Override
    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    @Override
    public int getFailureCooldownRemainingTicks() {
        if (consecutiveFailures == 0) {
            return 0;
        }

        return (int) Math.max(0, failureCooldownTicks - (cooldownClockSupplier.getAsLong() - lastFailureTick));
    }

    @Nullable PathNavigationLifecycle.Planning pendingPlanning() {
        return lifecycle instanceof PathNavigationLifecycle.Planning planning ? planning : null;
    }

    @Nullable PathNavigationLifecycle.RequestContext currentRequest() {
        return switch (lifecycle) {
            case PathNavigationLifecycle.Planning planning -> planning.request();
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath().request();
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> awaitingNextSegment.completedSegment().request();
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> awaitingRepath.request();
            case PathNavigationLifecycle.Reached reached -> reached.completedPath().request();
            case PathNavigationLifecycle.Exhausted exhausted -> exhausted.completedPath().request();
            case PathNavigationLifecycle.Failed failed -> failed.request();
            case PathNavigationLifecycle.Idle idle -> null;
        };
    }

    @Nullable BlockPos currentRawTarget() {
        var request = currentRequest();

        return request != null ? request.rawTarget() : null;
    }

    @Nullable BlockPos currentSearchTarget() {
        var request = currentRequest();

        return request != null ? request.searchTarget() : null;
    }

    @Nullable PathNavigationLifecycle.ActivePathContext activePathContext() {
        var activePath = switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath();
            case PathNavigationLifecycle.Planning planning -> planning.activePath();
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Reached reached -> null;
            case PathNavigationLifecycle.Exhausted exhausted -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };

        if (activePath == null || activePath.path().isDone()) {
            return null;
        }

        return activePath;
    }

    @Nullable BLibPath activePath() {
        var activePath = activePathContext();

        return activePath != null ? activePath.path() : null;
    }

    @Nullable BLibPath currentPath() {
        var currentPathContext = currentActivePathContext();

        return currentPathContext != null ? currentPathContext.path() : null;
    }

    @Nullable TerrainType currentTerrain() {
        var currentPathContext = currentActivePathContext();

        return currentPathContext != null ? currentPathContext.currentTerrain() : null;
    }

    boolean hasActivePath() {
        return activePath() != null;
    }

    PathNavigationLifecycle.RequestContext requestContext(
        BlockPos entityStart,
        BlockPos rawTarget,
        BlockPos searchTarget
    ) {
        return new PathNavigationLifecycle.RequestContext(entityStart, rawTarget, searchTarget);
    }

    @Nullable PathNavigationLifecycle.ActivePathContext currentActivePathContext() {
        var activePath = switch (lifecycle) {
            case PathNavigationLifecycle.Navigating navigating -> navigating.activePath();
            case PathNavigationLifecycle.Planning planning -> planning.activePath();
            case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> awaitingNextSegment.completedSegment();
            case PathNavigationLifecycle.Reached reached -> reached.completedPath();
            case PathNavigationLifecycle.Exhausted exhausted -> exhausted.completedPath();
            case PathNavigationLifecycle.Idle idle -> null;
            case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> null;
            case PathNavigationLifecycle.Failed failed -> null;
        };

        return activePath;
    }

    void enterPlanning(
        PathNavigationLifecycle.RequestContext request,
        @Nullable CompletableFuture<@Nullable BLibPath> pendingPath,
        @Nullable CompletableFuture<Result<BLibPath, PathNavigationFailure>> pendingPathResult,
        long startNanos,
        @Nullable PathNavigationLifecycle.ActivePathContext activePath
    ) {
        transitionTo(
            new PathNavigationLifecycle.Planning(
                request,
                pendingPath,
                pendingPathResult,
                startNanos,
                activePath
            ),
            TransitionReason.START_PLANNING
        );
    }

    void completePlanningWithNavigating(
        PathNavigationLifecycle.RequestContext request,
        BLibPath path,
        TerrainType terrain
    ) {
        var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);

        transitionTo(new PathNavigationLifecycle.Navigating(activePath), TransitionReason.COMPLETE_PLANNING);
    }

    void replaceNavigatingPath(
        PathNavigationLifecycle.RequestContext request,
        BLibPath path,
        TerrainType terrain
    ) {
        var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);

        transitionTo(new PathNavigationLifecycle.Navigating(activePath), TransitionReason.UPDATE_STATE_DATA);
    }

    void completePlanningWithAwaitingNextSegment(
        PathNavigationLifecycle.RequestContext request,
        BLibPath path,
        TerrainType terrain
    ) {
        var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);

        transitionTo(new PathNavigationLifecycle.AwaitingNextSegment(activePath), TransitionReason.COMPLETE_PLANNING);
    }

    void enterAwaitingRepath(PathNavigationLifecycle.RequestContext request) {
        transitionTo(new PathNavigationLifecycle.AwaitingRepath(request), TransitionReason.INVALIDATE_FOR_REPATH);
    }

    void completePlanningWithReached(
        PathNavigationLifecycle.RequestContext request,
        BLibPath path,
        TerrainType terrain
    ) {
        var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);

        transitionTo(new PathNavigationLifecycle.Reached(activePath), TransitionReason.COMPLETE_PLANNING);
    }

    void completePlanningWithExhausted(
        PathNavigationLifecycle.RequestContext request,
        BLibPath path,
        TerrainType terrain
    ) {
        var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);

        transitionTo(new PathNavigationLifecycle.Exhausted(activePath), TransitionReason.COMPLETE_PLANNING);
    }

    void completePlanningWithFailed(PathNavigationLifecycle.RequestContext request, PathNavigationFailure failure) {
        transitionTo(new PathNavigationLifecycle.Failed(request, failure), TransitionReason.COMPLETE_PLANNING);
    }

    void enterIdle() {
        transitionTo(new PathNavigationLifecycle.Idle(), TransitionReason.STOP);
    }

    void clearPendingLifecycleData() {
        if (lifecycle instanceof PathNavigationLifecycle.Planning planning) {
            if (planning.activePath() != null) {
                var activePath = planning.activePath();

                if (activePath.path().isDone() && activePath.path().isReached()) {
                    transitionTo(new PathNavigationLifecycle.Reached(activePath), TransitionReason.CANCEL_PLANNING);
                } else if (activePath.path().isDone()) {
                    transitionTo(new PathNavigationLifecycle.Exhausted(activePath), TransitionReason.CANCEL_PLANNING);
                } else {
                    transitionTo(new PathNavigationLifecycle.Navigating(activePath), TransitionReason.CANCEL_PLANNING);
                }
            } else {
                transitionTo(new PathNavigationLifecycle.Idle(), TransitionReason.CANCEL_PLANNING);
            }
        }
    }

    void updateLifecycleTarget(BlockPos rawTarget, BlockPos searchTarget) {
        if (lifecycle instanceof PathNavigationLifecycle.Idle) {
            return;
        }

        var updatedRequest = requestForTargetUpdate(rawTarget, searchTarget);
        transitionTo(
            switch (lifecycle) {
                case PathNavigationLifecycle.Planning planning -> new PathNavigationLifecycle.Planning(
                    updatedRequest,
                    planning.pendingPath(),
                    planning.resultFuture(),
                    planning.startNanos(),
                    planning.activePath()
                );
                case PathNavigationLifecycle.Navigating navigating -> new PathNavigationLifecycle.Navigating(
                    navigating.activePath().withRequest(updatedRequest)
                );
                case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> new PathNavigationLifecycle.AwaitingNextSegment(
                    awaitingNextSegment.completedSegment().withRequest(updatedRequest)
                );
                case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> new PathNavigationLifecycle.AwaitingRepath(updatedRequest);
                case PathNavigationLifecycle.Reached reached -> new PathNavigationLifecycle.Reached(
                    reached.completedPath().withRequest(updatedRequest)
                );
                case PathNavigationLifecycle.Exhausted exhausted -> new PathNavigationLifecycle.Exhausted(
                    exhausted.completedPath().withRequest(updatedRequest)
                );
                case PathNavigationLifecycle.Failed failed -> new PathNavigationLifecycle.Failed(updatedRequest, failed.failure());
                case PathNavigationLifecycle.Idle idle -> lifecycle;
            },
            TransitionReason.UPDATE_STATE_DATA
        );
    }

    void updateLifecycleTerrain(TerrainType terrain) {
        transitionTo(
            switch (lifecycle) {
                case PathNavigationLifecycle.Planning planning -> new PathNavigationLifecycle.Planning(
                    planning.request(),
                    planning.pendingPath(),
                    planning.resultFuture(),
                    planning.startNanos(),
                    planning.activePath() != null
                        ? planning.activePath().withPath(planning.activePath().path(), terrain)
                        : null
                );
                case PathNavigationLifecycle.Navigating navigating -> new PathNavigationLifecycle.Navigating(
                    navigating.activePath().withPath(navigating.activePath().path(), terrain)
                );
                case PathNavigationLifecycle.AwaitingNextSegment awaitingNextSegment -> new PathNavigationLifecycle.AwaitingNextSegment(
                    awaitingNextSegment.completedSegment().withPath(awaitingNextSegment.completedSegment().path(), terrain)
                );
                case PathNavigationLifecycle.Reached reached -> new PathNavigationLifecycle.Reached(
                    reached.completedPath().withPath(reached.completedPath().path(), terrain)
                );
                case PathNavigationLifecycle.Exhausted exhausted -> new PathNavigationLifecycle.Exhausted(
                    exhausted.completedPath().withPath(exhausted.completedPath().path(), terrain)
                );
                case PathNavigationLifecycle.Idle idle -> lifecycle;
                case PathNavigationLifecycle.AwaitingRepath awaitingRepath -> lifecycle;
                case PathNavigationLifecycle.Failed failed -> lifecycle;
            },
            TransitionReason.UPDATE_STATE_DATA
        );
    }

    void refreshTerminalLifecycle() {
        if (lifecycle instanceof PathNavigationLifecycle.Planning) {
            return;
        }

        var currentPath = currentActivePathContext();

        if (currentPath == null || !currentPath.path().isDone()) {
            return;
        }

        var path = currentPath.path();
        var request = currentPath.request();
        var terrain = currentPath.currentTerrain();

        if (path.isReached()) {
            var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);
            transitionTo(new PathNavigationLifecycle.Reached(activePath), TransitionReason.REFRESH_TERMINAL);
        } else {
            var activePath = new PathNavigationLifecycle.ActivePathContext(request, path, terrain);
            transitionTo(new PathNavigationLifecycle.Exhausted(activePath), TransitionReason.REFRESH_TERMINAL);
        }
    }

    void requirePlanningFor(PathNavigationLifecycle.RequestContext request) {
        if (!(lifecycle instanceof PathNavigationLifecycle.Planning planning) || !planning.request().equals(request)) {
            throw new IllegalStateException(
                "Path result can only be applied while planning for the same request"
            );
        }
    }

    private PathNavigationLifecycle.RequestContext requestForTargetUpdate(BlockPos rawTarget, BlockPos searchTarget) {
        var currentRequest = currentRequest();

        if (currentRequest != null) {
            return currentRequest.withTargets(rawTarget, searchTarget);
        }

        return requestContext(
            hasLastEntityPosition ? BlockPos.containing(lastEntityX, lastEntityY, lastEntityZ) : BlockPos.ZERO,
            rawTarget,
            searchTarget
        );
    }

    private void transitionTo(PathNavigationLifecycle next, TransitionReason reason) {
        if (!isLegalTransition(lifecycle, next, reason)) {
            throw new IllegalStateException(
                "Illegal path navigation lifecycle transition from "
                    + lifecycle.getClass().getSimpleName()
                    + " to "
                    + next.getClass().getSimpleName()
                    + " via "
                    + reason
            );
        }

        this.lifecycle = next;
    }

    private boolean isLegalTransition(
        PathNavigationLifecycle from,
        PathNavigationLifecycle to,
        TransitionReason reason
    ) {
        return switch (reason) {
            case START_PLANNING ->
                // New requests, queued replans, and segment computations enter Planning before they produce a result.
                to instanceof PathNavigationLifecycle.Planning
                    // A Planning state must be cancelled or completed before another plan can start.
                    && !(from instanceof PathNavigationLifecycle.Planning);
            case COMPLETE_PLANNING ->
                // Only a Planning state is allowed to publish a computed path result.
                from instanceof PathNavigationLifecycle.Planning
                    && (
                        // The computed path has a current node that can be followed immediately.
                        to instanceof PathNavigationLifecycle.Navigating
                            // A segmented route completed this segment but still has more route to compute.
                            || to instanceof PathNavigationLifecycle.AwaitingNextSegment
                            // The computed path is already complete and reached the requested target.
                            || to instanceof PathNavigationLifecycle.Reached
                            // The computed path is complete but did not reach the requested target.
                            || to instanceof PathNavigationLifecycle.Exhausted
                            // The plan failed, hit cooldown, or produced no usable path.
                            || to instanceof PathNavigationLifecycle.Failed
                    );
            case CANCEL_PLANNING ->
                // Only an interrupted Planning state can be restored after cancellation.
                from instanceof PathNavigationLifecycle.Planning
                    && (
                        // Cancelling with no previous active path returns the navigator to rest.
                        to instanceof PathNavigationLifecycle.Idle
                            // Cancelling with an unfinished previous path resumes that path.
                            || to instanceof PathNavigationLifecycle.Navigating
                            // Cancelling with a completed previous path that reached its target preserves completion.
                            || to instanceof PathNavigationLifecycle.Reached
                            // Cancelling with a completed previous path that did not reach its target preserves exhaustion.
                            || to instanceof PathNavigationLifecycle.Exhausted
                    );
            case INVALIDATE_FOR_REPATH ->
                (
                    // A pending plan can be superseded and replaced by a queued replan.
                    from instanceof PathNavigationLifecycle.Planning
                        // An active path can be invalidated by feature, runtime, target, or progress changes.
                        || from instanceof PathNavigationLifecycle.Navigating
                        // A completed segment can be invalidated before the next segment is computed.
                        || from instanceof PathNavigationLifecycle.AwaitingNextSegment
                        // A queued replan can be refreshed with newer request data.
                        || from instanceof PathNavigationLifecycle.AwaitingRepath
                        // A reached path can become stale after target, feature, or runtime changes.
                        || from instanceof PathNavigationLifecycle.Reached
                        // An exhausted path can be retried after target, feature, or runtime changes.
                        || from instanceof PathNavigationLifecycle.Exhausted
                        // A failed request can be retried after explicit invalidation.
                        || from instanceof PathNavigationLifecycle.Failed
                )
                    // Invalidation stores the latest request until the next tick handles the replan.
                    && to instanceof PathNavigationLifecycle.AwaitingRepath;
            case STOP ->
                // Stop clears any lifecycle state and returns the navigator to rest.
                to instanceof PathNavigationLifecycle.Idle;
            case UPDATE_STATE_DATA ->
                // Same-state transitions only replace the data carried by the current lifecycle state.
                from.getClass() == to.getClass();
            case REFRESH_TERMINAL ->
                (
                    // An active path can finish as waypoints advance.
                    from instanceof PathNavigationLifecycle.Navigating
                        // A segment waiting for route continuation can be closed out if the route becomes terminal.
                        || from instanceof PathNavigationLifecycle.AwaitingNextSegment
                        // A reached state can be refreshed after its path has already completed.
                        || from instanceof PathNavigationLifecycle.Reached
                        // An exhausted state can be refreshed after its path has already completed.
                        || from instanceof PathNavigationLifecycle.Exhausted
                )
                    && (
                        // Terminal refresh preserves or discovers that the target was reached.
                        to instanceof PathNavigationLifecycle.Reached
                            // Terminal refresh preserves or discovers that the path ended before the target.
                            || to instanceof PathNavigationLifecycle.Exhausted
                    );
        };
    }
}
