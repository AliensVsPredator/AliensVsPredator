package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.debug.PathRejectionReason;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugRecorder;
import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.node.PathBreakOrder;
import com.blib.api.common.pathfinding.v1.node.PathBreakRequirement;
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

    private PathNode getOrCreateNode(int x, int y, int z, TerrainType terrainType) {
        var node = nodePool.getOrCreate(x, y, z, terrainType);

        if (terrainType == TerrainType.GROUND) {
            var support = findStableSupport(x, y, z, null);

            if (support != null) {
                setStableGround(node, support);
            }
        }

        return prepareNode(node);
    }

    private static PathNode prepareNode(PathNode node) {
        node.setPendingTraversal(0.0f, List.of());

        return node;
    }

    private static PathNode prepareNode(PathNode node, float costMalus, List<PathBreakRequirement> breakRequirements) {
        node.setPendingTraversal(costMalus, breakRequirements);

        return node;
    }

    @Override
    public PathNode getStartNode(BlockPos entityPos) {
        var classified = classifyTerrain(entityPos);

        if (classified != null && snapshotCosts.containsKey(classified)) {
            return getOrCreateNode(entityPos.getX(), entityPos.getY(), entityPos.getZ(), classified);
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
                    return getOrCreateNode(
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

        return getOrCreateNode(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var classified = classifyTerrain(targetPos);

        if (classified != null && snapshotCosts.containsKey(classified)) {
            return getOrCreateNode(targetPos.getX(), targetPos.getY(), targetPos.getZ(), classified);
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
                    return getOrCreateNode(
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

        return getOrCreateNode(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ(), terrainType);
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
        for (int drop = 1; drop <= config.getMaxFallDistance() && count < neighbors.length; drop++) {
            var below = tryCreateDownwardBreakableNode(node, drop);

            if (below != null) {
                neighbors[count++] = below;
            }
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
        for (var offset : DIAGONAL_OFFSETS) {
            var neighbor = tryCreateNode(node, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;

                if (neighbor.getTerrainType() != TerrainType.BREAKABLE) {
                    continue;
                }
            }

            var newCount = addDiagonalStepUpNeighbor(node, offset[0], offset[1], neighbors, count);
            if (newCount > count) {
                count = newCount;
                continue;
            }

            newCount = addDiagonalStepDownNeighbor(node, offset[0], offset[1], neighbors, count);
            if (newCount > count) {
                count = newCount;
                continue;
            }

            reject(PathRejectionReason.DIAGONAL_BLOCKED, node.getX() + offset[0], node.getY(), node.getZ() + offset[1]);
        }

        return count;
    }

    private int addDiagonalStepUpNeighbor(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        for (int stepUp = 1; stepUp <= config.getMaxStepHeight() && count < neighbors.length; stepUp++) {
            var steppedUp = tryCreateNode(from, from.getX() + dx, from.getY() + stepUp, from.getZ() + dz);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                break;
            }
        }

        return count;
    }

    private int addDiagonalStepDownNeighbor(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        for (int stepDown = 1; stepDown <= config.getMaxFallDistance() && count < neighbors.length; stepDown++) {
            var steppedDown = tryCreateNode(from, from.getX() + dx, from.getY() - stepDown, from.getZ() + dz);

            if (steppedDown != null && steppedDown.getTerrainType() != TerrainType.BREAKABLE) {
                neighbors[count++] = steppedDown;
                break;
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

        var addedStepUp = false;

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryCreateNode(from, baseX, baseY + stepUp, baseZ);

            if (steppedUp != null) {
                neighbors[count++] = steppedUp;
                addedStepUp = true;
                break;
            }
        }

        if (!addedStepUp && footprintSize() > 1) {
            count = addFootprintStepUpNeighbor(from, dx, dz, neighbors, count);
        }

        if (sameLevel == null) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var steppedDown = tryCreateNode(from, baseX, baseY - stepDown, baseZ);

                if (steppedDown != null && steppedDown.getTerrainType() != TerrainType.BREAKABLE) {
                    neighbors[count++] = steppedDown;
                    break;
                }
            }
        }

        return count;
    }

    private int addFootprintStepUpNeighbor(PathNode from, int dx, int dz, PathNode[] neighbors, int count) {
        var footprint = footprintSize();
        // Wide entities need a full new landing; a one-block step overlaps support with the previous body cavity.
        var baseX = from.getX() + dx * footprint;
        var baseY = from.getY();
        var baseZ = from.getZ() + dz * footprint;

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryCreateNode(from, baseX, baseY + stepUp, baseZ);

            if (steppedUp == null) {
                continue;
            }

            neighbors[count++] = steppedUp;
            break;
        }

        return count;
    }

    // --- BREAKABLE neighbor generation ---

    private int getBreakableNeighbors(PathNode node, PathNode[] neighbors) {
        return getGroundNeighbors(node, neighbors);
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
        mutablePos.set(x, y, z);
        var terrainType = classifyTerrain(mutablePos);

        var support = snapshotCosts.containsKey(TerrainType.GROUND)
            ? findStableSupport(x, y, z, from)
            : null;

        if (support != null && hasEntityClearance(x, y, z, TerrainType.GROUND, from)) {
            var traversal = evaluateTraversalClearance(from, x, y, z, PathBreakOrder.BOTTOM_UP, false);

            if (!traversal.clear()) {
                return null;
            }

            var node = nodePool.getOrCreate(x, y, z, TerrainType.GROUND);
            setStableGround(node, support);

            return prepareNode(node, traversal.costMalus(), traversal.breakRequirements());
        }

        if (terrainType != null && snapshotCosts.containsKey(terrainType)) {
            if (terrainType == TerrainType.GROUND && support == null) {
                reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
                return null;
            }

            if (terrainType != TerrainType.GROUND && hasEntityClearance(x, y, z, terrainType, from)) {
                var traversal = evaluateTraversalClearance(from, x, y, z, PathBreakOrder.BOTTOM_UP, false);

                if (!traversal.clear()) {
                    return null;
                }

                return prepareNode(
                    nodePool.getOrCreate(x, y, z, terrainType),
                    traversal.costMalus(),
                    traversal.breakRequirements()
                );
            }

            var breakable = tryCreateBreakableNode(from, mutablePos.immutable());
            if (breakable == null) {
                reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            }
            return breakable;
        }

        var breakable = tryCreateBreakableNode(from, mutablePos.immutable());
        if (breakable == null) {
            reject(terrainType == null ? PathRejectionReason.UNCLASSIFIED_TERRAIN : PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
        }
        return breakable;
    }

    private @Nullable PathNode tryCreateBreakableNode(@Nullable PathNode from, BlockPos pos) {
        return tryCreateBreakableNode(from, pos, PathBreakOrder.BOTTOM_UP);
    }

    private @Nullable PathNode tryCreateBreakableNode(
        @Nullable PathNode from,
        BlockPos pos,
        PathBreakOrder breakOrder
    ) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null || !snapshotCosts.containsKey(TerrainType.BREAKABLE)) {
            reject(PathRejectionReason.BREAKING_DISABLED, pos);
            return null;
        }

        var support = findStableSupport(pos.getX(), pos.getY(), pos.getZ(), from);

        if (support == null) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, pos);
            return null;
        }

        var height = config.getEntityHeight();
        var totalCost = 0.0f;
        var hasBreakableBlock = false;
        var breakRequirements = new ArrayList<PathBreakRequirement>();

        for (int dx = 0; dx < footprintSize(); dx++) {
            for (int dz = 0; dz < footprintSize(); dz++) {
                var hasBreakableColumn = false;
                var bx = pos.getX() + dx;
                var bz = pos.getZ() + dz;

                for (int dy = 0; dy < height; dy++) {
                    var by = pos.getY() + dy;
                    var checkState = blockAccessor.getBlockState(bx, by, bz);

                    if (!blockAccessor.isSolid(checkState) || isClearedByPath(from, bx, by, bz)) {
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
                    hasBreakableColumn = true;
                }

                if (hasBreakableColumn) {
                    breakRequirements.add(new PathBreakRequirement(bx, pos.getY(), bz, height, breakOrder));
                }
            }
        }

        var traversal = evaluateTraversalClearance(from, pos.getX(), pos.getY(), pos.getZ(), breakOrder, true);

        if (!traversal.clear()) {
            return null;
        }

        totalCost += traversal.costMalus();
        hasBreakableBlock = hasBreakableBlock || !traversal.breakRequirements().isEmpty();
        breakRequirements.addAll(traversal.breakRequirements());

        if (!hasBreakableBlock) {
            return null;
        }

        var node = nodePool.getOrCreate(pos.getX(), pos.getY(), pos.getZ(), TerrainType.BREAKABLE);
        setStableGround(node, support);

        return prepareNode(
            node,
            totalCost,
            List.copyOf(breakRequirements)
        );
    }

    private @Nullable PathNode tryCreateDownwardBreakableNode(PathNode from, int drop) {
        var breakabilityEvaluator = config.getBreakabilityEvaluator();

        if (breakabilityEvaluator == null || !snapshotCosts.containsKey(TerrainType.BREAKABLE)) {
            reject(PathRejectionReason.BREAKING_DISABLED, from.getX(), from.getY() - drop, from.getZ());
            return null;
        }

        var targetY = from.getY() - drop;

        var support = findStableSupport(from.getX(), targetY, from.getZ(), from);

        if (support == null) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, from.getX(), targetY, from.getZ());
            return null;
        }

        var totalCost = 0.0f;
        var hasBreakableBlock = false;
        var breakRequirements = new ArrayList<PathBreakRequirement>();

        for (int dx = 0; dx < footprintSize(); dx++) {
            for (int dz = 0; dz < footprintSize(); dz++) {
                var hasBreakableColumn = false;
                var bx = from.getX() + dx;
                var bz = from.getZ() + dz;

                for (int dy = 0; dy < drop; dy++) {
                    var by = targetY + dy;
                    var checkState = blockAccessor.getBlockState(bx, by, bz);

                    if (!blockAccessor.isSolid(checkState) || isClearedByPath(from, bx, by, bz)) {
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
                    hasBreakableColumn = true;
                }

                if (hasBreakableColumn) {
                    breakRequirements.add(new PathBreakRequirement(
                        bx,
                        targetY,
                        bz,
                        drop,
                        PathBreakOrder.TOP_DOWN
                    ));
                }
            }
        }

        var traversal = evaluateTraversalClearance(
            from,
            from.getX(),
            targetY,
            from.getZ(),
            PathBreakOrder.TOP_DOWN,
            true
        );

        if (!traversal.clear()) {
            return null;
        }

        totalCost += traversal.costMalus();
        hasBreakableBlock = hasBreakableBlock || !traversal.breakRequirements().isEmpty();
        breakRequirements.addAll(traversal.breakRequirements());

        if (!hasBreakableBlock) {
            return null;
        }

        var node = nodePool.getOrCreate(from.getX(), targetY, from.getZ(), TerrainType.BREAKABLE);
        setStableGround(node, support);

        return prepareNode(
            node,
            totalCost,
            List.copyOf(breakRequirements)
        );
    }

    private TraversalClearance evaluateTraversalClearance(
        @Nullable PathNode from,
        int toX,
        int toY,
        int toZ,
        PathBreakOrder breakOrder,
        boolean skipTargetBody
    ) {
        if (from == null) {
            return TraversalClearance.CLEAR;
        }

        var height = config.getEntityHeight();
        var size = footprintSize();
        var minX = Math.min(from.getX(), toX);
        var maxX = Math.max(from.getX(), toX) + size - 1;
        var minY = Math.min(from.getY(), toY);
        var maxY = Math.max(from.getY(), toY) + height - 1;
        var minZ = Math.min(from.getZ(), toZ);
        var maxZ = Math.max(from.getZ(), toZ) + size - 1;
        var breakabilityEvaluator = config.getBreakabilityEvaluator();
        var totalCost = 0.0f;
        var columns = new LinkedHashMap<Long, MutableBreakColumn>();

        for (int bx = minX; bx <= maxX; bx++) {
            for (int bz = minZ; bz <= maxZ; bz++) {
                for (int by = minY; by <= maxY; by++) {
                    if (isInsideBody(from.getX(), from.getY(), from.getZ(), bx, by, bz, height, size)) {
                        continue;
                    }

                    if (skipTargetBody && isInsideBody(toX, toY, toZ, bx, by, bz, height, size)) {
                        continue;
                    }

                    if (isStableDestinationSupport(toX, toY, toZ, bx, by, bz, size, from)) {
                        continue;
                    }

                    if (isClearedByPath(from, bx, by, bz)) {
                        continue;
                    }

                    var state = blockAccessor.getBlockState(bx, by, bz);

                    if (!blockAccessor.isSolid(state)) {
                        continue;
                    }

                    clearancePos.set(bx, by, bz);

                    if (breakabilityEvaluator == null || !snapshotCosts.containsKey(TerrainType.BREAKABLE)) {
                        reject(PathRejectionReason.BREAKING_DISABLED, clearancePos);
                        return TraversalClearance.BLOCKED;
                    }

                    var result = breakabilityEvaluator.evaluate(level, clearancePos, state);

                    if (!result.canBreak()) {
                        reject(PathRejectionReason.UNBREAKABLE_BLOCK, clearancePos);
                        return TraversalClearance.BLOCKED;
                    }

                    totalCost += result.cost();
                    var columnX = bx;
                    var columnZ = bz;
                    columns.computeIfAbsent(
                        packColumnKey(columnX, columnZ),
                        ignored -> new MutableBreakColumn(columnX, columnZ)
                    ).include(by);
                }
            }
        }

        if (columns.isEmpty()) {
            return TraversalClearance.CLEAR;
        }

        var breakRequirements = new ArrayList<PathBreakRequirement>(columns.size());

        for (var column : columns.values()) {
            breakRequirements.add(column.toRequirement(breakOrder));
        }

        return new TraversalClearance(true, totalCost, List.copyOf(breakRequirements));
    }

    private boolean isInsideBody(
        int bodyX,
        int bodyY,
        int bodyZ,
        int x,
        int y,
        int z,
        int height,
        int size
    ) {
        return x >= bodyX && x < bodyX + size
            && y >= bodyY && y < bodyY + height
            && z >= bodyZ && z < bodyZ + size;
    }

    private boolean isStableDestinationSupport(
        int toX,
        int toY,
        int toZ,
        int x,
        int y,
        int z,
        int size,
        PathNode from
    ) {
        return y == toY - 1
            && x >= toX && x < toX + size
            && z >= toZ && z < toZ + size
            && hasStableSupportBlock(x, y, z, from);
    }

    private long packColumnKey(int x, int z) {
        return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
    }

    private record TraversalClearance(
        boolean clear,
        float costMalus,
        List<PathBreakRequirement> breakRequirements
    ) {

        private static final TraversalClearance CLEAR = new TraversalClearance(true, 0.0f, List.of());

        private static final TraversalClearance BLOCKED = new TraversalClearance(false, 0.0f, List.of());
    }

    private static final class MutableBreakColumn {

        private final int x;

        private final int z;

        private int minY = Integer.MAX_VALUE;

        private int maxY = Integer.MIN_VALUE;

        private MutableBreakColumn(int x, int z) {
            this.x = x;
            this.z = z;
        }

        private void include(int y) {
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        private PathBreakRequirement toRequirement(PathBreakOrder order) {
            return new PathBreakRequirement(x, minY, z, maxY - minY + 1, order);
        }
    }

    private void reject(PathRejectionReason reason, BlockPos pos) {
        reject(reason, pos.getX(), pos.getY(), pos.getZ());
    }

    private void reject(PathRejectionReason reason, int x, int y, int z) {
        if (debugRecorder != null) {
            debugRecorder.reject(reason, x, y, z);
        }
    }

    private @Nullable SupportFootprint findStableSupport(int x, int y, int z, @Nullable PathNode from) {
        var supportY = y - 1;
        var size = footprintSize();

        if (hasStableSupportRectangle(x, supportY, z, size, size, from)) {
            return new SupportFootprint(x, supportY, z, size, size);
        }

        var supportDepth = supportDepth(size);

        if (supportDepth >= size) {
            return null;
        }

        for (int dz = 0; dz <= size - supportDepth; dz++) {
            if (hasStableSupportRectangle(x, supportY, z + dz, size, supportDepth, from)) {
                return new SupportFootprint(x, supportY, z + dz, size, supportDepth);
            }
        }

        for (int dx = 0; dx <= size - supportDepth; dx++) {
            if (hasStableSupportRectangle(x + dx, supportY, z, supportDepth, size, from)) {
                return new SupportFootprint(x + dx, supportY, z, supportDepth, size);
            }
        }

        return null;
    }

    private boolean hasStableSupportRectangle(
        int x,
        int y,
        int z,
        int xSize,
        int zSize,
        @Nullable PathNode from
    ) {
        for (int dx = 0; dx < xSize; dx++) {
            for (int dz = 0; dz < zSize; dz++) {
                if (!hasStableSupportBlock(x + dx, y, z + dz, from)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasStableSupportBlock(int x, int y, int z, @Nullable PathNode from) {
        var supportState = blockAccessor.getBlockState(x, y, z);

        return blockAccessor.isSolid(supportState)
            && !blockAccessor.isLiquid(supportState)
            && !isClearedByPath(from, x, y, z);
    }

    private int supportDepth(int footprintSize) {
        return Math.max(1, (footprintSize + 1) / 2);
    }

    private record SupportFootprint(
        int x,
        int y,
        int z,
        int xSize,
        int zSize
    ) {}

    private boolean isClearedByPath(@Nullable PathNode from, int x, int y, int z) {
        var current = from;

        while (current != null) {
            for (var requirement : current.getBreakRequirements()) {
                if (isInsideBreakRequirement(requirement, x, y, z)) {
                    return true;
                }
            }

            current = current.getParent();
        }

        return false;
    }

    private boolean isInsideBreakRequirement(PathBreakRequirement requirement, int x, int y, int z) {
        return x == requirement.x()
            && z == requirement.z()
            && y >= requirement.y()
            && y < requirement.y() + requirement.height();
    }

    private boolean hasEntityClearance(int x, int y, int z, TerrainType terrainType) {
        return hasEntityClearance(x, y, z, terrainType, null);
    }

    private boolean hasEntityClearance(
        int x,
        int y,
        int z,
        TerrainType terrainType,
        @Nullable PathNode from
    ) {
        return hasEntityClearance(x, y, z, terrainType, config.getEntityHeight(), from);
    }

    private boolean hasEntityClearance(int x, int y, int z, TerrainType terrainType, int height) {
        return hasEntityClearance(x, y, z, terrainType, height, null);
    }

    private boolean hasEntityClearance(
        int x,
        int y,
        int z,
        TerrainType terrainType,
        int height,
        @Nullable PathNode from
    ) {
        for (int dx = 0; dx < footprintSize(); dx++) {
            for (int dz = 0; dz < footprintSize(); dz++) {
                for (int dy = 0; dy < height; dy++) {
                    var checkX = x + dx;
                    var checkY = y + dy;
                    var checkZ = z + dz;
                    var state = blockAccessor.getBlockState(checkX, checkY, checkZ);

                    if (blockAccessor.isSolid(state) && !isClearedByPath(from, checkX, checkY, checkZ)) {
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

    private void setStableGround(PathNode node, SupportFootprint support) {
        node.setStableGround(support.x(), support.y(), support.z(), support.xSize(), support.zSize());
    }

    private int footprintSize() {
        return Math.max(1, config.getEntityWidth());
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
