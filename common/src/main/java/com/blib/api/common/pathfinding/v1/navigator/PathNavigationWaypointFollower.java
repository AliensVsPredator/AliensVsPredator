package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Owns waypoint target selection, waypoint advancement, and path smoothing for an active navigation path.
 */
final class PathNavigationWaypointFollower {

    private static final double WIDE_FOOTPRINT_REACH_XZ = 0.45;

    private static final double WAYPOINT_REACH_Y = 0.45;

    private static final double DESCENDING_STAIR_EDGE_REACH_PROGRESS = 0.5;

    private static final double DROP_ENTRY_MIN_REACH_XZ = 0.75;

    private static final double SHAPE_WAYPOINT_SAMPLE_STEP = 0.05;

    private static final int ANY_ANGLE_SMOOTHING_MAX_LOOKAHEAD_NODES = 16;

    private static final double ANY_ANGLE_SMOOTHING_SAMPLE_INTERVAL = 0.25;

    private static final double COLLISION_EPSILON = 1.0E-7;

    private final PathNavigatorConfig config;

    private final PathNavigationStateComponent state;

    private final PathNavigationSpaceQuery spaceQuery;

    private final Supplier<PathfindingFeatures> activeFeaturesSupplier;

    private final Consumer<PathfindingFeature> featureUsageConsumer;

    private final Runnable progressMarker;

    private final BiConsumer<@Nullable TerrainType, TerrainType> transitionConsumer;

    private int dropEntryNodeIndex = -1;

    private boolean dropEntryReached;

    private final Set<AnyAngleSmoothingKey> anyAngleSmoothingFailures = new HashSet<>();

    PathNavigationWaypointFollower(
        PathNavigatorConfig config,
        PathNavigationStateComponent state,
        PathNavigationSpaceQuery spaceQuery,
        Supplier<PathfindingFeatures> activeFeaturesSupplier,
        Consumer<PathfindingFeature> featureUsageConsumer,
        Runnable progressMarker,
        BiConsumer<@Nullable TerrainType, TerrainType> transitionConsumer
    ) {
        this.config = config;
        this.state = state;
        this.spaceQuery = spaceQuery;
        this.activeFeaturesSupplier = activeFeaturesSupplier;
        this.featureUsageConsumer = featureUsageConsumer;
        this.progressMarker = progressMarker;
        this.transitionConsumer = transitionConsumer;
    }

    @Nullable Vec3 resolveCurrentTargetCenter() {
        var path = currentPath();

        if (path == null || path.isDone()) {
            return null;
        }

        return currentNodeTargetCenter(path.getCurrentNode(), state.lastEntityWidth, state.lastEntityHeight);
    }

