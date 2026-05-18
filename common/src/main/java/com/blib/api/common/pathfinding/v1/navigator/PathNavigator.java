package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathBreakRequirement;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.search.SegmentedPathPlanner;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;

/**
 * Standalone path navigator. Manages path planning, following, stuck detection, and terrain transition callbacks. Does
 * not extend any Minecraft class.
 * <p>
 * The consuming code calls {@link #tick(double, double, double, float, float)} each tick with the entity's exact
 * position and bounding box dimensions. The navigator advances along the path and provides the next waypoint via
 * {@link #getCurrentTargetCenter()}. The calling code is responsible for actually moving the entity toward the
 * waypoint.
 * </p>
 */
public final class PathNavigator {

    private final PathNavigatorConfig config;

    private final BLibPathFinder pathFinder;

    private final @Nullable SegmentedPathPlanner planner;

    private final LevelReader level;

    private @Nullable BLibPath currentPath;

    private @Nullable BlockPos targetPos;

    private @Nullable BlockPos lastComputedTargetPos;

    private @Nullable TerrainType currentTerrain;

    private boolean waitingForBlockBreak;

    private int breakRequirementIndex;

    private int lastPathComputeTick;

    private int lastProgressTick;

    private long lastPathComputeNanos;

    private double lastDistanceToTarget;

    private int tickCount;

    private @Nullable CompletableFuture<@Nullable BLibPath> pendingPath;

    private long asyncStartNanos;

    // --- Failed path backoff ---
    private int consecutiveFailures;

    private int failureCooldownTicks;

    private long lastFailureTick;

    private final long createdNanos = System.nanoTime();

    private static final int BASE_FAILURE_COOLDOWN = 10;

    private static final int MAX_FAILURE_COOLDOWN = 200;

    private static final long NANOS_PER_TICK = 50_000_000L;

    private static final double WIDE_FOOTPRINT_REACH_XZ = 0.45;

    private static final double WAYPOINT_REACH_Y = 0.45;

    private @Nullable Set<TerrainType> excludedTerrains;

    private boolean needsRepath;

    public PathNavigator(LevelReader level, PathNavigatorConfig config) {
        this(level, config, null);
    }

    public PathNavigator(LevelReader level, PathNavigatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.level = level;
        this.config = config;
        this.pathFinder = new BLibPathFinder(
            new UnifiedTerrainEvaluator(config.getEvaluatorConfig(), classificationCache),
            config.getSearchConfig(),
            classificationCache
        );
        this.planner = classificationCache != null
            ? new SegmentedPathPlanner(pathFinder)
            : null;
    }

    /**
     * Plans a path to the target position and begins following it.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(BlockPos entityPos, BlockPos target) {
        if (isInFailureCooldown(target)) {
            return false;
        }

        this.targetPos = target;
        this.lastComputedTargetPos = target;
        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;

        pathFinder.setExcludedTerrains(excludedTerrains);

        var startNanos = System.nanoTime();

        if (planner != null) {
            this.currentPath = planner.findPath(level, entityPos, target);
        } else {
            this.currentPath = pathFinder.findPath(level, entityPos, target);
        }

        this.lastPathComputeNanos = System.nanoTime() - startNanos;

        this.lastPathComputeTick = tickCount;
        this.lastProgressTick = tickCount;
        this.lastDistanceToTarget = Double.MAX_VALUE;

        if (currentPath != null && (currentPath.isReached() || (planner != null && planner.hasActiveRoute()))) {
            var startNode = currentPath.getCurrentNode();

            this.currentTerrain = startNode.getTerrainType();
            resetFailureCooldown();
        } else {
            recordFailure();
        }

        return currentPath != null;
    }

    /**
     * Plans a path from the entity's current center position. Wide entities are converted to the nearest footprint
     * anchor before search.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(double entityX, double entityY, double entityZ, BlockPos target) {
        return navigateTo(entityAnchorPos(entityX, entityY, entityZ), target);
    }

    /**
     * Asynchronously plans a path to the target position. Chunk data is snapshotted and the terrain cache is
     * pre-populated on the calling thread, then the A* search runs on a background thread. Call
     * {@link #isPathPending()} to check if an async computation is in progress. The path is automatically applied on
     * the next {@link #tick} call after the computation completes.
     */
    public void navigateToAsync(BlockPos entityPos, BlockPos target) {
        if (isInFailureCooldown(target)) {
            return;
        }

        this.targetPos = target;
        this.lastComputedTargetPos = target;
        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;
        pathFinder.setExcludedTerrains(excludedTerrains);

        this.asyncStartNanos = System.nanoTime();
        this.pendingPath = pathFinder.findPathAsync(level, entityPos, target);
    }

