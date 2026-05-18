package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.PathRejectionReason;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugRecorder;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Minimal ground-only terrain evaluator. A ground node is valid when the feet cell is open and the block below it is
 * solid. Neighbor generation scans adjacent columns for same-level, step-up, and step-down/fall positions.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final int[][] CARDINAL_OFFSETS = {
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

    private final BlockAccessor blockAccessor;

    private @Nullable PathSearchDebugRecorder debugRecorder;

    private PathfindingFeatures features = PathfindingProfile.LEGACY_PERMISSIVE.features();

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config) {
        this(config, null);
    }

    public UnifiedTerrainEvaluator(TerrainEvaluatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.config = config;
        this.nodePool = new PathNodePool();
        this.snapshotCosts = new EnumMap<>(TerrainType.class);
        this.blockAccessor = new BlockAccessor();
    }

    @Override
    public void prepare(LevelReader level) {
        blockAccessor.prepare(level);
        prepareCommon();
    }

    /**
     * Prepares the evaluator for async (off-thread) pathfinding. The chunk map must be pre-populated via
     * {@link #preloadChunk(int, int, ChunkAccess)} before the search starts. Block reads that miss the map return AIR.
     */
    public void prepareAsync() {
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

    public void setFeatures(PathfindingFeatures features) {
        this.features = features;
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
        var resolvedPos = findStandablePosition(entityPos);

        return getOrCreateGroundNode(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ());
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var resolvedPos = findStandablePosition(targetPos);

        return getOrCreateGroundNode(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ());
    }

    @Override
    public int getNeighbors(PathNode node, PathNode[] neighbors) {
        if (node.getTerrainType() != TerrainType.GROUND || !snapshotCosts.containsKey(TerrainType.GROUND)) {
            return 0;
        }

        var count = 0;

        count = appendGroundNeighbors(node, neighbors, count, CARDINAL_OFFSETS);

        if (features.diagonalMovement()) {
            count = appendGroundNeighbors(node, neighbors, count, DIAGONAL_OFFSETS);
        }

        return count;
    }

    @Override
    public float getTerrainCost(TerrainType terrainType) {
        return snapshotCosts.getOrDefault(terrainType, Float.MAX_VALUE);
    }

    @Override
    public void cleanup() {
        blockAccessor.cleanup();
    }

    private int appendGroundNeighbors(PathNode node, PathNode[] neighbors, int count, int[][] offsets) {
        for (var offset : offsets) {
            var neighbor = findGroundNeighbor(node, offset[0], offset[1]);

            if (neighbor != null) {
                neighbors[count++] = neighbor;
            }
        }

        return count;
    }

    private @Nullable PathNode findGroundNeighbor(PathNode from, int dx, int dz) {
        var x = from.getX() + dx;
        var z = from.getZ() + dz;

        if (features.sameLevelMovement()) {
            var sameLevel = tryCreateGroundNode(x, from.getY(), z);
            if (sameLevel != null) {
                return sameLevel;
            }
        }

        if (features.stepUp()) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateGroundNode(x, from.getY() + stepUp, z);

                if (steppedUp != null) {
                    return steppedUp;
                }
            }
        }

        if (features.stepDown()) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var steppedDown = tryCreateGroundNode(x, from.getY() - stepDown, z);

                if (steppedDown != null) {
                    return steppedDown;
                }
            }
        }

        return null;
    }

    private @Nullable PathNode tryCreateGroundNode(int x, int y, int z) {
        if (!snapshotCosts.containsKey(TerrainType.GROUND)) {
            reject(PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
            return null;
        }

        if (!isFeetOpen(x, y, z)) {
            reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            return null;
        }

        if (!hasGroundSupport(x, y, z)) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
            return null;
        }

        return getOrCreateGroundNode(x, y, z);
    }

    private PathNode getOrCreateGroundNode(int x, int y, int z) {
        var node = nodePool.getOrCreate(x, y, z, TerrainType.GROUND);
        node.setPendingTraversal(0.0f);
        node.setStableGround(x, y - 1, z, 1, 1);

        return node;
    }

    private boolean isFeetOpen(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        return !blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private boolean hasGroundSupport(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y - 1, z);

        return blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private BlockPos findStandablePosition(BlockPos pos) {
        if (!features.verticalTargetResolution()) {
            return pos;
        }

        if (isGroundStandable(pos.getX(), pos.getY(), pos.getZ())) {
            return pos;
        }

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var y = pos.getY() + stepUp;

            if (isGroundStandable(pos.getX(), y, pos.getZ())) {
                return new BlockPos(pos.getX(), y, pos.getZ());
            }
        }

        for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
            var y = pos.getY() - stepDown;

            if (isGroundStandable(pos.getX(), y, pos.getZ())) {
                return new BlockPos(pos.getX(), y, pos.getZ());
            }
        }

        return pos;
    }

    private boolean isGroundStandable(int x, int y, int z) {
        return isFeetOpen(x, y, z) && hasGroundSupport(x, y, z);
    }

    private void reject(PathRejectionReason reason, int x, int y, int z) {
        if (debugRecorder != null) {
            debugRecorder.reject(reason, x, y, z);
        }
    }
}
