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
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathNodePool;
import com.blib.api.common.pathfinding.v1.node.PathPosture;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Minimal ground-only terrain evaluator. A ground node is valid when the feet cell is open and the block below it is
 * solid. Neighbor generation scans adjacent columns for same-level, step-up, and step-down/fall positions.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final double STEPPED_TRANSITION_SAMPLE_INTERVAL = 0.25;

    private static final double DROP_ENTRY_APPROACH_SAMPLE_INTERVAL = 0.25;

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

        return getOrCreateResolvedGroundNode(resolvedPos);
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        var resolvedPos = findStandablePosition(targetPos, 0);

        return getOrCreateResolvedGroundNode(resolvedPos);
    }

    @Override
    public PathNode getGoalNode(BlockPos startPos, BlockPos targetPos) {
        var maxStepDown = targetPos.getY() < startPos.getY() ? config.getMaxFallDistance() : 0;
        var resolvedPos = findStandablePosition(targetPos, maxStepDown);

        return getOrCreateResolvedGroundNode(resolvedPos);
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

        if (features.dropDownOpenings()) {
            count = appendDropOpeningNeighbors(node, neighbors, count);
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

    private int appendDropOpeningNeighbors(PathNode node, PathNode[] neighbors, int count) {
        for (var offset : CARDINAL_OFFSETS) {
            var neighbor = findDropOpeningNeighbor(node, offset[0], offset[1]);

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
                return markMovementFeatureUsed(sameLevel, dx, dz, PathfindingFeature.SAME_LEVEL_MOVEMENT);
            }
        }

        if (features.stepUp()) {
            for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
                var steppedUp = tryCreateGroundNode(x, from.getY() + stepUp, z);

                if (steppedUp != null && hasMovementClearance(from, steppedUp, dx, dz)) {
                    return markMovementFeatureUsed(steppedUp, dx, dz, PathfindingFeature.STEP_UP);
                }
            }
        }

        if (features.stepDown()) {
            for (int stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
                var steppedDown = tryCreateGroundNode(x, from.getY() - stepDown, z);

                if (steppedDown == null) {
                    continue;
                }

                if (features.dropDownOpenings() && requiresDropOpening(from, steppedDown)) {
                    continue;
                }

                if (hasMovementClearance(from, steppedDown, dx, dz)) {
                    return markMovementFeatureUsed(steppedDown, dx, dz, PathfindingFeature.STEP_DOWN);
                }
            }
        }

        return null;
    }

    private @Nullable PathNode findDropOpeningNeighbor(PathNode from, int dx, int dz) {
        var horizontalDistance = dropOpeningHorizontalDistance();
        var x = from.getX() + dx * horizontalDistance;
        var z = from.getZ() + dz * horizontalDistance;
        var firstDropDistance = Math.max(1, config.getMaxStepHeight() + 1);

        for (var dropDistance = firstDropDistance; dropDistance <= config.getMaxFallDistance(); dropDistance++) {
            var landing = tryCreateGroundNode(x, from.getY() - dropDistance, z);

            if (landing == null) {
                continue;
            }

            if (!hasDropDownTransitionClearance(from, landing, dx, dz)) {
                continue;
            }

            landing.setPendingDropEntryWaypoint(nodeCenterX(landing.getX()), from.getY(), nodeCenterZ(landing.getZ()));
            markFeatureUsed(PathfindingFeature.DROP_DOWN_OPENINGS);

            return landing;
        }

        return null;
    }

    private PathNode markMovementFeatureUsed(PathNode node, int dx, int dz, PathfindingFeature feature) {
        markFeatureUsed(feature);

        if (dx != 0 && dz != 0) {
            markFeatureUsed(PathfindingFeature.DIAGONAL_MOVEMENT);
        }

        return node;
    }

    private boolean requiresDropOpening(PathNode from, PathNode to) {
        return from.getY() - to.getY() > config.getMaxStepHeight();
    }

    private boolean hasDropDownTransitionClearance(PathNode from, PathNode to, int dx, int dz) {
        if (!isDropEntryApproachClear(from, to)) {
            reject(PathRejectionReason.DROP_OPENING_BLOCKED, to.getX(), to.getY(), to.getZ());
            return false;
        }

        if (!isDropOpeningClear(to.getX(), from.getY(), to.getZ(), to.getY(), to.getPosture())) {
            reject(PathRejectionReason.DROP_OPENING_BLOCKED, to.getX(), to.getY(), to.getZ());
            return false;
        }

        return hasMovementClearance(from, to, dx, dz);
    }

    private boolean isDropEntryApproachClear(PathNode from, PathNode to) {
        var startX = nodeCenterX(from.getX());
        var startZ = nodeCenterZ(from.getZ());
        var endX = nodeCenterX(to.getX());
        var endZ = nodeCenterZ(to.getZ());
        var dx = endX - startX;
        var dz = endZ - startZ;
        var distance = Math.sqrt(dx * dx + dz * dz);
        var sampleCount = Math.max(1, (int) Math.ceil(distance / DROP_ENTRY_APPROACH_SAMPLE_INTERVAL));
        var entityWidth = entityWidth();
        var entityHeight = movementHeight(from, to);

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var centerX = startX + dx * progress;
            var centerZ = startZ + dz * progress;

            if (!isEntityBoxClear(centerX, from.getY(), centerZ, entityWidth, entityHeight)) {
                return false;
            }
        }

        return true;
    }

    private boolean isDropOpeningClear(int x, int topY, int z, int landingY, PathPosture posture) {
        for (var y = topY; y > landingY; y--) {
            if (!hasDropVolumeClearance(x, y, z, posture) || hasDropSupportAt(x, y, z)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasDropVolumeClearance(int x, int y, int z, PathPosture posture) {
        if (!usesEntityHitboxClearance(posture)) {
            return isFeetOpen(x, y, z);
        }

        markEntityBoxClearanceUsed(posture);

        return isEntityBoxClear(nodeCenterX(x), y, nodeCenterZ(z), entityWidth(), entityHeight(posture));
    }

    private boolean hasDropSupportAt(int x, int supportTopY, int z) {
        var supportSize = usesFootprintClearance() ? footprintCellWidth() : 1;

        for (var supportX = x; supportX < x + supportSize; supportX++) {
            for (var supportZ = z; supportZ < z + supportSize; supportZ++) {
                if (hasSupportAtTop(supportX, supportTopY, supportZ)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasMovementClearance(PathNode from, PathNode to, int dx, int dz) {
        if (features.diagonalCornerClearance() && hasDiagonalMovementComponent(from, to, dx, dz)) {
            markFeatureUsed(PathfindingFeature.DIAGONAL_CORNER_CLEARANCE);

            if (hasFullBlockDiagonalCorner(from, to, dx, dz) || hasSameLevelSweptDiagonalCollision(from, to, dx, dz)) {
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

    private boolean hasDiagonalMovementComponent(PathNode from, PathNode to, int dx, int dz) {
        var changedAxes = 0;

        if (dx != 0) {
            changedAxes++;
        }

        if (from.getY() != to.getY()) {
            changedAxes++;
        }

        if (dz != 0) {
            changedAxes++;
        }

        return changedAxes >= 2;
    }

    private boolean hasFullBlockDiagonalCorner(PathNode from, PathNode to, int dx, int dz) {
        if (dx != 0 && dz != 0 && hasHorizontalFullBlockDiagonalCorner(from, to, dx, dz)) {
            return true;
        }

        return hasTopHorizontalAxisFullBlockCorner(from, to, dx, dz);
    }

    private boolean hasHorizontalFullBlockDiagonalCorner(PathNode from, PathNode to, int dx, int dz) {
        var entityHeight = movementHeight(from, to);
        var minFeetY = Math.min(from.getY(), to.getY());
        var maxHeadY = Math.max(from.getY(), to.getY()) + entityHeight;
        var bodyMinY = (int) Math.floor(minFeetY);
        var bodyMaxY = (int) Math.floor(maxHeadY - COLLISION_EPSILON);
        var cornerMaxY = (int) Math.floor(maxHeadY + COLLISION_EPSILON);
        var verticalCornerSpan = Math.max(1, (int) Math.ceil(entityHeight));
        var sideAX = from.getX() + dx;
        var sideAZ = from.getZ();
        var sideBX = from.getX();
        var sideBZ = from.getZ() + dz;

        for (var sideAY = bodyMinY; sideAY <= cornerMaxY; sideAY++) {
            if (!isFullCollisionBlock(sideAX, sideAY, sideAZ)) {
                continue;
            }

            var minSideBY = Math.max(bodyMinY, sideAY - verticalCornerSpan);
            var maxSideBY = Math.min(cornerMaxY, sideAY + verticalCornerSpan);

            for (var sideBY = minSideBY; sideBY <= maxSideBY; sideBY++) {
                if (
                    isVerticalCardinalCorner(bodyMinY, bodyMaxY, sideAY, sideBY)
                        && isFullCollisionBlock(sideBX, sideBY, sideBZ)
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasTopHorizontalAxisFullBlockCorner(PathNode from, PathNode to, int dx, int dz) {
        if (dx == 0 && dz == 0) {
            return false;
        }

        var entityHeight = movementHeight(from, to);
        var minFeetY = Math.min(from.getY(), to.getY());
        var maxHeadY = Math.max(from.getY(), to.getY()) + entityHeight;
        var bodyMinY = (int) Math.floor(minFeetY);
        var bodyMaxY = (int) Math.floor(maxHeadY - COLLISION_EPSILON);
        var minTopY = (int) Math.floor(Math.min(from.getY(), to.getY()) + entityHeight + COLLISION_EPSILON);
        var maxTopY = (int) Math.floor(maxHeadY + COLLISION_EPSILON);

        if (!hasFullBlockAtTopFace(from, to, minTopY, maxTopY)) {
            return false;
        }

        for (var sideY = bodyMinY; sideY <= bodyMaxY; sideY++) {
            if (
                (dx != 0 && hasFullBlockAtHorizontalXFace(from, to, dx, sideY))
                    || (dz != 0 && hasFullBlockAtHorizontalZFace(from, to, dz, sideY))
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean hasFullBlockAtTopFace(PathNode from, PathNode to, int minY, int maxY) {
        var footprintWidth = footprintCellWidth();
        var minX = Math.min(from.getX(), to.getX());
        var minZ = Math.min(from.getZ(), to.getZ());
        var maxX = Math.max(from.getX(), to.getX()) + footprintWidth - 1;
        var maxZ = Math.max(from.getZ(), to.getZ()) + footprintWidth - 1;

        for (var y = minY; y <= maxY; y++) {
            for (var x = minX; x <= maxX; x++) {
                for (var z = minZ; z <= maxZ; z++) {
                    if (isFullCollisionBlock(x, y, z)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasFullBlockAtHorizontalXFace(PathNode from, PathNode to, int dx, int y) {
        var footprintWidth = footprintCellWidth();
        var sideX = dx > 0 ? from.getX() + footprintWidth : from.getX() - 1;
        var minZ = Math.min(from.getZ(), to.getZ());
        var maxZ = Math.max(from.getZ(), to.getZ()) + footprintWidth - 1;

        for (var z = minZ; z <= maxZ; z++) {
            if (isFullCollisionBlock(sideX, y, z)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasFullBlockAtHorizontalZFace(PathNode from, PathNode to, int dz, int y) {
        var footprintWidth = footprintCellWidth();
        var sideZ = dz > 0 ? from.getZ() + footprintWidth : from.getZ() - 1;
        var minX = Math.min(from.getX(), to.getX());
        var maxX = Math.max(from.getX(), to.getX()) + footprintWidth - 1;

        for (var x = minX; x <= maxX; x++) {
            if (isFullCollisionBlock(x, y, sideZ)) {
                return true;
            }
        }

        return false;
    }

    private boolean isVerticalCardinalCorner(int bodyMinY, int bodyMaxY, int firstY, int secondY) {
        return isBodyOverlappingY(bodyMinY, bodyMaxY, firstY) || isBodyOverlappingY(bodyMinY, bodyMaxY, secondY);
    }

    private boolean isBodyOverlappingY(int bodyMinY, int bodyMaxY, int y) {
        return y >= bodyMinY && y <= bodyMaxY;
    }

    private boolean isFullCollisionBlock(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        return blockAccessor.isCollisionShapeFullBlock(state, x, y, z);
    }

    private boolean hasSteppedFootprintTransitionClearance(PathNode from, PathNode to) {
        if (!usesSteppedFootprintSupport()) {
            return true;
        }

        var verticalDistance = Math.abs(to.getY() - from.getY());
        if (verticalDistance == 0 || verticalDistance > config.getMaxStepHeight()) {
            return true;
        }

        markFeatureUsed(PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT);

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
        var entityWidth = entityWidth();
        var entityHeight = movementHeight(from, to);

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var centerX = startX + dx * progress;
            var centerZ = startZ + dz * progress;

            if (
                !isEntityBoxClear(centerX, feetY, centerZ, entityWidth, entityHeight)
                    || !hasAnySteppedFootprintSupport(centerX, feetY, centerZ)
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSameLevelSweptDiagonalCollision(PathNode from, PathNode to, int dx, int dz) {
        return dx != 0 && dz != 0 && from.getY() == to.getY() && hasSweptDiagonalCollision(from, to);
    }

    private boolean hasSweptDiagonalCollision(PathNode from, PathNode to) {
        var startX = nodeCenterX(from.getX());
        var startY = from.getY();
        var startZ = nodeCenterZ(from.getZ());
        var endX = nodeCenterX(to.getX());
        var endY = to.getY();
        var endZ = nodeCenterZ(to.getZ());
        var dx = endX - startX;
        var dy = endY - startY;
        var dz = endZ - startZ;
        var entityWidth = entityWidth();
        var entityHeight = movementHeight(from, to);
        var startBox = entityBox(startX, startY, startZ, entityWidth, entityHeight);
        var endBox = entityBox(endX, endY, endZ, entityWidth, entityHeight);
        var sweepBounds = startBox.minmax(endBox);

        var minX = (int) Math.floor(sweepBounds.minX);
        var minY = (int) Math.floor(sweepBounds.minY);
        var minZ = (int) Math.floor(sweepBounds.minZ);
        var maxX = (int) Math.floor(sweepBounds.maxX - COLLISION_EPSILON);
        var maxY = (int) Math.floor(sweepBounds.maxY - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(sweepBounds.maxZ - COLLISION_EPSILON);

        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                for (var z = minZ; z <= maxZ; z++) {
                    var state = blockAccessor.getBlockState(x, y, z);

                    if (isDoorPassable(state)) {
                        continue;
                    }

                    if (blockAccessor.isLiquid(state)) {
                        return true;
                    }

                    var shape = blockAccessor.getCollisionShape(state, x, y, z);

                    if (shape.isEmpty()) {
                        continue;
                    }

                    var blockPos = new BlockPos(x, y, z);

                    for (var blockBox : shape.toAabbs()) {
                        if (sweptAabbIntersects(startBox, dx, dy, dz, blockBox.move(blockPos))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean sweptAabbIntersects(AABB movingBox, double dx, double dy, double dz, AABB staticBox) {
        if (movingBox.intersects(staticBox)) {
            return true;
        }

        var xSweep = sweepAxis(movingBox.minX, movingBox.maxX, staticBox.minX, staticBox.maxX, dx);
        if (xSweep == null) {
            return false;
        }

        var ySweep = sweepAxis(movingBox.minY, movingBox.maxY, staticBox.minY, staticBox.maxY, dy);
        if (ySweep == null) {
            return false;
        }

        var zSweep = sweepAxis(movingBox.minZ, movingBox.maxZ, staticBox.minZ, staticBox.maxZ, dz);
        if (zSweep == null) {
            return false;
        }

        var entryTime = Math.max(xSweep.entryTime(), Math.max(ySweep.entryTime(), zSweep.entryTime()));
        var exitTime = Math.min(xSweep.exitTime(), Math.min(ySweep.exitTime(), zSweep.exitTime()));

        return entryTime <= exitTime
            && exitTime >= -COLLISION_EPSILON
            && entryTime <= 1.0d + COLLISION_EPSILON;
    }

    private @Nullable AxisSweep sweepAxis(
        double movingMin,
        double movingMax,
        double staticMin,
        double staticMax,
        double delta
    ) {
        if (Math.abs(delta) <= COLLISION_EPSILON) {
            if (movingMax <= staticMin + COLLISION_EPSILON || movingMin >= staticMax - COLLISION_EPSILON) {
                return null;
            }

            return AxisSweep.ALWAYS_OVERLAPPING;
        }

        if (delta > 0.0d) {
            return new AxisSweep(
                (staticMin - movingMax) / delta,
                (staticMax - movingMin) / delta
            );
        }

        return new AxisSweep(
            (staticMax - movingMin) / delta,
            (staticMin - movingMax) / delta
        );
    }

    private record AxisSweep(double entryTime, double exitTime) {

        private static final AxisSweep ALWAYS_OVERLAPPING = new AxisSweep(
            Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY
        );
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

    private double entityHeight(PathPosture posture) {
        if (posture.isCrawling()) {
            return Math.max(1.0d, config.getCrawlConfig().crawlHeight());
        }

        return entityHeight();
    }

    private double movementHeight(PathNode from, PathNode to) {
        if (from.requiresCrawling() || to.requiresCrawling()) {
            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
            return entityHeight(PathPosture.CRAWLING);
        }

        return entityHeight();
    }

    private int footprintCellWidth() {
        return Math.max(1, config.getEntityWidth());
    }

    private int dropOpeningHorizontalDistance() {
        return footprintCellWidth();
    }

    private boolean usesFootprintClearance() {
        return features.footprintClearance() && footprintCellWidth() > 1;
    }

    private boolean usesEntityHitboxClearance(PathPosture posture) {
        return posture.isCrawling()
            || usesCrawling()
            || features.entityHitboxClearance()
            || usesFootprintClearance();
    }

    private boolean usesCrawling() {
        return features.crawlThroughGaps() && config.getCrawlConfig().enabled();
    }

    private void markEntityBoxClearanceUsed(PathPosture posture) {
        if (features.entityHitboxClearance()) {
            markFeatureUsed(PathfindingFeature.ENTITY_HITBOX_CLEARANCE);
        }

        if (usesFootprintClearance()) {
            markFeatureUsed(PathfindingFeature.FOOTPRINT_CLEARANCE);
        }

        if (posture.isCrawling()) {
            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
        }
    }

    private boolean usesSteppedFootprintSupport() {
        return usesFootprintClearance()
            && features.steppedFootprintSupport()
            && config.getMaxStepHeight() > 0;
    }

    private @Nullable PathNode tryCreateGroundNode(int x, int y, int z) {
        var standing = tryCreateGroundNode(x, y, z, PathPosture.STANDING);

        if (standing != null) {
            return standing;
        }

        if (!usesCrawling()) {
            return null;
        }

        return tryCreateGroundNode(x, y, z, PathPosture.CRAWLING);
    }

    private @Nullable PathNode tryCreateGroundNode(int x, int y, int z, PathPosture posture) {
        if (!snapshotCosts.containsKey(TerrainType.GROUND)) {
            reject(PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
            return null;
        }

        if (!hasNodeClearance(x, y, z, posture)) {
            reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            return null;
        }

        if (!hasNodeSupport(x, y, z)) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
            return null;
        }

        return getOrCreateGroundNode(x, y, z, posture);
    }

    private PathNode getOrCreateGroundNode(int x, int y, int z) {
        return getOrCreateGroundNode(x, y, z, PathPosture.STANDING);
    }

    private PathNode getOrCreateGroundNode(int x, int y, int z, PathPosture posture) {
        var node = nodePool.getOrCreate(x, y, z, TerrainType.GROUND, posture);
        var supportSize = usesFootprintClearance() ? footprintCellWidth() : 1;

        node.setPendingTraversal(posture.isCrawling() ? config.getCrawlConfig().crawlCostMalus() : 0.0f);
        node.setStableGround(x, y - 1, z, supportSize, supportSize);

        if (posture.isCrawling()) {
            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
        }

        return node;
    }

    private PathNode getOrCreateResolvedGroundNode(BlockPos pos) {
        var node = tryCreateGroundNode(pos.getX(), pos.getY(), pos.getZ());

        if (node != null) {
            return node;
        }

        return getOrCreateGroundNode(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean hasNodeClearance(int x, int y, int z, PathPosture posture) {
        if (!usesEntityHitboxClearance(posture)) {
            return isFeetOpen(x, y, z);
        }

        markEntityBoxClearanceUsed(posture);

        return isEntityBoxClear(nodeCenterX(x), y, nodeCenterZ(z), entityWidth(), entityHeight(posture));
    }

    private boolean isFeetOpen(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        if (isDoorPassable(state)) {
            return true;
        }

        return !blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private boolean isDoorPassable(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) {
            return false;
        }

        if (state.getValue(DoorBlock.OPEN)) {
            return true;
        }

        if (canOpenDoors()) {
            markFeatureUsed(PathfindingFeature.DOOR_OPENING);
            return true;
        }

        return false;
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

        markFeatureUsed(PathfindingFeature.FOOTPRINT_CLEARANCE);

        if (usesSteppedFootprintSupport()) {
            markFeatureUsed(PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT);
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
                markFeatureUsed(PathfindingFeature.VERTICAL_TARGET_RESOLUTION);
                return new BlockPos(pos.getX(), y, pos.getZ());
            }
        }

        for (int stepDown = 1; stepDown <= maxStepDown; stepDown++) {
            var y = pos.getY() - stepDown;

            if (isGroundStandable(pos.getX(), y, pos.getZ())) {
                markFeatureUsed(PathfindingFeature.VERTICAL_TARGET_RESOLUTION);
                return new BlockPos(pos.getX(), y, pos.getZ());
            }
        }

        return pos;
    }

    private boolean isGroundStandable(int x, int y, int z) {
        return tryCreateGroundNode(x, y, z) != null;
    }

    private void reject(PathRejectionReason reason, int x, int y, int z) {
        if (debugRecorder != null) {
            debugRecorder.reject(reason, x, y, z);
        }
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        if (debugRecorder != null) {
            debugRecorder.markFeatureUsed(feature);
        }
    }
}
