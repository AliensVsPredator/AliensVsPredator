package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.node.PathPosture;

/**
 * Owns active-path progress measurement and repeated-stuck-edge detection.
 */
final class PathNavigationProgressTracker {

    private static final double PROGRESS_DISTANCE_EPSILON_SQUARED = 0.25;

    private final PathNavigationStateComponent state;

    private final PathNavigationWaypointFollower waypointFollower;

    private final IntSupplier stuckTimeoutSupplier;

    private final Supplier<PathfindingFeatures> activeFeaturesSupplier;

    private final Consumer<PathfindingFeature> featureUsageConsumer;

    private double lastDistanceToCurrentNode;

    private double lastDistanceToNextNode;

    private int lastObservedNodeIndex = -1;

    private @Nullable PathEdgeKey lastStuckReplannedEdge;

    PathNavigationProgressTracker(
        PathNavigationStateComponent state,
        PathNavigationWaypointFollower waypointFollower,
        IntSupplier stuckTimeoutSupplier,
        Supplier<PathfindingFeatures> activeFeaturesSupplier,
        Consumer<PathfindingFeature> featureUsageConsumer
    ) {
        this.state = state;
        this.waypointFollower = waypointFollower;
        this.stuckTimeoutSupplier = stuckTimeoutSupplier;
        this.activeFeaturesSupplier = activeFeaturesSupplier;
        this.featureUsageConsumer = featureUsageConsumer;
    }

    StuckDecision detectStuck(
        BlockPos entityAnchorPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        if (state.targetPos == null || state.currentPath == null || state.currentPath.isDone()) {
            return StuckDecision.NONE;
        }

        var progressed = observePathProgress(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
        var ticksSinceProgress = state.tickCount - state.lastProgressTick;

        if (!progressed && ticksSinceProgress >= stuckTimeoutSupplier.getAsInt()) {
            return handleStuckEdge(entityAnchorPos);
        }

        return StuckDecision.NONE;
    }

    void markProgress() {
        state.lastProgressTick = state.tickCount;
        lastDistanceToCurrentNode = Double.MAX_VALUE;
        lastDistanceToNextNode = Double.MAX_VALUE;
        lastObservedNodeIndex = state.currentPath != null ? state.currentPath.getCurrentNodeIndex() : -1;
    }

    void resetProgressTracking() {
        state.lastProgressTick = state.tickCount;
        lastDistanceToCurrentNode = Double.MAX_VALUE;
        lastDistanceToNextNode = Double.MAX_VALUE;
        lastObservedNodeIndex = -1;
    }

    void clearStuckReplanHistory() {
        lastStuckReplannedEdge = null;
    }

    private boolean observePathProgress(
        BlockPos entityAnchorPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var currentIndex = state.currentPath.getCurrentNodeIndex();
        var currentCenter = waypointFollower.currentNodeTargetCenter(state.currentPath.getCurrentNode(), entityWidth, entityHeight);
        var distanceToCurrent = distanceSquared(entityX, entityY, entityZ, currentCenter);
        var distanceToNext = Double.MAX_VALUE;

        if (currentIndex + 1 < state.currentPath.getNodeCount()) {
            distanceToNext = distanceSquared(
                entityX,
                entityY,
                entityZ,
                waypointFollower.nodeTargetCenter(state.currentPath.getNode(currentIndex + 1), entityWidth, entityHeight)
            );
        }

        var progressed = currentIndex != lastObservedNodeIndex
            || distanceToCurrent < lastDistanceToCurrentNode - PROGRESS_DISTANCE_EPSILON_SQUARED
            || distanceToNext < lastDistanceToNextNode - PROGRESS_DISTANCE_EPSILON_SQUARED;

        if (progressed) {
            state.lastProgressTick = state.tickCount;
            lastObservedNodeIndex = currentIndex;
            lastDistanceToCurrentNode = distanceToCurrent;
            lastDistanceToNextNode = distanceToNext;

            var edge = currentEdgeKey(entityAnchorPos);
            if (lastStuckReplannedEdge != null && edge != null && !lastStuckReplannedEdge.equals(edge)) {
                lastStuckReplannedEdge = null;
            }
        }

        return progressed;
    }

    private StuckDecision handleStuckEdge(BlockPos entityAnchorPos) {
        if (!activePathfindingFeatures().stuckReplan()) {
            return StuckDecision.STOP;
        }

        var edge = currentEdgeKey(entityAnchorPos);

        if (edge != null && edge.equals(lastStuckReplannedEdge)) {
            return StuckDecision.STOP;
        }

        lastStuckReplannedEdge = edge;

        if (state.targetPos == null) {
            return StuckDecision.STOP;
        }

        markFeatureUsed(PathfindingFeature.STUCK_REPLAN);

        return StuckDecision.REPLAN;
    }

    private @Nullable PathEdgeKey currentEdgeKey(BlockPos entityAnchorPos) {
        if (state.currentPath == null || state.currentPath.isDone() || state.targetPos == null) {
            return null;
        }

        var currentIndex = state.currentPath.getCurrentNodeIndex();
        var to = state.currentPath.getCurrentNode();
        var from = currentIndex > 0 ? state.currentPath.getNode(currentIndex - 1) : null;
        var fromX = from != null ? from.getX() : entityAnchorPos.getX();
        var fromY = from != null ? from.getY() : entityAnchorPos.getY();
        var fromZ = from != null ? from.getZ() : entityAnchorPos.getZ();

        return new PathEdgeKey(
            fromX,
            fromY,
            fromZ,
            from != null ? from.getPosture() : PathPosture.STANDING,
            to.getX(),
            to.getY(),
            to.getZ(),
            to.getPosture(),
            state.targetPos.getX(),
            state.targetPos.getY(),
            state.targetPos.getZ()
        );
    }

    private double distanceSquared(double x, double y, double z, Vec3 target) {
        var dx = target.x - x;
        var dy = target.y - y;
        var dz = target.z - z;

        return dx * dx + dy * dy + dz * dz;
    }

    private PathfindingFeatures activePathfindingFeatures() {
        return activeFeaturesSupplier.get();
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureUsageConsumer.accept(feature);
    }

    enum StuckDecision {
        NONE,
        STOP,
        REPLAN
    }

    private record PathEdgeKey(
        int fromX,
        int fromY,
        int fromZ,
        PathPosture fromPosture,
        int toX,
        int toY,
        int toZ,
        PathPosture toPosture,
        int targetX,
        int targetY,
        int targetZ
    ) {}
}
