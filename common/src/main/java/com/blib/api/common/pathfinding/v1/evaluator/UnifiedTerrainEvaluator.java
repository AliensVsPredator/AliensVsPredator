package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.debug.PathRejectionReason;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugRecorder;
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

    private final BlockAccessor blockAccessor;

    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    private final BlockPos.MutableBlockPos clearancePos = new BlockPos.MutableBlockPos();

    private LevelReader level;

    private @Nullable PathSearchDebugRecorder debugRecorder;

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config) {
        this(config, null);
    }

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.config = config;
        this.nodePool = new PathNodePool();
        this.snapshotCosts = new EnumMap<>(TerrainType.class);
        this.classificationCache = classificationCache;
        this.blockAccessor = new BlockAccessor();
    }

    @Override
    public void prepare(LevelReader level) {
        this.level = level;
        blockAccessor.prepare(level);
        prepareCommon();
    }

    /**
     * Prepares the evaluator for async (off-thread) pathfinding. The chunk map must be pre-populated via
     * {@link #preloadChunk(int, int, ChunkAccess)} before the search starts. Block reads that miss the map return AIR.
     */
    public void prepareAsync() {
        this.level = null;
        blockAccessor.prepareAsync();
        prepareCommon();
    }

    /**
     * Excludes terrain types from the current search. Excluded terrains are removed from the cost snapshot, making them
     * impassable. Call after {@link #prepare(LevelReader)} or {@link #prepareAsync()}.
     */
    public void excludeTerrains(Set<TerrainType> terrains) {
        for (var terrain : terrains) {
            snapshotCosts.remove(terrain);
        }
    }

    /**
     * Pre-loads a chunk into the evaluator's chunk map for async search. Call from the main thread before dispatching.
     */
    public void preloadChunk(int chunkX, int chunkZ, ChunkAccess chunk) {
        blockAccessor.preloadChunk(chunkX, chunkZ, chunk);
    }

    public void setDebugRecorder(@Nullable PathSearchDebugRecorder debugRecorder) {
        this.debugRecorder = debugRecorder;
    }

    private void prepareCommon() {
        nodePool.reset();
        snapshotCosts.clear();

        for (var terrainType : config.getSupportedTerrains()) {
            snapshotCosts.put(terrainType, config.getCost(terrainType));
        }
    }

    @Override
    public PathNode getStartNode(BlockPos entityPos) {
        var classified = classifyTerrain(entityPos);

        if (classified != null && snapshotCosts.containsKey(classified)) {
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

                if (neighborClassified != null && snapshotCosts.containsKey(neighborClassified)) {
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

        if (classified != null && snapshotCosts.containsKey(classified)) {
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

                if (neighborClassified != null && snapshotCosts.containsKey(neighborClassified)) {
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
        blockAccessor.cleanup();
    }

    // --- GROUND neighbor generation ---

    // Reusable cardinal result cache for diagonal validation (#4).
    // Index: 0=west(-1,0), 1=east(+1,0), 2=north(0,-1), 3=south(0,+1)
    private final PathNode[] cardinalCache = new PathNode[4];

    private int getGroundNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        // Evaluate cardinals and cache results for diagonal reuse.
        for (int i = 0; i < HORIZONTAL_OFFSETS.length; i++) {
            cardinalCache[i] = tryCreateNode(
                node,
                node.getX() + HORIZONTAL_OFFSETS[i][0],
                node.getY(),
                node.getZ() + HORIZONTAL_OFFSETS[i][1]
            );
        }

        count = addGroundCardinalNeighbors(node, neighbors, count);
        count = addGroundDiagonalNeighborsCached(node, neighbors, count);
        count = addVerticalBreakableNeighbors(node, neighbors, count);

        return count;
    }

    private int addVerticalBreakableNeighbors(PathNode node, PathNode[] neighbors, int count) {
        mutablePos.set(node.getX(), node.getY() - 1, node.getZ());
        var below = tryCreateBreakableNode(node, mutablePos.immutable());

        if (below != null) {
            neighbors[count++] = below;
        }

        var above = tryCreateNode(node, node.getX(), node.getY() + 1, node.getZ(), false);

        if (above == null) {
            mutablePos.set(node.getX(), node.getY() + 1, node.getZ());
            above = tryCreateBreakableNode(node, mutablePos.immutable(), false);
        }

        if (above != null) {
            neighbors[count++] = above;
        }

        return count;
    }

    private int addGroundCardinalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (int i = 0; i < HORIZONTAL_OFFSETS.length; i++) {
            count = addGroundNeighborsForDirection(
                node,
                HORIZONTAL_OFFSETS[i][0],
                HORIZONTAL_OFFSETS[i][1],
                cardinalCache[i],
                neighbors,
                count
            );
        }

        return count;
    }

    private int addGroundDiagonalNeighborsCached(PathNode node, PathNode[] neighbors, int count) {
        // DIAGONAL_OFFSETS: (-1,-1), (-1,+1), (+1,-1), (+1,+1)
        // cardinalCache: 0=west(-1,0), 1=east(+1,0), 2=north(0,-1), 3=south(0,+1)
        // For diagonal (dx,dz): need cardinal at (dx,0) and (0,dz).
        // dx=-1 -> index 0 (west), dx=+1 -> index 1 (east)
        // dz=-1 -> index 2 (north), dz=+1 -> index 3 (south)
        for (var offset : DIAGONAL_OFFSETS) {
            var xIdx = offset[0] == -1 ? 0 : 1;
            var zIdx = offset[1] == -1 ? 2 : 3;
            var adjX = cardinalCache[xIdx];
            var adjZ = cardinalCache[zIdx];

            if (
                adjX == null || adjX.getTerrainType() == TerrainType.BREAKABLE
                    || adjZ == null || adjZ.getTerrainType() == TerrainType.BREAKABLE
            ) {
                reject(PathRejectionReason.DIAGONAL_BLOCKED, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);
                continue;
            }

            var neighbor = tryCreateNode(node, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (neighbor != null && neighbor.getTerrainType() != TerrainType.BREAKABLE) {
                neighbors[count++] = neighbor;
            }
        }

        return count;
    }

    private int addGroundNeighborsForDirection(
        PathNode from,
        int dx,
        int dz,
        @Nullable PathNode cachedSameLevel,
        PathNode[] neighbors,
        int count
    ) {
        var baseX = from.getX() + dx;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz;

        var sameLevel = cachedSameLevel;

        if (sameLevel != null) {
            neighbors[count++] = sameLevel;

            if (sameLevel.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        var entityHeight = config.getEntityHeight();
        var headroomClear = !blockAccessor.isSolid(
            blockAccessor.getBlockState(from.getX(), from.getY() + entityHeight, from.getZ())
        );

        if (headroomClear) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateNode(from, baseX, baseY + stepUp, baseZ);

                if (steppedUp != null) {
                    neighbors[count++] = steppedUp;
                    break;
                }
            }
        }

        if (sameLevel == null) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var checkState = blockAccessor.getBlockState(baseX, baseY - stepDown, baseZ);

                if (blockAccessor.isSolid(checkState)) {
                    break;
                }

                var steppedDown = tryCreateNode(from, baseX, baseY - stepDown, baseZ);

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
            var neighbor = tryCreateNode(node, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        var above = tryCreateNode(node, node.getX(), node.getY() + 1, node.getZ(), false);

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = tryCreateNode(node, node.getX(), node.getY() - 1, node.getZ());

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    // --- WATER neighbor generation ---

    private int getWaterNeighbors(PathNode node, PathNode[] neighbors) {
        var count = 0;

        for (var offset : HORIZONTAL_OFFSETS) {
            count = addWaterNeighborsForDirection(node, offset[0], offset[1], neighbors, count);
        }

        count = addWaterDiagonalNeighbors(node, neighbors, count);

        var above = tryCreateNode(node, node.getX(), node.getY() + 1, node.getZ());

        if (above != null) {
            neighbors[count++] = above;
        }

        var below = tryCreateNode(node, node.getX(), node.getY() - 1, node.getZ());

        if (below != null) {
            neighbors[count++] = below;
        }

        return count;
    }

    private int addWaterDiagonalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : DIAGONAL_OFFSETS) {
            var adjacentX = tryCreateNode(node, node.getX() + offset[0], node.getY(), node.getZ());
            var adjacentZ = tryCreateNode(node, node.getX(), node.getY(), node.getZ() + offset[1]);

            if (adjacentX == null || adjacentZ == null) {
                continue;
            }

            var diagonal = tryCreateNode(node, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

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

        var directNode = tryCreateNode(from, x, y, z);

        if (directNode != null) {
            neighbors[count++] = directNode;

            if (directNode.getTerrainType() != TerrainType.BREAKABLE) {
                return count;
            }
        }

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight() + 1; stepUp++) {
            var steppedUp = tryCreateNode(from, x, y + stepUp, z);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                break;
            }
        }

        return count;
    }

    // --- Shared node creation ---

    private @Nullable PathNode tryCreateNode(@Nullable PathNode from, int x, int y, int z) {
        return tryCreateNode(from, x, y, z, true);
    }

    private @Nullable PathNode tryCreateNode(@Nullable PathNode from, int x, int y, int z, boolean requireBreakableSupport) {
        mutablePos.set(x, y, z);
        var terrainType = classifyTerrain(mutablePos);

        if (terrainType != null && snapshotCosts.containsKey(terrainType)) {
            if (terrainType == TerrainType.GROUND && !hasStableSupport(x, y, z, from)) {
                reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
                return null;
            }

            if (hasEntityClearance(x, y, z, terrainType)) {
                return nodePool.getOrCreate(x, y, z, terrainType);
            }

            var breakable = tryCreateBreakableNode(from, mutablePos.immutable(), requireBreakableSupport);
            if (breakable == null) {
                reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            }
            return breakable;
        }

        var breakable = tryCreateBreakableNode(from, mutablePos.immutable(), requireBreakableSupport);
        if (breakable == null) {
            reject(terrainType == null ? PathRejectionReason.UNCLASSIFIED_TERRAIN : PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
        }
        return breakable;
    }

    private @Nullable PathNode tryCreateBreakableNode(@Nullable PathNode from, BlockPos pos) {
        return tryCreateBreakableNode(from, pos, true);
    }

    private @Nullable PathNode tryCreateBreakableNode(@Nullable PathNode from, BlockPos pos, boolean requireStableSupport) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null || !snapshotCosts.containsKey(TerrainType.BREAKABLE)) {
            reject(PathRejectionReason.BREAKING_DISABLED, pos);
            return null;
        }

        if (requireStableSupport && !hasStableSupport(pos.getX(), pos.getY(), pos.getZ(), from)) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, pos);
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
                    var bx = pos.getX() + dx;
                    var by = pos.getY() + dy;
                    var bz = pos.getZ() + dz;
                    var checkState = blockAccessor.getBlockState(bx, by, bz);

                    if (!blockAccessor.isSolid(checkState)) {
                        continue;
                    }

                    clearancePos.set(bx, by, bz);
                    var result = breakabilityEvaluator.evaluate(level, clearancePos, checkState);

                    if (!result.canBreak()) {
                        reject(PathRejectionReason.UNBREAKABLE_BLOCK, clearancePos);
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

    private void reject(PathRejectionReason reason, BlockPos pos) {
        reject(reason, pos.getX(), pos.getY(), pos.getZ());
    }

    private void reject(PathRejectionReason reason, int x, int y, int z) {
        if (debugRecorder != null) {
            debugRecorder.reject(reason, x, y, z);
        }
    }

    private boolean hasStableSupport(int x, int y, int z, @Nullable PathNode from) {
        var supportY = y - 1;
        var supportState = blockAccessor.getBlockState(x, supportY, z);

        if (!blockAccessor.isSolid(supportState) || blockAccessor.isLiquid(supportState)) {
            return false;
        }

        return !isClearedByPath(from, x, supportY, z);
    }

    private boolean isClearedByPath(@Nullable PathNode from, int x, int y, int z) {
        var current = from;

        while (current != null) {
            if (current.getTerrainType() == TerrainType.BREAKABLE && isInsideBreakableClearance(current, x, y, z)) {
                return true;
            }

            current = current.getParent();
        }

        return false;
    }

    private boolean isInsideBreakableClearance(PathNode node, int x, int y, int z) {
        var halfWidth = config.getEntityWidth() / 2;

        return x >= node.getX() - halfWidth
            && x <= node.getX() + halfWidth
            && z >= node.getZ() - halfWidth
            && z <= node.getZ() + halfWidth
            && y >= node.getY()
            && y < node.getY() + config.getEntityHeight();
    }

    private boolean hasEntityClearance(int x, int y, int z, TerrainType terrainType) {
        var width = config.getEntityWidth();
        var height = config.getEntityHeight();
        var halfWidth = width / 2;

        for (int dx = -halfWidth; dx <= halfWidth; dx++) {
            for (int dz = -halfWidth; dz <= halfWidth; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    var state = blockAccessor.getBlockState(x + dx, y + dy, z + dz);

                    if (blockAccessor.isSolid(state)) {
                        return false;
                    }

                    if (terrainType == TerrainType.GROUND && blockAccessor.isLiquid(state)) {
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

        if (classified != null && snapshotCosts.containsKey(classified)) {
            return pos;
        }

        var mutablePos = pos.mutable();

        for (int dy = 1; dy <= config.getMaxFallDistance(); dy++) {
            mutablePos.setY(pos.getY() - dy);
            var classifiedBelow = classifyTerrain(mutablePos);

            if (classifiedBelow != null && snapshotCosts.containsKey(classifiedBelow)) {
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
            // In async mode (level == null), only read pre-populated cache entries.
            if (level == null) {
                return classificationCache.getClassificationIfCached(pos);
            }

            return classificationCache.getClassification(level, pos);
        }

        if (level == null) {
            return null;
        }

        return config.getTerrainClassifier().classify(level, pos);
    }
}