    void advanceWaypoints(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var path = currentPath();

        if (path == null) {
            return;
        }

        var reachXZ = waypointReachXZ(entityWidth);
        var reachY = waypointReachY(entityHeight);

        while (!path.isDone()) {
            var waypoint = path.getCurrentNode();
            var waypointCenter = currentNodeTargetCenter(waypoint, entityWidth, entityHeight);
            var awaitingDropEntry = isCurrentDropEntryWaypointPending(waypoint);
            var activeReachXZ = awaitingDropEntry ? dropEntryReachXZ(entityWidth) : reachXZ;

            var dx = Math.abs(waypointCenter.x - entityX);
            var dy = Math.abs(waypointCenter.y - entityY);
            var dz = Math.abs(waypointCenter.z - entityZ);

            var withinVerticalReach = awaitingDropEntry ? entityY <= waypointCenter.y + reachY : dy <= reachY;
            if (
                !awaitingDropEntry
                    && !withinVerticalReach
                    && isDescendingSteppedFootprintWaypointWithinReach(path.getCurrentNodeIndex(), entityY, reachY)
            ) {
                withinVerticalReach = true;
                markFeatureUsed(PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT);
            }

            var withinReach = dx <= activeReachXZ && withinVerticalReach && dz <= activeReachXZ;
            var enteredDropShaft = awaitingDropEntry
                && entityY < waypointCenter.y - reachY
                && dx <= entityWidth
                && dz <= entityWidth;
            var descendingStairEdgeReached = !withinReach
                && !awaitingDropEntry
                && isDescendingStairEdgeWaypointReached(
                    path.getCurrentNodeIndex(),
                    entityX,
                    entityY,
                    entityZ,
                    entityWidth,
                    entityHeight,
                    reachY
                );
            var skippedAhead = !withinReach
                && !descendingStairEdgeReached
                && activePathfindingFeatures().pathSkipAhead()
                && shouldSkipToNextNode(entityX, entityY, entityZ, entityWidth, entityHeight);

            if (skippedAhead) {
                markFeatureUsed(PathfindingFeature.PATH_SKIP_AHEAD);
            }

            if (descendingStairEdgeReached) {
                markFeatureUsed(PathfindingFeature.DESCENDING_STAIR_EDGE_REACH);
            }

            var shouldAdvance = withinReach || enteredDropShaft || descendingStairEdgeReached || skippedAhead;

            if (!shouldAdvance) {
                break;
            }

            if (awaitingDropEntry) {
                dropEntryReached = true;
                markFeatureUsed(PathfindingFeature.DROP_DOWN_OPENINGS);
                markProgress();
                continue;
            }

            var previousTerrain = currentTerrain();

            path.advance();
            markProgress();

            if (!path.isDone()) {
                var nextNode = path.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();

                if (newTerrain != previousTerrain) {
                    transitionConsumer.accept(previousTerrain, newTerrain);
                    state.updateLifecycleTerrain(newTerrain);
                    break;
                }
            }
        }

        smoothAnyAngleWaypoint(entityX, entityY, entityZ, entityWidth, entityHeight);
    }

    Vec3 currentNodeTargetCenter(PathNode node, float entityWidth, float entityHeight) {
        syncDropEntryTracking(node);

        if (isCurrentDropEntryWaypointPending(node)) {
            markFeatureUsed(PathfindingFeature.DROP_DOWN_OPENINGS);
            return new Vec3(node.getDropEntryX(), node.getDropEntryY(), node.getDropEntryZ());
        }

        return nodeTargetCenter(node, entityWidth, entityHeight);
    }

    Vec3 nodeTargetCenter(PathNode node, float entityWidth, float entityHeight) {
        var center = nodeCenter(node);

        if (!activePathfindingFeatures().collisionShapeWaypoints() || node.getTerrainType() == TerrainType.WATER) {
            return center;
        }

        // Multi-cell footprint nodes use a different anchor model; keep those on the stable footprint center.
        if (config.getEvaluatorConfig().getEntityWidth() != 1) {
            return center;
        }

        var adjusted = shapeAwareNodeCenter(node, entityWidth, entityHeight);

        if (adjusted != null && !samePosition(adjusted, center)) {
            markFeatureUsed(PathfindingFeature.COLLISION_SHAPE_WAYPOINTS);
        }

        return adjusted != null ? adjusted : center;
    }

    void reset() {
        dropEntryNodeIndex = -1;
        dropEntryReached = false;
        anyAngleSmoothingFailures.clear();
    }

    private void smoothAnyAngleWaypoint(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var path = currentPath();

        if (path == null || path.isDone()) {
            return;
        }

        var smoothingFeature = anyAngleSmoothingFeatureFor(path.getCurrentNode());
        if (smoothingFeature == null) {
            return;
        }

        var currentIndex = path.getCurrentNodeIndex();
        var targetIndex = findAnyAngleSmoothingTargetIndex(entityX, entityY, entityZ, entityWidth, entityHeight);

        if (targetIndex <= currentIndex) {
            return;
        }

        markAnyAngleSmoothingUsed(currentIndex, targetIndex, smoothingFeature);

        var previousTerrain = currentTerrain();

        while (path.getCurrentNodeIndex() < targetIndex && !path.isDone()) {
            path.advance();
        }

        markProgress();

        if (!path.isDone()) {
            var newTerrain = path.getCurrentNode().getTerrainType();

            if (newTerrain != previousTerrain) {
                transitionConsumer.accept(previousTerrain, newTerrain);
                state.updateLifecycleTerrain(newTerrain);
            }
        }
    }

