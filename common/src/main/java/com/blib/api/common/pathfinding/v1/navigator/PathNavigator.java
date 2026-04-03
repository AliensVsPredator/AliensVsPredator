package com.blib.api.common.pathfinding.v1.navigator;

import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone path navigator. Manages path planning, following, stuck detection,
 * and terrain transition callbacks. Does not extend any Minecraft class.
 *
 * <p>The consuming code calls {@link #tick(BlockPos)} each tick with the entity's current
 * position. The navigator advances along the path and provides the next waypoint
 * via {@link #getCurrentTargetPos()}. The calling code is responsible for actually
 * moving the entity toward the waypoint.</p>
 */
public final class PathNavigator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathNavigator.class);

    private final PathNavigatorConfig config;

    private final BLibPathFinder pathFinder;

    private final LevelReader level;

    private @Nullable BLibPath currentPath;

    private @Nullable BlockPos targetPos;

    private @Nullable TerrainType currentTerrain;

    private boolean waitingForBlockBreak;

    private int lastPathComputeTick;

    private int lastProgressTick;

    private double lastDistanceToTarget;

    private int tickCount;

    public PathNavigator(LevelReader level, PathNavigatorConfig config) {
        this.level = level;
        this.config = config;
        this.pathFinder = new BLibPathFinder(
            new UnifiedTerrainEvaluator(config.getEvaluatorConfig()),
            config.getSearchConfig()
        );
    }

    /**
     * Plans a path to the target position and begins following it.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(BlockPos entityPos, BlockPos target) {
        this.targetPos = target;
        this.currentPath = pathFinder.findPath(level, entityPos, target);
        this.lastPathComputeTick = tickCount;
        this.lastProgressTick = tickCount;
        this.lastDistanceToTarget = Double.MAX_VALUE;

        if (currentPath != null) {
            this.currentTerrain = currentPath.getCurrentNode().getTerrainType();
        }

        return currentPath != null;
    }

    /**
     * Advances the navigator one tick. Call this every tick with the entity's current position.
     * The navigator checks waypoint proximity, advances the path, fires transition handlers,
     * and detects stuck conditions.
     */
    public void tick(BlockPos entityPos) {
        tickCount++;

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        advanceWaypoints(entityPos);

        if (waitingForBlockBreak) {
            return;
        }

        detectStuck(entityPos);
        checkRecalculate(entityPos);
    }

    /**
     * Stops navigation and clears the current path.
     */
    public void stop() {
        this.currentPath = null;
        this.targetPos = null;
        this.currentTerrain = null;
        this.waitingForBlockBreak = false;
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
     * Returns true if the navigator is paused at a BREAKABLE node,
     * waiting for the consuming code to break the block and call {@link #confirmBlockBroken()}.
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
     * Signals that the block at the current BREAKABLE node has been broken.
     * The navigator resumes path following.
     */
    public void confirmBlockBroken() {
        this.waitingForBlockBreak = false;
    }

    public @Nullable BlockPos getTargetPos() {
        return targetPos;
    }

    /**
     * Updates the destination without forcing an immediate path recomputation.
     * The navigator will recompute the path on its next recalculation cycle
     * using this updated target.
     */
    public void updateTarget(BlockPos newTarget) {
        this.targetPos = newTarget;
    }

    private void advanceWaypoints(BlockPos entityPos) {
        var reachDistance = config.getWaypointReachDistance();
        var reachDistanceSquared = reachDistance * reachDistance;

        while (!currentPath.isDone()) {
            var waypoint = currentPath.getCurrentNode();
            var distanceSquared = entityDistanceSquared(entityPos, waypoint);

            LOGGER.info("[Nav] Waypoint check: entity={}, node=({},{},{}) terrain={}, dist²={}, reach²={}",
                entityPos, waypoint.getX(), waypoint.getY(), waypoint.getZ(),
                waypoint.getTerrainType(), distanceSquared, reachDistanceSquared);

            if (distanceSquared > reachDistanceSquared) {
                break;
            }

            var previousTerrain = currentTerrain;

            currentPath.advance();
            lastProgressTick = tickCount;
            lastDistanceToTarget = Double.MAX_VALUE;

            if (!currentPath.isDone()) {
                var nextNode = currentPath.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();

                LOGGER.info("[Nav] Advanced to node ({},{},{}) terrain={}",
                    nextNode.getX(), nextNode.getY(), nextNode.getZ(), newTerrain);

                if (newTerrain == TerrainType.BREAKABLE) {
                    LOGGER.info("[Nav] BREAKABLE detected — waiting for block break at ({},{},{})",
                        nextNode.getX(), nextNode.getY(), nextNode.getZ());
                    waitingForBlockBreak = true;
                    currentTerrain = newTerrain;
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    break;
                }

                if (newTerrain != previousTerrain) {
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    currentTerrain = newTerrain;
                }
            }
        }
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

        if (tickCount - lastProgressTick >= config.getStuckTimeoutInTicks()) {
            stop();
        }
    }

    private void checkRecalculate(BlockPos entityPos) {
        if (targetPos == null) {
            return;
        }

        if (tickCount - lastPathComputeTick >= config.getPathRecalculateIntervalInTicks()) {
            navigateTo(entityPos, targetPos);
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

    private static double entityDistanceSquared(BlockPos entityPos, PathNode node) {
        var dx = entityPos.getX() - node.getX();
        var dy = entityPos.getY() - node.getY();
        var dz = entityPos.getZ() - node.getZ();

        return dx * dx + dy * dy + dz * dz;
    }
}
