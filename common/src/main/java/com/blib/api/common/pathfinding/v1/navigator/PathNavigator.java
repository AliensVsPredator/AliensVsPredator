package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;

/**
 * Standalone path navigator. Manages path planning, following, stuck detection, and terrain transition callbacks. Does
 * not extend any Minecraft class.
 * <p>
 * The consuming code calls {@link #tick(double, double, double, float, float)} each tick with the entity's exact
 * position and bounding box dimensions. The navigator advances along the path and provides the next waypoint via
 * {@link #getCurrentTargetPos()}. The calling code is responsible for actually moving the entity toward the waypoint.
 * </p>
 */
public final class PathNavigator {

    private final PathNavigatorConfig config;

    private final BLibPathFinder pathFinder;

    private final LevelReader level;

    private @Nullable BLibPath currentPath;

    private @Nullable BlockPos targetPos;

    private @Nullable BlockPos lastComputedTargetPos;

    private @Nullable TerrainType currentTerrain;

    private boolean waitingForBlockBreak;

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

    private int lastFailureTick;

    private static final int BASE_FAILURE_COOLDOWN = 10;

    private static final int MAX_FAILURE_COOLDOWN = 200;

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

        var startNanos = System.nanoTime();
        this.currentPath = pathFinder.findPath(level, entityPos, target);
        this.lastPathComputeNanos = System.nanoTime() - startNanos;

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

        return currentPath != null;
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
        this.asyncStartNanos = System.nanoTime();
        this.pendingPath = pathFinder.findPathAsync(level, entityPos, target);
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

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);

        var entityBlockPos = BlockPos.containing(entityX, entityY, entityZ);

        if (currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        detectStuck(entityBlockPos);
        checkRecalculate(entityBlockPos, entityX, entityY, entityZ, entityWidth, entityHeight);
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
     * Returns the position the entity should move toward, or null if not navigating.
     */
    public @Nullable BlockPos getCurrentTargetPos() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        var node = currentPath.getCurrentNode();

        return new BlockPos(node.getX(), node.getY(), node.getZ());
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
     * Returns true if the navigator is paused at a BREAKABLE node, waiting for the consuming code to break the block
     * and call {@link #confirmBlockBroken()}.
     */
    public boolean isWaitingForBlockBreak() {
        return waitingForBlockBreak;
    }

    /**
     * Returns the position of the block that needs to be broken, or null if not waiting.
     */
    public @Nullable BlockPos getBlockToBreak() {
        if (!waitingForBlockBreak || currentPath == null || currentPath.isDone()) {
            return null;
        }

        var node = currentPath.getCurrentNode();

        return new BlockPos(node.getX(), node.getY(), node.getZ());
    }

    /**
     * Signals that the block at the current BREAKABLE node has been broken. The navigator resumes path following.
     */
    public void confirmBlockBroken() {
        this.waitingForBlockBreak = false;
    }

    public @Nullable BlockPos getTargetPos() {
        return targetPos;
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

        return tickCount - lastFailureTick < failureCooldownTicks;
    }

    private void recordFailure() {
        consecutiveFailures++;
        lastFailureTick = tickCount;
        failureCooldownTicks = Math.min(BASE_FAILURE_COOLDOWN * (1 << (consecutiveFailures - 1)), MAX_FAILURE_COOLDOWN);
    }

    private void resetFailureCooldown() {
        consecutiveFailures = 0;
        failureCooldownTicks = 0;
    }

    private void advanceWaypoints(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var reachXZ = entityWidth > 0.75f ? entityWidth / 2.0 : 0.75 - entityWidth / 2.0;
        var reachY = Math.max(1.0, entityHeight > 0.75f ? entityHeight / 2.0 : 0.75 - entityHeight / 2.0);
        var nodeCenterOffset = (int) (entityWidth + 1.0f) * 0.5;

        while (!currentPath.isDone()) {
            var waypoint = currentPath.getCurrentNode();

            var waypointCenterX = waypoint.getX() + nodeCenterOffset;
            var waypointCenterZ = waypoint.getZ() + nodeCenterOffset;

            var dx = Math.abs(waypointCenterX - entityX);
            var dy = Math.abs(waypoint.getY() - entityY);
            var dz = Math.abs(waypointCenterZ - entityZ);

            var withinReach = dx <= reachXZ && dy <= reachY && dz <= reachXZ;

            if (!withinReach && !shouldSkipToNextNode(entityX, entityY, entityZ, nodeCenterOffset)) {
                break;
            }

            var previousTerrain = currentTerrain;

            currentPath.advance();
            lastProgressTick = tickCount;
            lastDistanceToTarget = Double.MAX_VALUE;

            if (!currentPath.isDone()) {
                var nextNode = currentPath.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();

                if (newTerrain == TerrainType.BREAKABLE) {
                    waitingForBlockBreak = true;
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

    /**
     * Checks whether the entity has already passed the current node and should skip ahead to the next one. Uses the
     * same approach as vanilla Minecraft's {@code shouldTargetNextNodeInDirection}: if the entity is closer to the next
     * node than the current one and the dot product of the direction vectors is negative (meaning the current node is
     * behind the entity), the current node is skipped.
     */
    private boolean shouldSkipToNextNode(
        double entityX,
        double entityY,
        double entityZ,
        double nodeCenterOffset
    ) {
        var nextIndex = currentPath.getCurrentNodeIndex() + 1;

        if (nextIndex >= currentPath.getNodeCount()) {
            return false;
        }

        var currentNode = currentPath.getCurrentNode();
        var currentCenterX = currentNode.getX() + nodeCenterOffset;
        var currentCenterY = (double) currentNode.getY();
        var currentCenterZ = currentNode.getZ() + nodeCenterOffset;

        var toCurrentX = currentCenterX - entityX;
        var toCurrentY = currentCenterY - entityY;
        var toCurrentZ = currentCenterZ - entityZ;
        var distToCurrentSq = toCurrentX * toCurrentX + toCurrentY * toCurrentY + toCurrentZ * toCurrentZ;

        if (distToCurrentSq > 4.0) {
            return false;
        }

        var nextNode = currentPath.getNode(nextIndex);
        var nextCenterX = nextNode.getX() + nodeCenterOffset;
        var nextCenterY = (double) nextNode.getY();
        var nextCenterZ = nextNode.getZ() + nodeCenterOffset;

        var toNextX = nextCenterX - entityX;
        var toNextY = nextCenterY - entityY;
        var toNextZ = nextCenterZ - entityZ;
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
            navigateTo(entityPos, targetPos);

            // Advance past any nodes the entity has already reached so the new
            // path doesn't briefly target the start node behind the entity.
            if (currentPath != null && !currentPath.isDone()) {
                advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);
            }
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

}