    private @Nullable PathfindingFeature anyAngleSmoothingFeatureFor(PathNode node) {
        var activeFeatures = activePathfindingFeatures();

        return switch (node.getTerrainType()) {
            case GROUND -> activeFeatures.anyAngleSmoothing() ? PathfindingFeature.ANY_ANGLE_SMOOTHING : null;
            case WATER -> activeFeatures.waterPathfinding() && activeFeatures.waterAnyAngleSmoothing()
                ? PathfindingFeature.WATER_ANY_ANGLE_SMOOTHING
                : null;
        };
    }

    private void markAnyAngleSmoothingUsed(int fromIndex, int toIndex, PathfindingFeature smoothingFeature) {
        markFeatureUsed(smoothingFeature);

        if (smoothingFeature != PathfindingFeature.WATER_ANY_ANGLE_SMOOTHING) {
            return;
        }

        markFeatureUsed(PathfindingFeature.WATER_PATHFINDING);

        var from = currentPath().getNode(fromIndex);
        var to = currentPath().getNode(toIndex);

        if (from.getY() == to.getY()) {
            return;
        }

        if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
            markFeatureUsed(PathfindingFeature.WATER_SLOPE_SWIM);
        } else {
            markFeatureUsed(PathfindingFeature.WATER_VERTICAL_SWIM);
        }
    }

    private int findAnyAngleSmoothingTargetIndex(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var currentIndex = currentPath().getCurrentNodeIndex();
        var maxIndex = Math.min(
            currentPath().getNodeCount() - 1,
            currentIndex + ANY_ANGLE_SMOOTHING_MAX_LOOKAHEAD_NODES
        );

        for (var targetIndex = maxIndex; targetIndex > currentIndex; targetIndex--) {
            if (!isSmoothableNodeRange(currentIndex, targetIndex)) {
                continue;
            }

            var targetCenter = nodeTargetCenter(currentPath().getNode(targetIndex), entityWidth, entityHeight);
            var terrain = currentPath().getNode(currentIndex).getTerrainType();

            var cacheKey = anyAngleSmoothingKey(
                currentIndex,
                targetIndex,
                entityX,
                entityY,
                entityZ,
                targetCenter,
                entityWidth,
                entityHeight,
                terrain
            );

            if (cacheKey != null && anyAngleSmoothingFailures.contains(cacheKey)) {
                markFeatureUsed(PathfindingFeature.ANY_ANGLE_SMOOTHING_CACHE);
                continue;
            }

            if (canTraverseDirectly(entityX, entityY, entityZ, targetCenter, entityWidth, entityHeight, terrain)) {
                return targetIndex;
            }

            if (cacheKey != null) {
                markFeatureUsed(PathfindingFeature.ANY_ANGLE_SMOOTHING_CACHE);
                anyAngleSmoothingFailures.add(cacheKey);
            }
        }

        return currentIndex;
    }

    private @Nullable AnyAngleSmoothingKey anyAngleSmoothingKey(
        int currentIndex,
        int targetIndex,
        double entityX,
        double entityY,
        double entityZ,
        Vec3 targetCenter,
        float entityWidth,
        float entityHeight,
        TerrainType terrain
    ) {
        if (!activePathfindingFeatures().anyAngleSmoothingCache()) {
            return null;
        }

        return new AnyAngleSmoothingKey(
            currentIndex,
            targetIndex,
            Double.doubleToLongBits(entityX),
            Double.doubleToLongBits(entityY),
            Double.doubleToLongBits(entityZ),
            Double.doubleToLongBits(targetCenter.x),
            Double.doubleToLongBits(targetCenter.y),
            Double.doubleToLongBits(targetCenter.z),
            Float.floatToIntBits(entityWidth),
            Float.floatToIntBits(entityHeight),
            terrain
        );
    }

    private boolean isSmoothableNodeRange(int fromIndex, int toIndex) {
        var from = currentPath().getNode(fromIndex);

        if (from.hasDropEntryWaypoint()) {
            return false;
        }

        return switch (from.getTerrainType()) {
            case GROUND -> isSmoothableGroundNodeRange(fromIndex, toIndex);
            case WATER -> isSmoothableWaterNodeRange(fromIndex, toIndex);
        };
    }

    private boolean isSmoothableGroundNodeRange(int fromIndex, int toIndex) {
        var from = currentPath().getNode(fromIndex);
        var y = from.getY();
        var posture = from.getPosture();

        for (var index = fromIndex + 1; index <= toIndex; index++) {
            var node = currentPath().getNode(index);

            if (
                node.hasDropEntryWaypoint()
                    || node.getY() != y
                    || node.getTerrainType() != TerrainType.GROUND
                    || node.getPosture() != posture
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean isSmoothableWaterNodeRange(int fromIndex, int toIndex) {
        var activeFeatures = activePathfindingFeatures();

        if (!activeFeatures.waterPathfinding() || !activeFeatures.waterAnyAngleSmoothing()) {
            return false;
        }

        var from = currentPath().getNode(fromIndex);
        var posture = from.getPosture();

        for (var index = fromIndex + 1; index <= toIndex; index++) {
            var node = currentPath().getNode(index);

            if (
                node.hasDropEntryWaypoint()
                    || node.getTerrainType() != TerrainType.WATER
                    || node.getPosture() != posture
            ) {
                return false;
            }
        }

        return isWaterAnyAngleMovementAllowed(from, currentPath().getNode(toIndex));
    }

    private boolean isWaterAnyAngleMovementAllowed(PathNode from, PathNode to) {
        var activeFeatures = activePathfindingFeatures();

        if (from.getY() == to.getY()) {
            return true;
        }

        if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
            return activeFeatures.waterSlopeSwim();
        }

        return activeFeatures.waterVerticalSwim();
    }

    private boolean canTraverseDirectly(
        double entityX,
        double entityY,
        double entityZ,
        Vec3 targetCenter,
        float entityWidth,
        float entityHeight,
        TerrainType terrain
    ) {
        if (entityWidth <= 0.0f || entityHeight <= 0.0f) {
            return false;
        }

        var dx = targetCenter.x - entityX;
        var dy = targetCenter.y - entityY;
        var dz = targetCenter.z - entityZ;
        var distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        var sampleCount = Math.max(1, (int) Math.ceil(distance / ANY_ANGLE_SMOOTHING_SAMPLE_INTERVAL));

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var feetCenter = new Vec3(
                entityX + dx * progress,
                entityY + dy * progress,
                entityZ + dz * progress
            );
            var allowLiquids = terrain == TerrainType.WATER;

            if (
                !spaceQuery.isEntityBoxClear(feetCenter, entityWidth, entityHeight, allowLiquids)
                    || (!allowLiquids && !spaceQuery.hasEntitySupport(feetCenter, entityWidth))
                    || !spaceQuery.hasExpectedTerrain(feetCenter, entityWidth, terrain)
            ) {
                return false;
            }
        }

        return true;
    }

    private double waypointReachXZ(float entityWidth) {
        var reach = entityWidth > 0.75f ? entityWidth / 2.0 : 0.75 - entityWidth / 2.0;

        if (config.getEvaluatorConfig().getEntityWidth() > 1) {
            return Math.min(reach, WIDE_FOOTPRINT_REACH_XZ);
        }

        return reach;
    }

    private double dropEntryReachXZ(float entityWidth) {
        return Math.max(DROP_ENTRY_MIN_REACH_XZ, entityWidth / 2.0d);
    }

    private double waypointReachY(float entityHeight) {
        var reach = Math.max(1.0, entityHeight > 0.75f ? entityHeight / 2.0 : 0.75 - entityHeight / 2.0);

        return Math.min(reach, WAYPOINT_REACH_Y);
    }

    private boolean isDescendingSteppedFootprintWaypointWithinReach(int nodeIndex, double entityY, double reachY) {
        if (!usesSteppedFootprintSupport() || currentPath() == null || nodeIndex <= 0) {
            return false;
        }

        var previousNode = currentPath().getNode(nodeIndex - 1);
        var node = currentPath().getNode(nodeIndex);

        return isDescendingStepEdge(previousNode, node)
            && isDescendingStepYWithinReach(previousNode, node, entityY, reachY);
    }

    private boolean usesSteppedFootprintSupport() {
        var evaluatorConfig = config.getEvaluatorConfig();
        var activeFeatures = activePathfindingFeatures();

        return activeFeatures.footprintClearance()
            && activeFeatures.steppedFootprintSupport()
            && evaluatorConfig.getEntityWidth() > 1
            && evaluatorConfig.getMaxStepHeight() > 0;
    }

    private boolean isDescendingStairEdgeWaypointReached(
        int nodeIndex,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight,
        double reachY
    ) {
        if (!usesDescendingStairEdgeReach() || currentPath() == null || nodeIndex <= 0) {
            return false;
        }

        var previousNode = currentPath().getNode(nodeIndex - 1);
        var node = currentPath().getNode(nodeIndex);

        if (
            !isDescendingStepEdge(previousNode, node)
                || !isDescendingStepYWithinReach(previousNode, node, entityY, reachY)
        ) {
            return false;
        }

        var previousCenter = nodeTargetCenter(previousNode, entityWidth, entityHeight);
        var nodeCenter = nodeTargetCenter(node, entityWidth, entityHeight);
        var edgeX = nodeCenter.x - previousCenter.x;
        var edgeZ = nodeCenter.z - previousCenter.z;
        var edgeDistanceSquared = edgeX * edgeX + edgeZ * edgeZ;

        if (edgeDistanceSquared <= COLLISION_EPSILON) {
            return false;
        }

        var entityEdgeX = entityX - previousCenter.x;
        var entityEdgeZ = entityZ - previousCenter.z;
        var progress = (entityEdgeX * edgeX + entityEdgeZ * edgeZ) / edgeDistanceSquared;

        if (progress < DESCENDING_STAIR_EDGE_REACH_PROGRESS) {
            return false;
        }

        var clampedProgress = Math.max(0.0d, Math.min(1.0d, progress));
        var closestX = previousCenter.x + edgeX * clampedProgress;
        var closestZ = previousCenter.z + edgeZ * clampedProgress;
        var lateralX = entityX - closestX;
        var lateralZ = entityZ - closestZ;
        var lateralTolerance = descendingStairEdgeLateralTolerance(entityWidth);

        return lateralX * lateralX + lateralZ * lateralZ <= lateralTolerance * lateralTolerance;
    }

    private boolean usesDescendingStairEdgeReach() {
        var activeFeatures = activePathfindingFeatures();

        return activeFeatures.descendingStairEdgeReach()
            && activeFeatures.stepDown()
            && config.getEvaluatorConfig().getMaxStepHeight() > 0;
    }

    private boolean isDescendingStepEdge(PathNode previousNode, PathNode node) {
        if (previousNode.hasDropEntryWaypoint() || node.hasDropEntryWaypoint()) {
            return false;
        }

        var stepDown = previousNode.getY() - node.getY();

        if (stepDown <= 0 || stepDown > config.getEvaluatorConfig().getMaxStepHeight()) {
            return false;
        }

        var dx = Math.abs(previousNode.getX() - node.getX());
        var dz = Math.abs(previousNode.getZ() - node.getZ());

        return (dx != 0 || dz != 0) && dx <= 1 && dz <= 1;
    }

    private boolean isDescendingStepYWithinReach(PathNode previousNode, PathNode node, double entityY, double reachY) {
        return entityY >= node.getY() - reachY && entityY <= previousNode.getY() + reachY;
    }

    private double descendingStairEdgeLateralTolerance(float entityWidth) {
        var configuredWidth = Math.max(1.0d, config.getEvaluatorConfig().getEntityWidth());
        var observedWidth = Math.max(configuredWidth, entityWidth);

        return Math.max(waypointReachXZ(entityWidth), observedWidth / 2.0d + 0.25d);
    }

    private boolean shouldSkipToNextNode(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var nextIndex = currentPath().getCurrentNodeIndex() + 1;

        if (nextIndex >= currentPath().getNodeCount()) {
            return false;
        }

        var currentNode = currentPath().getCurrentNode();
        var nextNode = currentPath().getNode(nextIndex);

        if (currentNode.hasDropEntryWaypoint() || nextNode.hasDropEntryWaypoint()) {
            return false;
        }

        if (currentNode.getPosture() != nextNode.getPosture()) {
            return false;
        }

        if (currentNode.getY() != nextNode.getY() || isCornerWaypoint(currentPath().getCurrentNodeIndex(), nextIndex)) {
            return false;
        }

        var currentCenter = nodeTargetCenter(currentNode, entityWidth, entityHeight);

        var toCurrentX = currentCenter.x - entityX;
        var toCurrentY = currentCenter.y - entityY;
        var toCurrentZ = currentCenter.z - entityZ;
        var distToCurrentSq = toCurrentX * toCurrentX + toCurrentY * toCurrentY + toCurrentZ * toCurrentZ;

        if (distToCurrentSq > 4.0) {
            return false;
        }

        var nextCenter = nodeTargetCenter(nextNode, entityWidth, entityHeight);

        var toNextX = nextCenter.x - entityX;
        var toNextY = nextCenter.y - entityY;
        var toNextZ = nextCenter.z - entityZ;
        var distToNextSq = toNextX * toNextX + toNextY * toNextY + toNextZ * toNextZ;

        var closerToNext = distToNextSq < distToCurrentSq;
        var veryCloseToCurrent = distToCurrentSq < 0.5;

        if (!closerToNext && !veryCloseToCurrent) {
            return false;
        }

        // Negative dot product means the current node is behind the entity relative to the next node.
        var dot = toNextX * toCurrentX + toNextY * toCurrentY + toNextZ * toCurrentZ;

        return dot < 0.0;
    }

    private boolean isCornerWaypoint(int currentIndex, int nextIndex) {
        if (currentIndex <= 0) {
            return false;
        }

        var previousNode = currentPath().getNode(currentIndex - 1);
        var currentNode = currentPath().getNode(currentIndex);
        var nextNode = currentPath().getNode(nextIndex);

        if (previousNode.getY() != currentNode.getY() || currentNode.getY() != nextNode.getY()) {
            return true;
        }

        var previousDx = Integer.compare(currentNode.getX(), previousNode.getX());
        var previousDz = Integer.compare(currentNode.getZ(), previousNode.getZ());
        var nextDx = Integer.compare(nextNode.getX(), currentNode.getX());
        var nextDz = Integer.compare(nextNode.getZ(), currentNode.getZ());

        return previousDx != nextDx || previousDz != nextDz;
    }

    private void syncDropEntryTracking(PathNode node) {
        if (currentPath() == null || currentPath().isDone() || !node.hasDropEntryWaypoint()) {
            dropEntryNodeIndex = -1;
            dropEntryReached = false;
            return;
        }

        var nodeIndex = currentPath().getCurrentNodeIndex();

        if (dropEntryNodeIndex != nodeIndex) {
            dropEntryNodeIndex = nodeIndex;
            dropEntryReached = false;
        }
    }

    private boolean isCurrentDropEntryWaypointPending(PathNode node) {
        return currentPath() != null
            && !currentPath().isDone()
            && node.hasDropEntryWaypoint()
            && currentPath().getCurrentNodeIndex() == dropEntryNodeIndex
            && !dropEntryReached;
    }

    private @Nullable Vec3 shapeAwareNodeCenter(PathNode node, float entityWidth, float entityHeight) {
        if (entityWidth <= 0.0f || entityHeight <= 0.0f || entityWidth > 1.0f) {
            return null;
        }

        var center = nodeCenter(node);

        if (spaceQuery.isEntityBoxClear(center, entityWidth, entityHeight)) {
            return center;
        }

        for (var candidate : shapeWaypointCandidates(entityWidth)) {
            var candidateCenter = new Vec3(node.getX() + candidate.localX(), node.getY(), node.getZ() + candidate.localZ());

            if (spaceQuery.isEntityBoxClear(candidateCenter, entityWidth, entityHeight)) {
                return candidateCenter;
            }
        }

        return null;
    }

    private static List<WaypointCandidate> shapeWaypointCandidates(float entityWidth) {
        var halfWidth = entityWidth / 2.0d;
        var minLocal = halfWidth;
        var maxLocal = 1.0d - halfWidth;

        if (minLocal > maxLocal + COLLISION_EPSILON) {
            return List.of();
        }

        var axisSamples = shapeWaypointAxisSamples(minLocal, maxLocal);
        var candidates = new ArrayList<WaypointCandidate>();

        for (var localX : axisSamples) {
            for (var localZ : axisSamples) {
                if (Math.abs(localX - 0.5d) <= COLLISION_EPSILON && Math.abs(localZ - 0.5d) <= COLLISION_EPSILON) {
                    continue;
                }

                var dx = localX - 0.5d;
                var dz = localZ - 0.5d;
                candidates.add(new WaypointCandidate(localX, localZ, dx * dx + dz * dz));
            }
        }

        candidates.sort(
            Comparator.comparingDouble(WaypointCandidate::distanceSquared)
                .thenComparingDouble(WaypointCandidate::localX)
                .thenComparingDouble(WaypointCandidate::localZ)
        );

        return candidates;
    }

    private static List<Double> shapeWaypointAxisSamples(double minLocal, double maxLocal) {
        var samples = new ArrayList<Double>();
        addShapeWaypointAxisSample(samples, 0.5d, minLocal, maxLocal);

        var sampleCount = (int) Math.ceil(0.5d / SHAPE_WAYPOINT_SAMPLE_STEP);

        for (var i = 1; i <= sampleCount; i++) {
            var offset = i * SHAPE_WAYPOINT_SAMPLE_STEP;

            addShapeWaypointAxisSample(samples, 0.5d - offset, minLocal, maxLocal);
            addShapeWaypointAxisSample(samples, 0.5d + offset, minLocal, maxLocal);
        }

        addShapeWaypointAxisSample(samples, minLocal, minLocal, maxLocal);
        addShapeWaypointAxisSample(samples, maxLocal, minLocal, maxLocal);

        samples.sort(Comparator.comparingDouble(sample -> Math.abs(sample - 0.5d)));

        return samples;
    }

    private static void addShapeWaypointAxisSample(List<Double> samples, double sample, double minLocal, double maxLocal) {
        if (sample < minLocal - COLLISION_EPSILON || sample > maxLocal + COLLISION_EPSILON) {
            return;
        }

        var clamped = Math.max(minLocal, Math.min(maxLocal, sample));

        for (var existing : samples) {
            if (Math.abs(existing - clamped) <= COLLISION_EPSILON) {
                return;
            }
        }

        samples.add(clamped);
    }

    private Vec3 nodeCenter(PathNode node) {
        var centerOffset = Math.max(1, config.getEvaluatorConfig().getEntityWidth()) / 2.0;

        return new Vec3(node.getX() + centerOffset, node.getY(), node.getZ() + centerOffset);
    }

    private boolean samePosition(Vec3 left, Vec3 right) {
        return Math.abs(left.x - right.x) <= COLLISION_EPSILON
            && Math.abs(left.y - right.y) <= COLLISION_EPSILON
            && Math.abs(left.z - right.z) <= COLLISION_EPSILON;
    }

    private PathfindingFeatures activePathfindingFeatures() {
        return activeFeaturesSupplier.get();
    }

    private @Nullable BLibPath currentPath() {
        return state.currentPath();
    }

    private @Nullable TerrainType currentTerrain() {
        return state.currentTerrain();
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureUsageConsumer.accept(feature);
    }

    private void markProgress() {
        progressMarker.run();
    }

    private record WaypointCandidate(
        double localX,
        double localZ,
        double distanceSquared
    ) {}

    private record AnyAngleSmoothingKey(
        int currentIndex,
        int targetIndex,
        long entityX,
        long entityY,
        long entityZ,
        long targetX,
        long targetY,
        long targetZ,
        int entityWidth,
        int entityHeight,
        TerrainType terrain
    ) {}
}
