package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
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

    private static final double DIAGONAL_SWEEP_SAMPLE_INTERVAL = 0.125;

    private static final double STEPPED_TRANSITION_SAMPLE_INTERVAL = 0.25;

    private static final double COLLISION_EPSILON = 1.0E-7;

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
        var resolvedPos = findStandablePosition(entityPos, config.getMaxFallDistance());

        return getOrCreateGroundNode(resolvedPos.getX(), resolvedPos.getY(), resolvedPos.getZ());
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var resolvedPos = findStandablePosition(targetPos, config.getMaxStepHeight());

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
            if (sameLevel != null && hasMovementClearance(from, sameLevel, dx, dz)) {
                return sameLevel;
            }
        }

        if (features.stepUp()) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateGroundNode(x, from.getY() + stepUp, z);

                if (steppedUp != null && hasMovementClearance(from, steppedUp, dx, dz)) {
                    return steppedUp;
                }
            }
        }

        if (features.stepDown()) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var steppedDown = tryCreateGroundNode(x, from.getY() - stepDown, z);

                if (steppedDown != null && hasMovementClearance(from, steppedDown, dx, dz)) {
                    return steppedDown;
                }
            }
        }

        return null;
    }

    private boolean hasMovementClearance(PathNode from, PathNode to, int dx, int dz) {
        if (features.diagonalCornerClearance() && dx != 0 && dz != 0) {
            if (hasFullBlockDiagonalCorner(from, to, dx, dz) || hasSameLevelSweptDiagonalCollision(from, to)) {
                reject(PathRejectionReason.DIAGONAL_CORNER_BLOCKED, to.getX(), to.getY(), to.getZ());
                return false;
            }
        }

        if (!hasSteppedFootprintTransitionClearance(from, to)) {
            reject(PathRejectionReason.NO_CLEARANCE, to.getX(), to.getY(), to.getZ());
            return false;
        }

        return true;
    }

    private boolean hasFullBlockDiagonalCorner(PathNode from, PathNode to, int dx, int dz) {
        var minY = Math.min(from.getY(), to.getY());
        var maxY = Math.max(from.getY(), to.getY()) + Math.max(1, config.getEntityHeight()) - 1;

        for (var y = minY; y <= maxY; y++) {
            if (
                isFullCollisionBlock(from.getX() + dx, y, from.getZ())
                    && isFullCollisionBlock(from.getX(), y, from.getZ() + dz)
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean isFullCollisionBlock(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        return blockAccessor.isCollisionShapeFullBlock(state, x, y, z);
    }

    private boolean hasSameLevelSweptDiagonalCollision(PathNode from, PathNode to) {
        return from.getY() == to.getY() && !isSweptDiagonalShapeClear(from, to);
    }

    private boolean hasSteppedFootprintTransitionClearance(PathNode from, PathNode to) {
        if (!usesSteppedFootprintSupport()) {
            return true;
        }

        var verticalDistance = Math.abs(to.getY() - from.getY());
        if (verticalDistance == 0 || verticalDistance > config.getMaxStepHeight()) {
            return true;
        }

        return isSteppedFootprintTransitionClear(from, to);
    }

    private boolean isSteppedFootprintTransitionClear(PathNode from, PathNode to) {
        var startX = nodeCenterX(from.getX());
        var startZ = nodeCenterZ(from.getZ());
        var endX = nodeCenterX(to.getX());
        var endZ = nodeCenterZ(to.getZ());
        var feetY = Math.max(from.getY(), to.getY());
        var dx = endX - startX;
        var dz = endZ - startZ;
        var distance = Math.sqrt(dx * dx + dz * dz);
        var sampleCount = Math.max(1, (int) Math.ceil(distance / STEPPED_TRANSITION_SAMPLE_INTERVAL));

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var centerX = startX + dx * progress;
            var centerZ = startZ + dz * progress;

            if (
                !isEntityBoxClear(centerX, feetY, centerZ, entityWidth(), entityHeight())
                    || !hasAnySteppedFootprintSupport(centerX, feetY, centerZ)
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean isSweptDiagonalShapeClear(PathNode from, PathNode to) {
        var startX = nodeCenterX(from.getX());
        var startY = from.getY();
        var startZ = nodeCenterZ(from.getZ());
        var endX = nodeCenterX(to.getX());
        var endY = to.getY();
        var endZ = nodeCenterZ(to.getZ());
        var dx = endX - startX;
        var dy = endY - startY;
        var dz = endZ - startZ;
        var distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        var sampleCount = Math.max(2, (int) Math.ceil(distance / DIAGONAL_SWEEP_SAMPLE_INTERVAL));
        var entityWidth = entityWidth();
        var entityHeight = entityHeight();

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var centerX = startX + dx * progress;
            var feetY = startY + dy * progress;
            var centerZ = startZ + dz * progress;

            if (!isEntityBoxClear(centerX, feetY, centerZ, entityWidth, entityHeight)) {
                return false;
            }
        }

        return true;
    }

    private boolean isEntityBoxClear(double centerX, double feetY, double centerZ, double entityWidth, double entityHeight) {
        var entityBox = entityBox(centerX, feetY, centerZ, entityWidth, entityHeight);

        var minX = (int) Math.floor(entityBox.minX);
        var minY = (int) Math.floor(entityBox.minY);
        var minZ = (int) Math.floor(entityBox.minZ);
        var maxX = (int) Math.floor(entityBox.maxX - COLLISION_EPSILON);
        var maxY = (int) Math.floor(entityBox.maxY - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(entityBox.maxZ - COLLISION_EPSILON);

        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                for (var z = minZ; z <= maxZ; z++) {
                    var state = blockAccessor.getBlockState(x, y, z);

                    if (isDoorPassable(state)) {
                        continue;
                    }

                    if (blockAccessor.isLiquid(state)) {
                        return false;
                    }

                    var shape = blockAccessor.getCollisionShape(state, x, y, z);

                    if (shape.isEmpty()) {
                        continue;
                    }

                    var blockPos = new BlockPos(x, y, z);

                    for (var blockBox : shape.toAabbs()) {
                        if (blockBox.move(blockPos).intersects(entityBox)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private AABB entityBox(double centerX, double feetY, double centerZ, double entityWidth, double entityHeight) {
        var halfWidth = entityWidth / 2.0d;

        return new AABB(
            centerX - halfWidth,
            feetY,
            centerZ - halfWidth,
            centerX + halfWidth,
            feetY + entityHeight,
            centerZ + halfWidth
        ).deflate(COLLISION_EPSILON, 0.0, COLLISION_EPSILON);
    }

    private double nodeCenterX(int x) {
        return x + nodeCenterOffset();
    }

    private double nodeCenterZ(int z) {
        return z + nodeCenterOffset();
    }

    private double nodeCenterOffset() {
        return entityWidth() / 2.0d;
    }

    private double entityWidth() {
        return Math.max(1.0d, config.getEntityWidth());
    }

    private double entityHeight() {
        return Math.max(1.0d, config.getEntityHeight());
    }

    private int footprintCellWidth() {
        return Math.max(1, config.getEntityWidth());
    }

    private boolean usesFootprintClearance() {
        return features.footprintClearance() && footprintCellWidth() > 1;
    }

    private boolean usesSteppedFootprintSupport() {
        return usesFootprintClearance()
            && features.steppedFootprintSupport()
            && config.getMaxStepHeight() > 0;
    }

    private @Nullable PathNode tryCreateGroundNode(int x, int y, int z) {
        if (!snapshotCosts.containsKey(TerrainType.GROUND)) {
            reject(PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
            return null;
        }

        if (!hasNodeClearance(x, y, z)) {
            reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            return null;
        }

        if (!hasNodeSupport(x, y, z)) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
            return null;
        }

        return getOrCreateGroundNode(x, y, z);
    }

    private PathNode getOrCreateGroundNode(int x, int y, int z) {
        var node = nodePool.getOrCreate(x, y, z, TerrainType.GROUND);
        var supportSize = usesFootprintClearance() ? footprintCellWidth() : 1;

        node.setPendingTraversal(0.0f);
        node.setStableGround(x, y - 1, z, supportSize, supportSize);

        return node;
    }

    private boolean hasNodeClearance(int x, int y, int z) {
        if (!usesFootprintClearance()) {
            return isFeetOpen(x, y, z);
        }

        return isEntityBoxClear(nodeCenterX(x), y, nodeCenterZ(z), entityWidth(), entityHeight());
    }

    private boolean isFeetOpen(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        if (isDoorPassable(state)) {
            return true;
        }

        return !blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private boolean isDoorPassable(BlockState state) {
        return state.getBlock() instanceof DoorBlock
            && (state.getValue(DoorBlock.OPEN) || canOpenDoors());
    }

    private boolean canOpenDoors() {
        return features.doorOpening() && config.canOpenDoors();
    }

    private boolean hasGroundSupport(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y - 1, z);

        return blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private boolean hasNodeSupport(int x, int y, int z) {
        if (!usesFootprintClearance()) {
            return hasGroundSupport(x, y, z);
        }

        if (usesSteppedFootprintSupport()) {
            return hasSteppedFootprintSupport(nodeCenterX(x), y, nodeCenterZ(z), true);
        }

        for (var supportX = x; supportX < x + footprintCellWidth(); supportX++) {
            for (var supportZ = z; supportZ < z + footprintCellWidth(); supportZ++) {
                if (!hasFootprintSupport(supportX, y, supportZ)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasFootprintSupport(int x, int feetY, int z) {
        return hasSupportAtTop(x, feetY, z);
    }

    private boolean hasSteppedFootprintSupport(double centerX, double feetY, double centerZ, boolean requireNominalSupport) {
        var halfWidth = entityWidth() / 2.0d;
        var minX = (int) Math.floor(centerX - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(centerZ - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(centerX + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(centerZ + halfWidth - COLLISION_EPSILON);
        var nominalSupportTopY = (int) Math.floor(feetY);
        var hasNominalSupport = false;

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                var supportTopY = findSupportTopY(x, nominalSupportTopY, z);

                if (supportTopY == Integer.MIN_VALUE) {
                    return false;
                }

                if (supportTopY == nominalSupportTopY) {
                    hasNominalSupport = true;
                }
            }
        }

        return !requireNominalSupport || hasNominalSupport;
    }

    private boolean hasAnySteppedFootprintSupport(double centerX, double feetY, double centerZ) {
        // A wide body moving between one-deep stair treads can transiently overlap more columns than an endpoint node.
        // Endpoint nodes still require full stepped support; the sweep only needs a supporting tread under part of it.
        var halfWidth = entityWidth() / 2.0d;
        var minX = (int) Math.floor(centerX - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(centerZ - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(centerX + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(centerZ + halfWidth - COLLISION_EPSILON);
        var nominalSupportTopY = (int) Math.floor(feetY);

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                if (findSupportTopY(x, nominalSupportTopY, z) != Integer.MIN_VALUE) {
                    return true;
                }
            }
        }

        return false;
    }

    private int findSupportTopY(int x, int feetY, int z) {
        for (var supportTopY = feetY; supportTopY >= feetY - config.getMaxStepHeight(); supportTopY--) {
            if (hasSupportAtTop(x, supportTopY, z)) {
                return supportTopY;
            }
        }

        return Integer.MIN_VALUE;
    }

    private boolean hasSupportAtTop(int x, int supportTopY, int z) {
        var supportY = supportTopY - 1;
        var state = blockAccessor.getBlockState(x, supportY, z);

        if (blockAccessor.isLiquid(state)) {
            return false;
        }

        var shape = blockAccessor.getCollisionShape(state, x, supportY, z);

        if (shape.isEmpty()) {
            return false;
        }

        var blockPos = new BlockPos(x, supportY, z);
        var supportProbe = new AABB(
            x + COLLISION_EPSILON,
            supportTopY - COLLISION_EPSILON,
            z + COLLISION_EPSILON,
            x + 1.0d - COLLISION_EPSILON,
            supportTopY + COLLISION_EPSILON,
            z + 1.0d - COLLISION_EPSILON
        );

        for (var blockBox : shape.toAabbs()) {
            if (blockBox.move(blockPos).intersects(supportProbe)) {
                return true;
            }
        }

        return false;
    }

    private BlockPos findStandablePosition(BlockPos pos, int maxStepDown) {
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

        for (int stepDown = 1; stepDown <= maxStepDown; stepDown++) {
            var y = pos.getY() - stepDown;

            if (isGroundStandable(pos.getX(), y, pos.getZ())) {
                return new BlockPos(pos.getX(), y, pos.getZ());
            }
        }

        return pos;
    }

    private boolean isGroundStandable(int x, int y, int z) {
        return hasNodeClearance(x, y, z) && hasNodeSupport(x, y, z);
    }

    private void reject(PathRejectionReason reason, int x, int y, int z) {
        if (debugRecorder != null) {
            debugRecorder.reject(reason, x, y, z);
        }
    }
}
