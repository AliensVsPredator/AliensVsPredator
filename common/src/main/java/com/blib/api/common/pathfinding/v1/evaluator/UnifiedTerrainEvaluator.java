package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * Core terrain evaluator that handles neighbor generation across all supported terrain types.
 * Phase 1 supports GROUND only. Future phases add WATER, BREAKABLE, etc.
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

    private LevelReader level;

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config) {
        this.config = config;
        this.nodePool = new PathNodePool();
    }

    @Override
    public void prepare(LevelReader level) {
        this.level = level;
        nodePool.reset();
    }

    @Override
    public PathNode getStartNode(BlockPos entityPos) {
        var groundPos = findGround(entityPos);
        var terrainType = classifyOrDefault(groundPos, TerrainType.GROUND);

        return nodePool.getOrCreate(groundPos.getX(), groundPos.getY(), groundPos.getZ(), terrainType);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var groundPos = findGround(targetPos);
        var terrainType = classifyOrDefault(groundPos, TerrainType.GROUND);

        return nodePool.getOrCreate(groundPos.getX(), groundPos.getY(), groundPos.getZ(), terrainType);
    }

    @Override
    public int getNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        count = addCardinalNeighbors(node, neighbors, count);
        count = addDiagonalNeighbors(node, neighbors, count);

        return count;
    }

    @Override
    public void cleanup() {
        this.level = null;
    }

    private int addCardinalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : HORIZONTAL_OFFSETS) {
            var neighbor = findGroundNeighbor(node, offset[0], offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        return count;
    }

    private int addDiagonalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : DIAGONAL_OFFSETS) {
            var neighbor = findGroundNeighbor(node, offset[0], offset[1]);

            if (neighbor == null) {
                continue;
            }

            if (!isDiagonalValid(node, offset[0], offset[1])) {
                continue;
            }

            neighbors[count++] = neighbor;
        }

        return count;
    }

    private @Nullable PathNode findGroundNeighbor(PathNode from, int dx, int dz) {
        var baseX = from.getX() + dx;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz;

        // Try same level.
        var sameLevel = tryCreateGroundNode(baseX, baseY, baseZ);

        if (sameLevel != null) {
            return sameLevel;
        }

        // Try step-up (up to maxStepHeight).
        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryCreateGroundNode(baseX, baseY + stepUp, baseZ);

            if (steppedUp != null) {
                return steppedUp;
            }
        }

        // Try step-down / fall (up to maxFallDistance).
        for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
            var steppedDown = tryCreateGroundNode(baseX, baseY - stepDown, baseZ);

            if (steppedDown != null) {
                return steppedDown;
            }
        }

        return null;
    }

    private @Nullable PathNode tryCreateGroundNode(int x, int y, int z) {
        var pos = new BlockPos(x, y, z);
        var terrainType = config.getTerrainClassifier().classify(level, pos);

        if (terrainType == null || !config.supportsTerrain(terrainType)) {
            return null;
        }

        if (!hasEntityClearance(x, y, z)) {
            return null;
        }

        return nodePool.getOrCreate(x, y, z, terrainType);
    }

    private boolean hasEntityClearance(int x, int y, int z) {
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
                }
            }
        }

        return true;
    }

    private boolean isDiagonalValid(PathNode from, int dx, int dz) {
        // Check that both cardinal components of the diagonal are passable.
        // Prevents cutting through wall corners.
        var adjacentX = tryCreateGroundNode(from.getX() + dx, from.getY(), from.getZ());
        var adjacentZ = tryCreateGroundNode(from.getX(), from.getY(), from.getZ() + dz);

        return adjacentX != null && adjacentZ != null;
    }

    private BlockPos findGround(BlockPos pos) {
        var mutablePos = pos.mutable();

        // Search downward for a solid block to stand on.
        for (int dy = 0; dy <= config.getMaxFallDistance(); dy++) {
            mutablePos.setY(pos.getY() - dy);
            var below = mutablePos.below();

            if (!level.getBlockState(mutablePos).isSolid() && level.getBlockState(below).isSolid()) {
                return mutablePos.immutable();
            }
        }

        return pos;
    }

    private TerrainType classifyOrDefault(BlockPos pos, TerrainType defaultType) {
        var classified = config.getTerrainClassifier().classify(level, pos);

        return classified != null ? classified : defaultType;
    }
}
