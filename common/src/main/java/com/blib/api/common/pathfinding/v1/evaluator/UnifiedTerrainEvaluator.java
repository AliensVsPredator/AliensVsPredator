package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Core terrain evaluator that handles neighbor generation across all supported terrain types.
 * Dispatches neighbor generation based on the current node's terrain type:
 * GROUND uses gravity-based horizontal movement with step-up/fall/diagonal.
 * WATER uses 3D movement in all 6 cardinal directions.
 * Cross-terrain transitions (GROUND↔WATER) are discovered naturally via the classifier.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final int[][] HORIZONTAL_OFFSETS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    private static final int[][] DIAGONAL_OFFSETS = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedTerrainEvaluator.class);

    private final TerrainEvaluatorConfig config;

    private final PathNodePool nodePool;

    private final Map<TerrainType, Float> snapshotCosts;

    private LevelReader level;

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config) {
        this.config = config;
        this.nodePool = new PathNodePool();
        this.snapshotCosts = new EnumMap<>(TerrainType.class);
    }

    @Override
    public void prepare(LevelReader level) {
        this.level = level;
        nodePool.reset();
        snapshotCosts.clear();

        for (var terrainType : config.getSupportedTerrains()) {
            snapshotCosts.put(terrainType, config.getCost(terrainType));
        }
    }

    @Override
    public PathNode getStartNode(BlockPos entityPos) {
        var resolvedPos = findStandablePosition(entityPos);
        var terrainType = classifyOrDefault(resolvedPos);

        return nodePool.getOrCreate(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var resolvedPos = findStandablePosition(targetPos);
        var terrainType = classifyOrDefault(resolvedPos);

        return nodePool.getOrCreate(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType);
    }

    @Override
    public int getNeighbors(PathNode node, PathNode[] neighbors) {
        return switch (node.getTerrainType()) {
            case GROUND -> getGroundNeighbors(node, neighbors);
            case WATER -> getWaterNeighbors(node, neighbors);
            case BREAKABLE -> getBreakableNeighbors(node, neighbors);
            default -> 0;
        };
    }

    @Override
    public float getTerrainCost(TerrainType terrainType) {
        return snapshotCosts.getOrDefault(terrainType, Float.MAX_VALUE);
    }

    @Override
    public void cleanup() {
        this.level = null;
    }

    // --- GROUND neighbor generation ---

    private int getGroundNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        count = addGroundCardinalNeighbors(node, neighbors, count);
        count = addGroundDiagonalNeighbors(node, neighbors, count);
        count = addVerticalBreakableNeighbors(node, neighbors, count);

        return count;
    }

    private int addVerticalBreakableNeighbors(PathNode node, PathNode[] neighbors, int count) {
        var below = tryCreateBreakableNode(new BlockPos(node.getX(), node.getY() - 1, node.getZ()));

        if (below != null) {
            neighbors[count++] = below;
        }

        var above = tryCreateNode(node.getX(), node.getY() + 1, node.getZ());

        if (above == null) {
            above = tryCreateBreakableNode(new BlockPos(node.getX(), node.getY() + 1, node.getZ()));
        }

        if (above != null) {
            neighbors[count++] = above;
        }

        return count;
    }

    private int addGroundCardinalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : HORIZONTAL_OFFSETS) {
            count = addGroundNeighborsForDirection(node, offset[0], offset[1], neighbors, count);
        }

        return count;
    }

    private int addGroundDiagonalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : DIAGONAL_OFFSETS) {
            if (!isDiagonalValid(node, offset[0], offset[1])) {
                continue;
            }

            var sameLevel = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (sameLevel != null) {
                neighbors[count++] = sameLevel;
            }
        }

        return count;
    }

    private int addGroundNeighborsForDirection(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var baseX = from.getX() + dx;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz;

        // Same level.
        var sameLevel = tryCreateNode(baseX, baseY, baseZ);

        if (sameLevel != null) {
            neighbors[count++] = sameLevel;

            // If same-level is walkable (not breakable), no need to check step-up.
            if (sameLevel.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        // Step-up: always check if same-level was null or BREAKABLE.
        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryCreateNode(baseX, baseY + stepUp, baseZ);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                break;
            }
        }

        // Step-down / fall: only if same-level was null (no walkable or breakable block at this level).
        if (sameLevel == null) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var checkPos = new BlockPos(baseX, baseY - stepDown, baseZ);
                var checkState = level.getBlockState(checkPos);

                if (checkState.isSolid()) {
                    break;
                }

                var steppedDown = tryCreateNode(baseX, baseY - stepDown, baseZ);

                if (steppedDown != null && steppedDown.getTerrainType() != TerrainType.BREAKABLE) {
                    neighbors[count++] = steppedDown;
                    break;
                }
            }
        }

        return count;
    }

    // --- BREAKABLE neighbor generation ---

    private int getBreakableNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        for (var offset : HORIZONTAL_OFFSETS) {
            var neighbor = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        var above = tryCreateNode(node.getX(), node.getY() + 1, node.getZ());

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = tryCreateNode(node.getX(), node.getY() - 1, node.getZ());

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    private boolean isDiagonalValid(PathNode from, int dx, int dz) {
        var adjacentX = tryCreateNode(from.getX() + dx, from.getY(), from.getZ());
        var adjacentZ = tryCreateNode(from.getX(), from.getY(), from.getZ() + dz);

        return adjacentX != null && adjacentZ != null;
    }

    // --- WATER neighbor generation ---

    private int getWaterNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        // All 6 cardinal directions for 3D water movement.
        for (var offset : HORIZONTAL_OFFSETS) {
            var neighbor = findWaterNeighbor(node, offset[0], 0, offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        var above = findWaterNeighbor(node, 0, 1, 0);

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = findWaterNeighbor(node, 0, -1, 0);

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    private @Nullable PathNode findWaterNeighbor(PathNode from, int dx, int dy, int dz) {
        var x = from.getX() + dx;
        var y = from.getY() + dy;
        var z = from.getZ() + dz;

        var directNode = tryCreateNode(x, y, z);

        if (directNode != null) {
            return directNode;
        }

        // For horizontal water movement, check step-up to find GROUND at the water edge.
        if (dy == 0) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight() + 1; stepUp++) {
                var steppedUp = tryCreateNode(x, y + stepUp, z);

                if (steppedUp != null) {
                    return steppedUp;
                }
            }
        }

        return null;
    }

    // --- Shared node creation ---

    private @Nullable PathNode tryCreateNode(int x, int y, int z) {
        var pos = new BlockPos(x, y, z);
        var terrainType = config.getTerrainClassifier().classify(level, pos);

        if (terrainType != null && config.supportsTerrain(terrainType)) {
            if (!hasEntityClearance(x, y, z, terrainType)) {
                return null;
            }

            return nodePool.getOrCreate(x, y, z, terrainType);
        }

        var breakableNode = tryCreateBreakableNode(pos);

        if (breakableNode != null) {
            LOGGER.info("[Evaluator] Created BREAKABLE node at ({},{},{}) costMalus={}", x, y, z, breakableNode.getCostMalus());
        }

        return breakableNode;
    }

    private @Nullable PathNode tryCreateBreakableNode(BlockPos pos) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null) {
            LOGGER.info("[Evaluator] No breakability evaluator configured");
            return null;
        }

        if (!config.supportsTerrain(TerrainType.BREAKABLE)) {
            LOGGER.info("[Evaluator] BREAKABLE terrain not supported");
            return null;
        }

        var width = config.getEntityWidth();
        var height = config.getEntityHeight();
        var halfWidth = width / 2;
        var totalCost = 0.0f;
        var hasBreakableBlock = false;

        for (int dx = -halfWidth; dx <= halfWidth; dx++) {
            for (int dz = -halfWidth; dz <= halfWidth; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    var checkPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    var checkState = level.getBlockState(checkPos);

                    if (!checkState.isSolid()) {
                        continue;
                    }

                    var result = breakabilityEvaluator.evaluate(level, checkPos, checkState);

                    if (!result.canBreak()) {
                        return null;
                    }

                    totalCost += result.cost();
                    hasBreakableBlock = true;
                }
            }
        }

        if (!hasBreakableBlock) {
            return null;
        }

        var node = nodePool.getOrCreate(pos.getX(), pos.getY(), pos.getZ(), TerrainType.BREAKABLE);
        node.setCostMalus(totalCost);

        return node;
    }

    private boolean hasEntityClearance(int x, int y, int z, TerrainType terrainType) {
        var width = config.getEntityWidth();
        var height = config.getEntityHeight();
        var halfWidth = width / 2;

        for (int dx = -halfWidth; dx <= halfWidth; dx++) {
            for (int dz = -halfWidth; dz <= halfWidth; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    var checkPos = new BlockPos(x + dx, y + dy, z + dz);
                    var state = level.getBlockState(checkPos);

                    if (state.isSolid()) {
                        return false;
                    }

                    if (terrainType == TerrainType.GROUND && state.liquid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // --- Position resolution ---

    private BlockPos findStandablePosition(BlockPos pos) {
        // Check if the position is already valid (GROUND or WATER).
        var classified = config.getTerrainClassifier().classify(level, pos);

        if (classified != null && config.supportsTerrain(classified)) {
            return pos;
        }

        // Search downward for a valid position.
        var mutablePos = pos.mutable();

        for (int dy = 1; dy <= config.getMaxFallDistance(); dy++) {
            mutablePos.setY(pos.getY() - dy);
            var classifiedBelow = config.getTerrainClassifier().classify(level, mutablePos);

            if (classifiedBelow != null && config.supportsTerrain(classifiedBelow)) {
                return mutablePos.immutable();
            }
        }

        return pos;
    }

    private TerrainType classifyOrDefault(BlockPos pos) {
        var classified = config.getTerrainClassifier().classify(level, pos);

        return classified != null ? classified : TerrainType.GROUND;
    }
}