    /**
     * Asynchronously plans a path from the entity's current center position. Wide entities are converted to the nearest
     * footprint anchor before search.
     */
    public void navigateToAsync(double entityX, double entityY, double entityZ, BlockPos target) {
        navigateToAsync(entityAnchorPos(entityX, entityY, entityZ), target);
    }

    /**
     * Returns true if an asynchronous path computation is in progress.
     */
    public boolean isPathPending() {
        return pendingPath != null;
    }

    private void checkPendingPath() {
        if (pendingPath == null || !pendingPath.isDone()) {
            return;
        }

        var path = pendingPath.join();
        pendingPath = null;

        this.currentPath = path;
        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;
        this.lastPathComputeNanos = System.nanoTime() - asyncStartNanos;
        this.lastPathComputeTick = tickCount;
        this.lastProgressTick = tickCount;
        this.lastDistanceToTarget = Double.MAX_VALUE;

        if (currentPath != null && currentPath.isReached()) {
            var startNode = currentPath.getCurrentNode();

            this.currentTerrain = startNode.getTerrainType();
            resetFailureCooldown();
        } else {
            recordFailure();
        }
    }

    /**
     * Advances the navigator one tick. Call this every tick with the entity's exact position. The navigator checks
     * waypoint proximity using per-axis distance and entity dimensions, advances the path, fires transition handlers,
     * and detects stuck conditions.
     *
     * @param entityX      exact X position of the entity
     * @param entityY      exact Y position of the entity (feet)
     * @param entityZ      exact Z position of the entity
     * @param entityWidth  bounding box width of the entity
     * @param entityHeight bounding box height of the entity
     */
    public void tick(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        tickCount++;
        checkPendingPath();

        if (needsRepath) {
            needsRepath = false;

            if (targetPos != null) {
                navigateTo(entityX, entityY, entityZ, targetPos);
            }
        }

        // Advance to next segment if current path is done but route hasn't reached the final target.
        if (currentPath != null && currentPath.isDone() && planner != null && planner.hasActiveRoute()) {
            if (currentPath.isReached()) {
                planner.clear();
            } else {
                advanceToNextSegment(entityX, entityY, entityZ);
            }
        }

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);

        var entityBlockPos = BlockPos.containing(entityX, entityY, entityZ);
        var entityAnchorPos = entityAnchorPos(entityX, entityY, entityZ);

        // After waypoint advancement, check again for segment transition.
        if (currentPath != null && currentPath.isDone() && planner != null && planner.hasActiveRoute()) {
            if (currentPath.isReached()) {
                planner.clear();
            } else {
                advanceToNextSegment(entityX, entityY, entityZ);
            }
        }

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        detectStuck(entityBlockPos);
        checkRecalculate(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
    }

    /**
     * Stops navigation and clears the current path.
     */
    public void stop() {
        if (pendingPath != null) {
            pendingPath.cancel(false);
            pendingPath = null;
        }

        this.currentPath = null;
        this.targetPos = null;
        this.currentTerrain = null;
        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;
        this.needsRepath = false;

        if (planner != null) {
            planner.clear();
        }
    }

    public PathNavigatorConfig getConfig() {
        return config;
    }

    /**
     * Returns the navigator's internal tick counter, incremented once per {@link #tick} call.
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * Returns the tick at which the current path was computed.
     */
    public int getLastPathComputeTick() {
        return lastPathComputeTick;
    }

    /**
     * Returns the tick at which the navigator last advanced to a new node (i.e. made measurable progress).
     */
    public int getLastProgressTick() {
        return lastProgressTick;
    }

    /**
     * Returns the wall-clock time in nanoseconds that the most recent path computation took.
     */
    public long getLastPathComputeNanos() {
        return lastPathComputeNanos;
    }

