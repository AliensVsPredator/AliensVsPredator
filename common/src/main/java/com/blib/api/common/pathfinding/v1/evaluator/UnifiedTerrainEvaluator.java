package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Core terrain evaluator with multi-posture support. Each node carries a posture index.
 * The evaluator generates neighbors for the current posture AND posture-switch neighbors
 * at the same position. The A* naturally finds paths that switch postures when cheaper.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final int[][] HORIZONTAL_OFFSETS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    private static final int[][] DIAGONAL_OFFSETS = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    private final TerrainEvaluatorConfig config;

    private final PathNodePool nodePool;

    private final Map<TerrainType, Float> snapshotCosts;

    private final @Nullable TerrainClassificationCache classificationCache;

    private LevelReader level;

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config) {
        this(config, null);
    }

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.config = config;
        this.nodePool = new PathNodePool();
        this.snapshotCosts = new EnumMap<>(TerrainType.class);
        this.classificationCache = classificationCache;
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

        return nodePool.getOrCreate(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType, 0);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var resolvedPos = findStandablePosition(targetPos);
        var terrainType = classifyOrDefault(resolvedPos);

        return nodePool.getOrCreate(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType, 0);
    }

    @Override
    public int getNeighbors(PathNode node, PathNode[] neighbors) {
        var count = switch (node.getTerrainType()) {
            case GROUND -> getGroundNeighbors(node, neighbors);
            case WATER -> getWaterNeighbors(node, neighbors);
            case BREAKABLE -> getBreakableNeighbors(node, neighbors);
            default -> 0;
        };

        count = addPostureSwitchNeighbors(node, neighbors, count);

        return count;
    }

    @Override
    public float getTerrainCost(TerrainType terrainType) {
        return snapshotCosts.getOrDefault(terrainType, Float.MAX_VALUE);
    }

    @Override
    public void cleanup() {
        this.level = null;
    }

    // --- Posture switch ---

    private int addPostureSwitchNeighbors(PathNode node, PathNode[] neighbors, int count) {
        var currentPosture = node.getPostureIndex();

        for (int p = 0; p < config.getPostureCount(); p++) {
            if (p == currentPosture) {
                continue;
            }

            if (!hasEntityClearance(node.getX(), node.getY(), node.getZ(), node.getTerrainType(), p)) {
                continue;
            }

            var switchNode = nodePool.getOrCreate(node.getX(), node.getY(), node.getZ(), node.getTerrainType(), p);
            switchNode.setCostMalus(config.getPostureTransitionCost(p));
            neighbors[count++] = switchNode;
        }

        return count;
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
        var posture = node.getPostureIndex();
        var below = tryCreateBreakableNode(new BlockPos(node.getX(), node.getY() - 1, node.getZ()), posture);

        if (below != null) {
            neighbors[count++] = below;
        }

        var above = tryCreateNode(node.getX(), node.getY() + 1, node.getZ(), posture);

        if (above == null) {
            above = tryCreateBreakableNode(new BlockPos(node.getX(), node.getY() + 1, node.getZ()), posture);
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
        var posture = node.getPostureIndex();

        for (var offset : DIAGONAL_OFFSETS) {
            if (!isDiagonalValid(node, offset[0], offset[1])) {
                continue;
            }

            var neighbor = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1], posture);

            if (neighbor != null && neighbor.getTerrainType() != TerrainType.BREAKABLE) {
                neighbors[count++] = neighbor;
            }
        }

        return count;
    }

    private int addGroundNeighborsForDirection(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var posture = from.getPostureIndex();
        var baseX = from.getX() + dx;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz;

        var sameLevel = tryCreateNode(baseX, baseY, baseZ, posture);

        if (sameLevel != null) {
            neighbors[count++] = sameLevel;

            if (sameLevel.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        var entityHeight = config.getEntityHeight(posture);
        var headroomPos = new BlockPos(from.getX(), from.getY() + entityHeight, from.getZ());
        var headroomClear = !level.getBlockState(headroomPos).isSolid();

        if (headroomClear) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateNode(baseX, baseY + stepUp, baseZ, posture);

                if (steppedUp != null) {
                    neighbors[count++] = steppedUp;
                    break;
                }
            }
        }

        if (sameLevel == null) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var checkPos = new BlockPos(baseX, baseY - stepDown, baseZ);
                var checkState = level.getBlockState(checkPos);

                if (checkState.isSolid()) {
                    break;
                }

                var steppedDown = tryCreateNode(baseX, baseY - stepDown, baseZ, posture);

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
        var posture = node.getPostureIndex();

        for (var offset : HORIZONTAL_OFFSETS) {
            var neighbor = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1], posture);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        var above = tryCreateNode(node.getX(), node.getY() + 1, node.getZ(), posture);

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = tryCreateNode(node.getX(), node.getY() - 1, node.getZ(), posture);

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    private boolean isDiagonalValid(PathNode from, int dx, int dz) {
        var posture = from.getPostureIndex();
        var adjacentX = tryCreateNode(from.getX() + dx, from.getY(), from.getZ(), posture);
        var adjacentZ = tryCreateNode(from.getX(), from.getY(), from.getZ() + dz, posture);

        return adjacentX != null && adjacentX.getTerrainType() != TerrainType.BREAKABLE
            && adjacentZ != null && adjacentZ.getTerrainType() != TerrainType.BREAKABLE;
    }

    // --- WATER neighbor generation ---

    private int getWaterNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;
        var posture = node.getPostureIndex();

        for (var offset : HORIZONTAL_OFFSETS) {
            count = addWaterNeighborsForDirection(node, offset[0], offset[1], neighbors, count);
        }

        var above = tryCreateNode(node.getX(), node.getY() + 1, node.getZ(), posture);

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = tryCreateNode(node.getX(), node.getY() - 1, node.getZ(), posture);

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    private int addWaterNeighborsForDirection(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var posture = from.getPostureIndex();
        var x = from.getX() + dx;
        var y = from.getY();
        var z = from.getZ() + dz;

        var directNode = tryCreateNode(x, y, z, posture);

        if (directNode != null) {
            neighbors[count++] = directNode;

            if (directNode.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight() + 1; stepUp++) {
            var steppedUp = tryCreateNode(x, y + stepUp, z, posture);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                break;
            }
        }

        return count;
    }

    // --- Shared node creation ---

    private @Nullable PathNode tryCreateNode(int x, int y, int z, int postureIndex) {
        var pos = new BlockPos(x, y, z);
        var terrainType = classifyTerrain(pos);

        if (terrainType != null && config.supportsTerrain(terrainType)) {
            if (!hasEntityClearance(x, y, z, terrainType, postureIndex)) {
                return null;
            }

            return nodePool.getOrCreate(x, y, z, terrainType, postureIndex);
        }

        return tryCreateBreakableNode(pos, postureIndex);
    }

    private @Nullable PathNode tryCreateBreakableNode(BlockPos pos, int postureIndex) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null || !config.supportsTerrain(TerrainType.BREAKABLE)) {
            return null;
        }

        var width = config.getEntityWidth(postureIndex);
        var height = config.getEntityHeight(postureIndex);
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

        var node = nodePool.getOrCreate(pos.getX(), pos.getY(), pos.getZ(), TerrainType.BREAKABLE, postureIndex);
        node.setCostMalus(totalCost);

        return node;
    }

    private boolean hasEntityClearance(int x, int y, int z, TerrainType terrainType, int postureIndex) {
        var width = config.getEntityWidth(postureIndex);
        var height = config.getEntityHeight(postureIndex);
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
        var classified = classifyTerrain(pos);

        if (classified != null && config.supportsTerrain(classified)) {
            return pos;
        }

        var mutablePos = pos.mutable();

        for (int dy = 1; dy <= config.getMaxFallDistance(); dy++) {
            mutablePos.setY(pos.getY() - dy);
            var classifiedBelow = classifyTerrain(mutablePos);

            if (classifiedBelow != null && config.supportsTerrain(classifiedBelow)) {
                return mutablePos.immutable();
            }
        }

        return pos;
    }

    private TerrainType classifyOrDefault(BlockPos pos) {
        var classified = classifyTerrain(pos);

        return classified != null ? classified : TerrainType.GROUND;
    }

    private @Nullable TerrainType classifyTerrain(BlockPos pos) {
        if (classificationCache != null) {
            return classificationCache.getClassification(level, pos);
        }

        return config.getTerrainClassifier().classify(level, pos);
    }
}
