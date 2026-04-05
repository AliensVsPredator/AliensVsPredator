package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * The consuming code calls {@link #tick(double, double, double, float, float, int, boolean)} each tick with the entity's exact
 * position, bounding box dimensions, and climbing surface. The navigator advances along the path and provides the next
 * waypoint via {@link #getCurrentTargetPos()}. The calling code is responsible for actually moving the entity toward
 * the waypoint.
 * </p>
 */
public final class PathNavigator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathNavigator.class);

    private final PathNavigatorConfig config;

    private final BLibPathFinder pathFinder;

    private final LevelReader level;

    private @Nullable BLibPath currentPath;

    private @Nullable BlockPos targetPos;

    private @Nullable BlockPos lastComputedTargetPos;

    private @Nullable TerrainType currentTerrain;

    private int currentPostureIndex;

    private int currentSurfaceDirection;

    private boolean waitingForBlockBreak;

    private int lastPathComputeTick;

    private int lastProgressTick;

    private double lastDistanceToTarget;

    private int tickCount;

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
        this.targetPos = target;
        this.lastComputedTargetPos = target;

        var startTime = System.nanoTime();
        this.currentPath = pathFinder.findPath(level, entityPos, target);
        var elapsedMicros = (System.nanoTime() - startTime) / 1000;

        this.lastPathComputeTick = tickCount;
        this.lastProgressTick = tickCount;
        this.lastDistanceToTarget = Double.MAX_VALUE;

        if (currentPath != null) {
            var startNode = currentPath.getCurrentNode();

            this.currentTerrain = startNode.getTerrainType();
            this.currentPostureIndex = startNode.getPostureIndex();
            config.firePostureEnter(currentPostureIndex);

            var newSurface = startNode.getSurfaceDirection();

            if (newSurface != currentSurfaceDirection) {
                config.fireSurfaceDirectionChange(currentSurfaceDirection, newSurface);
                currentSurfaceDirection = newSurface;
            }

            LOGGER.info(
                "[Pathfinding] {}µs | {} nodes | reached={} | from={} to={}",
                elapsedMicros,
                currentPath.getNodeCount(),
                currentPath.isReached(),
                entityPos,
                target
            );

            var nodeCount = currentPath.getNodeCount();
            var dumpStart = Math.max(0, nodeCount - 6);

            for (int i = dumpStart; i < nodeCount; i++) {
                var node = currentPath.getNode(i);

                LOGGER.info(
                    "[Pathfinding]   node[{}] ({},{},{}) terrain={} posture={} surface={}",
                    i,
                    node.getX(),
                    node.getY(),
                    node.getZ(),
                    node.getTerrainType(),
                    node.getPostureIndex(),
                    node.getSurfaceDirection()
                );
            }
        } else {
            LOGGER.info("[Pathfinding] {}µs | no path | from={} to={}", elapsedMicros, entityPos, target);
        }

        return currentPath != null;
    }

    /**
     * Advances the navigator one tick. Call this every tick with the entity's exact position. The navigator checks
     * waypoint proximity using per-axis distance and entity dimensions, advances the path, fires transition handlers,
     * and detects stuck conditions.
     *
     * @param entityX                exact X position of the entity
     * @param entityY                exact Y position of the entity (feet)
     * @param entityZ                exact Z position of the entity
     * @param entityWidth            bounding box width of the entity
     * @param entityHeight           bounding box height of the entity
     * @param entitySurfaceDirection the entity's current climbing surface ordinal (0 if not climbing)
     * @param entityOnGround         whether the entity is currently on the ground
     */
    public void tick(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight,
        int entitySurfaceDirection,
        boolean entityOnGround
    ) {
        tickCount++;

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight, entitySurfaceDirection, entityOnGround);

        var entityBlockPos = BlockPos.containing(entityX, entityY, entityZ);

        if (currentPath.isDone()) {
            LOGGER.info(
                "[PathNav] path completed | entityPos=({},{},{}) targetPos={} reached={} nodeIdx={}/{}",
                String.format("%.2f", entityX),
                String.format("%.2f", entityY),
                String.format("%.2f", entityZ),
                targetPos,
                currentPath.isReached(),
                currentPath.getCurrentNodeIndex(),
                currentPath.getNodeCount()
            );

            resetPosture();
            return;
        }

        if (waitingForBlockBreak) {
            return;
        }

        detectStuck(entityBlockPos);
        checkRecalculate(entityBlockPos);
    }

    /**
     * Stops navigation and clears the current path.
     */
    public void stop() {
        this.currentPath = null;
        this.targetPos = null;
        this.currentTerrain = null;
        resetPosture();
        this.waitingForBlockBreak = false;
    }

    public PathNavigatorConfig getConfig() {
        return config;
    }

    /**
     * Returns the snapshot from the most recent A* search, or null if no search has been performed.
     */
    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return pathFinder.getLastSearchSnapshot();
    }

    /**
     * Returns the surface direction ordinal of the current path node. Only meaningful when {@link #getCurrentTerrain()}
     * is {@link TerrainType#CLIMBABLE}. Maps to {@link net.minecraft.core.Direction#ordinal()}: 0=DOWN, 1=UP, 2=NORTH,
     * 3=SOUTH, 4=WEST, 5=EAST.
     */
    public int getCurrentSurfaceDirection() {
        return currentSurfaceDirection;
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
    private void resetPosture() {
        if (currentPostureIndex != 0) {
            currentPostureIndex = 0;
            config.firePostureEnter(0);
        }

        if (currentSurfaceDirection != 0) {
            config.fireSurfaceDirectionChange(currentSurfaceDirection, 0);
            currentSurfaceDirection = 0;
        }
    }

    public void updateTarget(BlockPos newTarget) {
        this.targetPos = newTarget;
    }

    private void advanceWaypoints(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight,
        int entitySurfaceDirection,
        boolean entityOnGround
    ) {
        var reachXZ = entityWidth > 0.75f ? entityWidth / 2.0 : 0.75 - entityWidth / 2.0;
        var reachY = Math.max(1.0, entityHeight > 0.75f ? entityHeight / 2.0 : 0.75 - entityHeight / 2.0);
        var nodeCenterOffset = (int) (entityWidth + 1.0f) * 0.5;

        while (!currentPath.isDone()) {
            var waypoint = currentPath.getCurrentNode();

            var dx = Math.abs(waypoint.getX() + nodeCenterOffset - entityX);
            var dy = Math.abs(waypoint.getY() - entityY);
            var dz = Math.abs(waypoint.getZ() + nodeCenterOffset - entityZ);

            if (dx > reachXZ || dy > reachY || dz > reachXZ) {
                break;
            }

            // For climbable nodes, the entity must be on the matching surface before advancing.
            // Prevents skipping a side node while still on the ceiling (crawling-port approach).
            if (
                waypoint.getTerrainType() == TerrainType.CLIMBABLE
                    && waypoint.getSurfaceDirection() > 0
                    && entitySurfaceDirection > 0
                    && entitySurfaceDirection != waypoint.getSurfaceDirection()
            ) {
                break;
            }

            // For ground nodes, the entity must actually be on the ground before advancing.
            // Prevents completing a path while still climbing the side of a bridge.
            if (waypoint.getTerrainType() == TerrainType.GROUND && entitySurfaceDirection > 0 && !entityOnGround) {
                break;
            }

            var previousTerrain = currentTerrain;

            currentPath.advance();
            lastProgressTick = tickCount;
            lastDistanceToTarget = Double.MAX_VALUE;

            if (!currentPath.isDone()) {
                var nextNode = currentPath.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();
                var newPosture = nextNode.getPostureIndex();

                if (newPosture != currentPostureIndex) {
                    LOGGER.info(
                        "[PathNav] posture {} -> {} at node ({},{},{}) entity=({},{},{})",
                        currentPostureIndex,
                        newPosture,
                        nextNode.getX(),
                        nextNode.getY(),
                        nextNode.getZ(),
                        String.format("%.2f", entityX),
                        String.format("%.2f", entityY),
                        String.format("%.2f", entityZ)
                    );

                    currentPostureIndex = newPosture;
                    config.firePostureEnter(newPosture);
                }

                var newSurface = nextNode.getSurfaceDirection();

                if (newSurface != currentSurfaceDirection) {
                    LOGGER.info(
                        "[PathNav] surface {} -> {} at node ({},{},{}) terrain={} entity=({},{},{})",
                        currentSurfaceDirection,
                        newSurface,
                        nextNode.getX(),
                        nextNode.getY(),
                        nextNode.getZ(),
                        newTerrain,
                        String.format("%.2f", entityX),
                        String.format("%.2f", entityY),
                        String.format("%.2f", entityZ)
                    );

                    var previousSurface = currentSurfaceDirection;

                    config.fireSurfaceDirectionChange(currentSurfaceDirection, newSurface);
                    currentSurfaceDirection = newSurface;

                    if (newTerrain == TerrainType.CLIMBABLE && previousSurface > 0) {
                        break;
                    }
                }

                if (newTerrain == TerrainType.BREAKABLE) {
                    waitingForBlockBreak = true;
                    currentTerrain = newTerrain;
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    break;
                }

                if (newTerrain != previousTerrain) {
                    LOGGER.info(
                        "[PathNav] terrain {} -> {} at node ({},{},{}) surface={} entity=({},{},{})",
                        previousTerrain,
                        newTerrain,
                        nextNode.getX(),
                        nextNode.getY(),
                        nextNode.getZ(),
                        newSurface,
                        String.format("%.2f", entityX),
                        String.format("%.2f", entityY),
                        String.format("%.2f", entityZ)
                    );

                    fireTransitionHandlers(previousTerrain, newTerrain);
                    currentTerrain = newTerrain;
                    break;
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

        var ticksSinceProgress = tickCount - lastProgressTick;

        if (ticksSinceProgress >= config.getStuckTimeoutInTicks()) {
            var node = currentPath != null && !currentPath.isDone() ? currentPath.getCurrentNode() : null;

            LOGGER.info(
                "[Stuck] path abandoned after {} ticks | entityPos={} targetPos={} distSqr={} node={} terrain={} surface={}",
                ticksSinceProgress,
                entityPos,
                targetPos,
                String.format("%.2f", currentDistance),
                node != null ? "(%d,%d,%d)".formatted(node.getX(), node.getY(), node.getZ()) : "none",
                currentTerrain,
                currentSurfaceDirection
            );

            stop();
        } else if (ticksSinceProgress > 0 && tickCount % 20 == 0) {
            LOGGER.info(
                "[StuckWatch] no progress for {} ticks (timeout={}) | entityPos={} distToTarget={}",
                ticksSinceProgress,
                config.getStuckTimeoutInTicks(),
                entityPos,
                String.format("%.2f", Math.sqrt(currentDistance))
            );
        }
    }

    private static final double MIN_TARGET_MOVE_DISTANCE_SQUARED = 9.0;

    private void checkRecalculate(BlockPos entityPos) {
        if (targetPos == null) {
            return;
        }

        if (tickCount - lastPathComputeTick < config.getPathRecalculateIntervalInTicks()) {
            return;
        }

        var targetMoved = lastComputedTargetPos == null
            || targetPos.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED;

        if (targetMoved) {
            LOGGER.info(
                "[PathNav] recalculating | entityPos={} targetPos={} lastComputedTarget={} ticksSinceCompute={}",
                entityPos,
                targetPos,
                lastComputedTargetPos,
                tickCount - lastPathComputeTick
            );

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

}