    /**
     * Returns the snapshot from the most recent A* search, or null if no search has been performed.
     */
    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return pathFinder.getLastSearchSnapshot();
    }

    /**
     * Returns the current path node, or null if not navigating.
     */
    public @Nullable PathNode getCurrentNode() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return currentPath.getCurrentNode();
    }

    /**
     * Returns the footprint-anchor position of the current path node, or null if not navigating. Movement code should
     * use {@link #getCurrentTargetCenter()} instead.
     */
    public @Nullable BlockPos getCurrentTargetPos() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        var node = currentPath.getCurrentNode();

        return new BlockPos(node.getX(), node.getY(), node.getZ());
    }

    /**
     * Returns the world-space center the entity should move toward for the current path node, or null if not navigating.
     */
    public @Nullable Vec3 getCurrentTargetCenter() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return nodeCenter(currentPath.getCurrentNode());
    }

    public boolean isNavigating() {
        return currentPath != null && !currentPath.isDone();
    }

    public boolean isDone() {
        return currentPath == null || currentPath.isDone();
    }

    public @Nullable BLibPath getCurrentPath() {
        return currentPath;
    }

    public @Nullable TerrainType getCurrentTerrain() {
        return currentTerrain;
    }

    /**
     * Returns true if the navigator is paused at a node with break requirements and waiting for the consuming code to
     * clear the current requirement and call {@link #confirmBlockBroken()}.
     */
    public boolean isWaitingForBlockBreak() {
        return waitingForBlockBreak;
    }

    /**
     * Returns the origin of the current break requirement, or null if not waiting.
     */
    public @Nullable BlockPos getBlockToBreak() {
        var requirement = getCurrentBreakRequirement();

        if (requirement == null) {
            return null;
        }

        return new BlockPos(requirement.x(), requirement.y(), requirement.z());
    }

    /**
     * Returns the current block column that must be cleared before movement can resume, or null if not waiting.
     */
    public @Nullable PathBreakRequirement getCurrentBreakRequirement() {
        if (!waitingForBlockBreak || currentPath == null || currentPath.isDone()) {
            return null;
        }

        var node = currentPath.getCurrentNode();
        if (breakRequirementIndex < 0 || breakRequirementIndex >= node.getBreakRequirementCount()) {
            return null;
        }

        return node.getBreakRequirement(breakRequirementIndex);
    }

    /**
     * Returns the remaining block columns for the current path node, beginning with the current requirement.
     */
    public List<PathBreakRequirement> getRemainingBreakRequirements() {
        if (!waitingForBlockBreak || currentPath == null || currentPath.isDone()) {
            return List.of();
        }

        var node = currentPath.getCurrentNode();
        if (breakRequirementIndex < 0 || breakRequirementIndex >= node.getBreakRequirementCount()) {
            return List.of();
        }

        return List.copyOf(node.getBreakRequirements().subList(breakRequirementIndex, node.getBreakRequirementCount()));
    }

    /**
     * Signals that the current break requirement has been cleared. Movement resumes after all requirements on the
     * current node are cleared.
     */
    public void confirmBlockBroken() {
        if (currentPath == null || currentPath.isDone()) {
            this.waitingForBlockBreak = false;
            this.breakRequirementIndex = 0;
            return;
        }

        var node = currentPath.getCurrentNode();
        if (breakRequirementIndex + 1 < node.getBreakRequirementCount()) {
            breakRequirementIndex++;
            return;
        }

        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;
    }

    public @Nullable BlockPos getTargetPos() {
        return targetPos;
    }

    /**
     * Sets terrain types to exclude from the next pathfinding search. Excluded terrains are treated as impassable.
     * Useful for restricting behavior — e.g., wandering entities should not consider BREAKABLE paths.
     * <p>
     * Pass {@code null} to clear exclusions.
     * </p>
     */
    public void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains) {
        if (!Objects.equals(this.excludedTerrains, excludedTerrains)) {
            this.excludedTerrains = excludedTerrains;

            if (isNavigating()) {
                needsRepath = true;
            }
        }
    }

    public @Nullable Set<TerrainType> getExcludedTerrains() {
        return excludedTerrains;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public int getFailureCooldownRemainingTicks() {
        if (consecutiveFailures == 0) {
            return 0;
        }

        return (int) Math.max(0, failureCooldownTicks - (cooldownClock() - lastFailureTick));
    }

    public void setDebugCaptureEnabled(boolean debugCaptureEnabled) {
        pathFinder.setDebugCaptureEnabled(debugCaptureEnabled);
    }

    /**
     * Updates the destination without forcing an immediate path recomputation. The navigator will recompute the path on
     * its next recalculation cycle using this updated target.
     */
    public void updateTarget(BlockPos newTarget) {
        this.targetPos = newTarget;
    }

    // --- Failure backoff ---

    private boolean isInFailureCooldown(BlockPos target) {
        if (consecutiveFailures == 0) {
            return false;
        }

        // Reset cooldown if target changed significantly.
        if (lastComputedTargetPos != null && target.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED) {
            resetFailureCooldown();
            return false;
        }

        return cooldownClock() - lastFailureTick < failureCooldownTicks;
    }

    private void recordFailure() {
        consecutiveFailures++;
        lastFailureTick = cooldownClock();
        failureCooldownTicks = Math.min(BASE_FAILURE_COOLDOWN * (1 << (consecutiveFailures - 1)), MAX_FAILURE_COOLDOWN);
    }

    private void resetFailureCooldown() {
        consecutiveFailures = 0;
        failureCooldownTicks = 0;
    }

    private long cooldownClock() {
        if (level instanceof Level concreteLevel) {
            return concreteLevel.getGameTime();
        }

        return (System.nanoTime() - createdNanos) / NANOS_PER_TICK;
    }

    private void advanceWaypoints(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var reachXZ = waypointReachXZ(entityWidth);
        var reachY = waypointReachY(entityHeight);

        while (!currentPath.isDone()) {
            var waypoint = currentPath.getCurrentNode();
            var waypointCenter = nodeCenter(waypoint);

            var dx = Math.abs(waypointCenter.x - entityX);
            var dy = Math.abs(waypointCenter.y - entityY);
            var dz = Math.abs(waypointCenter.z - entityZ);

            var withinReach = dx <= reachXZ && dy <= reachY && dz <= reachXZ;

            if (!withinReach && !shouldSkipToNextNode(entityX, entityY, entityZ)) {
                break;
            }

            var previousTerrain = currentTerrain;

            currentPath.advance();
            lastProgressTick = tickCount;
            lastDistanceToTarget = Double.MAX_VALUE;

            if (!currentPath.isDone()) {
                var nextNode = currentPath.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();

                if (nextNode.hasBreakRequirements()) {
                    waitingForBlockBreak = true;
                    breakRequirementIndex = 0;
                    currentTerrain = newTerrain;
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    break;
                }

                if (newTerrain != previousTerrain) {
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    currentTerrain = newTerrain;
                    break;
                }
            }
        }
    }

    private double waypointReachXZ(float entityWidth) {
        var reach = entityWidth > 0.75f ? entityWidth / 2.0 : 0.75 - entityWidth / 2.0;

        if (config.getEvaluatorConfig().getEntityWidth() > 1) {
            return Math.min(reach, WIDE_FOOTPRINT_REACH_XZ);
        }

        return reach;
    }

    private double waypointReachY(float entityHeight) {
        var reach = Math.max(1.0, entityHeight > 0.75f ? entityHeight / 2.0 : 0.75 - entityHeight / 2.0);

        return Math.min(reach, WAYPOINT_REACH_Y);
    }

    /**
     * Checks whether the entity has already passed the current node and should skip ahead to the next one. Skip-ahead is
     * intentionally limited to straight, same-height runs. Corners and vertical transitions are real navigation
     * waypoints, especially for wide entities on stairs.
     */
    private boolean shouldSkipToNextNode(
        double entityX,
        double entityY,
        double entityZ
    ) {
        var nextIndex = currentPath.getCurrentNodeIndex() + 1;

        if (nextIndex >= currentPath.getNodeCount()) {
            return false;
        }

        var currentNode = currentPath.getCurrentNode();
        var nextNode = currentPath.getNode(nextIndex);

        if (currentNode.getY() != nextNode.getY() || isCornerWaypoint(currentPath.getCurrentNodeIndex(), nextIndex)) {
            return false;
        }

        var currentCenter = nodeCenter(currentNode);

        var toCurrentX = currentCenter.x - entityX;
        var toCurrentY = currentCenter.y - entityY;
        var toCurrentZ = currentCenter.z - entityZ;
        var distToCurrentSq = toCurrentX * toCurrentX + toCurrentY * toCurrentY + toCurrentZ * toCurrentZ;

        if (distToCurrentSq > 4.0) {
            return false;
        }

        var nextCenter = nodeCenter(nextNode);

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

        var previousNode = currentPath.getNode(currentIndex - 1);
        var currentNode = currentPath.getNode(currentIndex);
        var nextNode = currentPath.getNode(nextIndex);

        if (previousNode.getY() != currentNode.getY() || currentNode.getY() != nextNode.getY()) {
            return true;
        }

        var previousDx = Integer.compare(currentNode.getX(), previousNode.getX());
        var previousDz = Integer.compare(currentNode.getZ(), previousNode.getZ());
        var nextDx = Integer.compare(nextNode.getX(), currentNode.getX());
        var nextDz = Integer.compare(nextNode.getZ(), currentNode.getZ());

        return previousDx != nextDx || previousDz != nextDz;
    }

    private void detectStuck(BlockPos entityPos) {
        if (targetPos == null) {
            return;
        }

        var currentDistance = entityPos.distSqr(targetPos);

        if (currentDistance < lastDistanceToTarget - 0.5) {
            lastDistanceToTarget = currentDistance;
            lastProgressTick = tickCount;
            return;
        }

        var ticksSinceProgress = tickCount - lastProgressTick;

        if (ticksSinceProgress >= config.getStuckTimeoutInTicks()) {
            recordFailure();
            stop();
        }
    }

    private static final double MIN_TARGET_MOVE_DISTANCE_SQUARED = 9.0;

    private void checkRecalculate(
        BlockPos entityPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        if (targetPos == null) {
            return;
        }

        if (tickCount - lastPathComputeTick < config.getPathRecalculateIntervalInTicks()) {
            return;
        }

        var targetMoved = lastComputedTargetPos == null
            || targetPos.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED;

        if (targetMoved) {
            if (planner != null) {
                planner.clear();
            }

            navigateTo(entityPos, targetPos);

            // Advance past any nodes the entity has already reached so the new
            // path doesn't briefly target the start node behind the entity.
            if (currentPath != null && !currentPath.isDone()) {
                advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);
            }
        }
    }

    private void advanceToNextSegment(double entityX, double entityY, double entityZ) {
        var entityPos = entityAnchorPos(entityX, entityY, entityZ);
        var startNanos = System.nanoTime();

        pathFinder.setExcludedTerrains(excludedTerrains);
        this.currentPath = planner.computeNextSegment(level, entityPos);
        this.waitingForBlockBreak = false;
        this.breakRequirementIndex = 0;
        this.lastPathComputeNanos = System.nanoTime() - startNanos;
        this.lastPathComputeTick = tickCount;
        this.lastProgressTick = tickCount;
        this.lastDistanceToTarget = Double.MAX_VALUE;

        if (currentPath != null) {
            this.currentTerrain = currentPath.getCurrentNode().getTerrainType();
        }
    }

    private void fireTransitionHandlers(@Nullable TerrainType from, TerrainType to) {
        if (from == null) {
            return;
        }

        var handlers = config.getTransitionHandlers(from, to);
        var transition = new TerrainTransition(from, to, currentPath.getCurrentNodeIndex());

        for (var handler : handlers) {
            handler.onTransition(transition, currentPath);
        }
    }

    private BlockPos entityAnchorPos(double entityX, double entityY, double entityZ) {
        var centerOffset = nodeCenterOffset();

        return BlockPos.containing(entityX - centerOffset + 0.5, entityY, entityZ - centerOffset + 0.5);
    }

    private Vec3 nodeCenter(PathNode node) {
        var centerOffset = nodeCenterOffset();

        return new Vec3(node.getX() + centerOffset, node.getY(), node.getZ() + centerOffset);
    }

    private double nodeCenterOffset() {
        return Math.max(1, config.getEvaluatorConfig().getEntityWidth()) / 2.0;
    }

}
