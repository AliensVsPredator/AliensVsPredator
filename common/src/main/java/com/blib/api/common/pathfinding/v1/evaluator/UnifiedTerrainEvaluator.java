package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Core terrain evaluator. Generates ground, water, and breakable neighbors for A* search.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final int[][] HORIZONTAL_OFFSETS = {
        { -1, 0 },
        { 1, 0 },
        { 0, -1 },
        { 0, 1 }
    };

    private static final int[][] DIAGONAL_OFFSETS = {
        { -1, -1 },
        { -1, 1 },
        { 1, -1 },
        { 1, 1 }
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
        var classified = classifyTerrain(entityPos);

        if (classified != null && config.supportsTerrain(classified)) {
            return nodePool.getOrCreate(entityPos.getX(), entityPos.getY(), entityPos.getZ(), classified);
        }

        // Entity might be on the edge/corner of an adjacent block (blockPosition floors to air).
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                var neighborPos = entityPos.offset(dx, 0, dz);
                var neighborClassified = classifyTerrain(neighborPos);

                if (neighborClassified != null && config.supportsTerrain(neighborClassified)) {
                    return nodePool.getOrCreate(
                        neighborPos.getX(),
                        neighborPos.getY(),
                        neighborPos.getZ(),
                        neighborClassified
                    );
                }
            }
        }

        var resolvedPos = findStandablePosition(entityPos);
        var terrainType = classifyOrDefault(resolvedPos);

        return nodePool.getOrCreate(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var classified = classifyTerrain(targetPos);

        if (classified != null && config.supportsTerrain(classified)) {
            return nodePool.getOrCreate(targetPos.getX(), targetPos.getY(), targetPos.getZ(), classified);
        }

        // Target might be on the edge/corner of an adjacent block (blockPosition floors to air).
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                var neighborPos = targetPos.offset(dx, 0, dz);
                var neighborClassified = classifyTerrain(neighborPos);

                if (neighborClassified != null && config.supportsTerrain(neighborClassified)) {
                    return nodePool.getOrCreate(
                        neighborPos.getX(),
                        neighborPos.getY(),
                        neighborPos.getZ(),
                        neighborClassified
                    );
                }
            }
        }

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

            var neighbor = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (neighbor != null && neighbor.getTerrainType() != TerrainType.BREAKABLE) {
                neighbors[count++] = neighbor;
            }
        }

        return count;
    }

    private int addGroundNeighborsForDirection(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var baseX = from.getX() + dx;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz;

        var sameLevel = tryCreateNode(baseX, baseY, baseZ);

        if (sameLevel != null) {
            neighbors[count++] = sameLevel;

            if (sameLevel.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        var entityHeight = config.getEntityHeight();
        var headroomPos = new BlockPos(from.getX(), from.getY() + entityHeight, from.getZ());
        var headroomClear = !level.getBlockState(headroomPos).isSolid();

        if (headroomClear) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateNode(baseX, baseY + stepUp, baseZ);

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

        return adjacentX != null && adjacentX.getTerrainType() != TerrainType.BREAKABLE
            && adjacentZ != null && adjacentZ.getTerrainType() != TerrainType.BREAKABLE;
    }

    // --- WATER neighbor generation ---

    private int getWaterNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        for (var offset : HORIZONTAL_OFFSETS) {
            count = addWaterNeighborsForDirection(node, offset[0], offset[1], neighbors, count);
        }

        count = addWaterDiagonalNeighbors(node, neighbors, count);

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

    private int addWaterDiagonalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : DIAGONAL_OFFSETS) {
            var adjacentX = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ());
            var adjacentZ = tryCreateNode(node.getX(), node.getY(), node.getZ() + offset[1]);

            if (adjacentX == null || adjacentZ == null) {
                continue;
            }

            var diagonal = tryCreateNode(node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (diagonal != null && diagonal.getTerrainType() != TerrainType.BREAKABLE) {
                neighbors[count++] = diagonal;
            }
        }

        return count;
    }

    private int addWaterNeighborsForDirection(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var x = from.getX() + dx;
        var y = from.getY();
        var z = from.getZ() + dz;

        var directNode = tryCreateNode(x, y, z);

        if (directNode != null) {
            neighbors[count++] = directNode;

            if (directNode.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight() + 1; stepUp++) {
            var steppedUp = tryCreateNode(x, y + stepUp, z);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                break;
            }
        }

        return count;
    }

    // --- Shared node creation ---

    private @Nullable PathNode tryCreateNode(int x, int y, int z) {
        var pos = new BlockPos(x, y, z);
        var terrainType = classifyTerrain(pos);

        if (terrainType != null && config.supportsTerrain(terrainType)) {
            if (!hasEntityClearance(x, y, z, terrainType)) {
                return null;
            }

            return nodePool.getOrCreate(x, y, z, terrainType);
        }

        return tryCreateBreakableNode(pos);
    }

    private @Nullable PathNode tryCreateBreakableNode(BlockPos pos) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null || !config.supportsTerrain(TerrainType.BREAKABLE)) {
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
