package com.blib.api.common.pathfinding.v1.evaluator;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

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
 * Ground and water terrain evaluator. Ground nodes require open feet space and support below. Water nodes require a
 * water volume and collision clearance, but do not require ground support.
 */
public final class UnifiedTerrainEvaluator implements TerrainEvaluator {

    private static final double STEPPED_TRANSITION_SAMPLE_INTERVAL = 0.25;

    private static final double DROP_ENTRY_APPROACH_SAMPLE_INTERVAL = 0.25;

    private static final double COLLISION_EPSILON = 1.0E-7;

    private static final byte CACHE_FALSE = 1;

    private static final byte CACHE_TRUE = 2;

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

    private final Long2ByteOpenHashMap feetOpenCache;

    private final Long2ByteOpenHashMap waterBlockCache;

    private final Long2ByteOpenHashMap groundSupportCache;

    private final Long2ByteOpenHashMap supportTopCache;

    private final Long2ByteOpenHashMap fullCollisionBlockCache;

    private final Long2ByteOpenHashMap footprintNodeSupportCache;

    private final Long2ByteOpenHashMap waterFootprintCache;

    private final Long2ByteOpenHashMap dropSupportCache;

    private final Long2IntOpenHashMap supportTopSearchCache;

    private final Map<EntityBoxClearanceKey, Boolean> entityBoxClearanceCache;

    private final Map<FootprintScanKey, Boolean> steppedFootprintSupportCache;

    private final Map<FootprintScanKey, Boolean> anySteppedFootprintSupportCache;

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
        this.feetOpenCache = new Long2ByteOpenHashMap();
        this.waterBlockCache = new Long2ByteOpenHashMap();
        this.groundSupportCache = new Long2ByteOpenHashMap();
        this.supportTopCache = new Long2ByteOpenHashMap();
        this.fullCollisionBlockCache = new Long2ByteOpenHashMap();
        this.footprintNodeSupportCache = new Long2ByteOpenHashMap();
        this.waterFootprintCache = new Long2ByteOpenHashMap();
        this.dropSupportCache = new Long2ByteOpenHashMap();
        this.supportTopSearchCache = new Long2IntOpenHashMap();
        this.entityBoxClearanceCache = new HashMap<>();
        this.steppedFootprintSupportCache = new HashMap<>();
        this.anySteppedFootprintSupportCache = new HashMap<>();
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
        clearSearchCaches();

        for (var terrainType : config.getSupportedTerrains()) {
            snapshotCosts.put(terrainType, config.getCost(terrainType));
        }
    }

    private void clearSearchCaches() {
        feetOpenCache.clear();
        waterBlockCache.clear();
        groundSupportCache.clear();
        supportTopCache.clear();
        fullCollisionBlockCache.clear();
        footprintNodeSupportCache.clear();
        waterFootprintCache.clear();
        dropSupportCache.clear();
        supportTopSearchCache.clear();
        entityBoxClearanceCache.clear();
        steppedFootprintSupportCache.clear();
        anySteppedFootprintSupportCache.clear();
    }

    @Override
    public PathNode getStartNode(BlockPos entityPos) {
        return getOrCreateResolvedNode(entityPos, config.getMaxFallDistance());
    }

    @Override
    public PathNode getGoalNode(BlockPos targetPos) {
        return getOrCreateResolvedNode(targetPos, 0);
    }

    @Override
    public PathNode getGoalNode(BlockPos startPos, BlockPos targetPos) {
        var maxStepDown = targetPos.getY() < startPos.getY() ? config.getMaxFallDistance() : 0;

        return getOrCreateResolvedNode(targetPos, maxStepDown);
    }

    @Override
    public int getNeighbors(PathNode node, PathNode[] neighbors) {
        if (node.getTerrainType() == TerrainType.GROUND && snapshotCosts.containsKey(TerrainType.GROUND)) {
            return getGroundNeighbors(node, neighbors);
        }

        if (node.getTerrainType() == TerrainType.WATER && usesWaterPathfinding()) {
            return getWaterNeighbors(node, neighbors);
        }

        return 0;
    }

    private int getGroundNeighbors(PathNode node, PathNode[] neighbors) {
        var count = appendGroundNeighbors(node, neighbors, 0, CARDINAL_OFFSETS);

        if (features.diagonalMovement()) {
            count = appendGroundNeighbors(node, neighbors, count, DIAGONAL_OFFSETS);
        }

        if (features.dropDownOpenings()) {
            count = appendDropOpeningNeighbors(node, neighbors, count);
        }

        return count;
    }

    private int getWaterNeighbors(PathNode node, PathNode[] neighbors) {
        var count = appendWaterNeighbors(node, neighbors, 0, CARDINAL_OFFSETS);

        if (features.diagonalMovement()) {
            count = appendWaterNeighbors(node, neighbors, count, DIAGONAL_OFFSETS);
        }

        count = appendWaterVerticalNeighbors(node, neighbors, count);

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

    private boolean usesWaterPathfinding() {
        return features.waterPathfinding() && snapshotCosts.containsKey(TerrainType.WATER);
    }

    private boolean usesSearchCaching() {
        return features.searchCaching();
    }

    private boolean usesTerrainPrecheck() {
        return features.terrainPrecheck();
    }

    private boolean usesFootprintScanCaching() {
        return features.footprintScanCache();
    }

    private int appendWaterNeighbors(PathNode node, PathNode[] neighbors, int count, int[][] offsets) {
        for (var offset : offsets) {
            var groundExit = findWaterExitNeighbor(node, offset[0], offset[1]);

            if (groundExit != null) {
                neighbors[count++] = groundExit;
            }

            var waterTravel = findWaterTravelNeighbor(node, offset[0], offset[1]);

            if (waterTravel != null) {
                neighbors[count++] = waterTravel;
            }
        }

        return count;
    }

    private int appendWaterVerticalNeighbors(PathNode node, PathNode[] neighbors, int count) {
        var swimUp = tryWaterMovementNeighbor(node, node.getX(), node.getY() + 1, node.getZ(), 0, 0);

        if (swimUp != null) {
            neighbors[count++] = swimUp;
        }

        var swimDown = tryWaterMovementNeighbor(node, node.getX(), node.getY() - 1, node.getZ(), 0, 0);

        if (swimDown != null) {
            neighbors[count++] = swimDown;
        }

        return count;
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

    private @Nullable PathNode findWaterTravelNeighbor(PathNode from, int dx, int dz) {
        var x = from.getX() + dx;
        var z = from.getZ() + dz;
        var sameLevel = tryWaterMovementNeighbor(from, x, from.getY(), z, dx, dz);

        if (sameLevel != null) {
            return sameLevel;
        }

        var swimUp = tryWaterMovementNeighbor(from, x, from.getY() + 1, z, dx, dz);

        if (swimUp != null) {
            return swimUp;
        }

        return tryWaterMovementNeighbor(from, x, from.getY() - 1, z, dx, dz);
    }

    private @Nullable PathNode findWaterExitNeighbor(PathNode from, int dx, int dz) {
        if (dx == 0 && dz == 0) {
            return null;
        }

        var x = from.getX() + dx;
        var z = from.getZ() + dz;
        var sameLevel = tryGroundExitNeighbor(from, x, from.getY(), z, dx, dz);

        if (sameLevel != null) {
            return sameLevel;
        }

        for (var stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryGroundExitNeighbor(from, x, from.getY() + stepUp, z, dx, dz);

            if (steppedUp != null) {
                return steppedUp;
            }
        }

        for (var stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
            var steppedDown = tryGroundExitNeighbor(from, x, from.getY() - stepDown, z, dx, dz);

            if (steppedDown != null) {
                return steppedDown;
            }
        }

        return null;
    }

    private @Nullable PathNode tryGroundExitNeighbor(PathNode from, int x, int y, int z, int dx, int dz) {
        var ground = tryCreateGroundNode(x, y, z);

        if (ground == null || !hasMovementClearance(from, ground, dx, dz)) {
            return null;
        }

        return markWaterMovementFeatureUsed(ground, dx, dz);
    }

    private @Nullable PathNode tryWaterMovementNeighbor(PathNode from, int x, int y, int z, int dx, int dz) {
        var water = tryCreateWaterNode(x, y, z);

        if (water == null || !hasMovementClearance(from, water, dx, dz)) {
            return null;
        }

        return markWaterMovementFeatureUsed(water, dx, dz);
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

        return findWaterEntryNeighbor(from, dx, dz);
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

    private @Nullable PathNode findWaterEntryNeighbor(PathNode from, int dx, int dz) {
        if (!usesWaterPathfinding()) {
            return null;
        }

        var x = from.getX() + dx;
        var z = from.getZ() + dz;
        var sameLevel = tryWaterMovementNeighbor(from, x, from.getY(), z, dx, dz);

        if (sameLevel != null) {
            return sameLevel;
        }

        for (var stepDown = 1; stepDown <= config.getMaxFallDistance(); stepDown++) {
            var steppedDown = tryWaterMovementNeighbor(from, x, from.getY() - stepDown, z, dx, dz);

            if (steppedDown != null) {
                return steppedDown;
            }
        }

        for (var stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var steppedUp = tryWaterMovementNeighbor(from, x, from.getY() + stepUp, z, dx, dz);

            if (steppedUp != null) {
                return steppedUp;
            }
        }

        return null;
    }

    private PathNode markWaterMovementFeatureUsed(PathNode node, int dx, int dz) {
        markFeatureUsed(PathfindingFeature.WATER_PATHFINDING);

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

        if (usesFootprintScanCaching()) {
            return cachedFootprintBoolean(
                dropSupportCache,
                x,
                supportTopY,
                z,
                () -> hasDropSupportAtUncached(x, supportTopY, z, supportSize)
            );
        }

        return hasDropSupportAtUncached(x, supportTopY, z, supportSize);
    }

    private boolean hasDropSupportAtUncached(int x, int supportTopY, int z, int supportSize) {
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

    private boolean movementAllowsLiquids(PathNode from, PathNode to) {
        return from.getTerrainType() == TerrainType.WATER || to.getTerrainType() == TerrainType.WATER;
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
        if (usesSearchCaching()) {
            return cachedBlockBoolean(fullCollisionBlockCache, x, y, z, () -> isFullCollisionBlockUncached(x, y, z));
        }

        return isFullCollisionBlockUncached(x, y, z);
    }

    private boolean isFullCollisionBlockUncached(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        return blockAccessor.isCollisionShapeFullBlock(state, x, y, z);
    }

    private boolean hasSteppedFootprintTransitionClearance(PathNode from, PathNode to) {
        if (to.getTerrainType() == TerrainType.WATER) {
            return true;
        }

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
        var allowLiquids = movementAllowsLiquids(from, to);

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var centerX = startX + dx * progress;
            var centerZ = startZ + dz * progress;

            if (
                !isEntityBoxClear(centerX, feetY, centerZ, entityWidth, entityHeight, allowLiquids)
                    || !hasAnySteppedFootprintSupport(centerX, feetY, centerZ)
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSameLevelSweptDiagonalCollision(PathNode from, PathNode to, int dx, int dz) {
        return dx != 0
            && dz != 0
            && from.getY() == to.getY()
            && hasSweptDiagonalCollision(from, to, movementAllowsLiquids(from, to));
    }

    private boolean hasSweptDiagonalCollision(PathNode from, PathNode to, boolean allowLiquids) {
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

                    if (blockAccessor.isLiquid(state) && (!allowLiquids || !blockAccessor.isWater(state))) {
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

    private record EntityBoxClearanceKey(
        long centerX,
        long feetY,
        long centerZ,
        long entityWidth,
        long entityHeight,
        boolean allowLiquids
    ) {}

    private record FootprintScanKey(
        long centerX,
        long feetY,
        long centerZ,
        boolean requireNominalSupport
    ) {}

    private boolean isEntityBoxClear(double centerX, double feetY, double centerZ, double entityWidth, double entityHeight) {
        return isEntityBoxClear(centerX, feetY, centerZ, entityWidth, entityHeight, false);
    }

    private boolean isEntityBoxClear(
        double centerX,
        double feetY,
        double centerZ,
        double entityWidth,
        double entityHeight,
        boolean allowLiquids
    ) {
        if (usesSearchCaching()) {
            markFeatureUsed(PathfindingFeature.SEARCH_CACHING);

            var key = new EntityBoxClearanceKey(
                Double.doubleToLongBits(centerX),
                Double.doubleToLongBits(feetY),
                Double.doubleToLongBits(centerZ),
                Double.doubleToLongBits(entityWidth),
                Double.doubleToLongBits(entityHeight),
                allowLiquids
            );
            var cached = entityBoxClearanceCache.get(key);

            if (cached != null) {
                return cached;
            }

            var clear = isEntityBoxClearUncached(centerX, feetY, centerZ, entityWidth, entityHeight, allowLiquids);
            entityBoxClearanceCache.put(key, clear);

            return clear;
        }

        return isEntityBoxClearUncached(centerX, feetY, centerZ, entityWidth, entityHeight, allowLiquids);
    }

    private boolean isEntityBoxClearUncached(
        double centerX,
        double feetY,
        double centerZ,
        double entityWidth,
        double entityHeight,
        boolean allowLiquids
    ) {
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

                    if (blockAccessor.isLiquid(state) && (!allowLiquids || !blockAccessor.isWater(state))) {
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

        var supportPrechecked = false;
        if (usesTerrainPrecheck()) {
            var rejectionReason = precheckGroundNodeTerrain(x, y, z);

            if (rejectionReason != null) {
                reject(rejectionReason, x, y, z);
                return null;
            }

            supportPrechecked = true;
        }

        if (!hasNodeClearance(x, y, z, posture)) {
            reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            return null;
        }

        if (!supportPrechecked && !hasNodeSupport(x, y, z)) {
            reject(PathRejectionReason.UNSTABLE_SUPPORT, x, y, z);
            return null;
        }

        return getOrCreateGroundNode(x, y, z, posture);
    }

    private @Nullable PathNode tryCreateWaterNode(int x, int y, int z) {
        if (!usesWaterPathfinding()) {
            reject(PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
            return null;
        }

        if (usesTerrainPrecheck()) {
            markFeatureUsed(PathfindingFeature.TERRAIN_PRECHECK);
        }

        if (!hasWaterFootprint(x, y, z)) {
            reject(PathRejectionReason.UNSUPPORTED_TERRAIN, x, y, z);
            return null;
        }

        if (!hasWaterNodeClearance(x, y, z)) {
            reject(PathRejectionReason.NO_CLEARANCE, x, y, z);
            return null;
        }

        markFeatureUsed(PathfindingFeature.WATER_PATHFINDING);

        return getOrCreateWaterNode(x, y, z);
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

    private PathNode getOrCreateWaterNode(int x, int y, int z) {
        return nodePool.getOrCreate(x, y, z, TerrainType.WATER, PathPosture.STANDING);
    }

    private PathNode getOrCreateResolvedNode(BlockPos pos, int maxStepDown) {
        var node = getPreferredNode(pos.getX(), pos.getY(), pos.getZ());

        if (node != null) {
            return node;
        }

        if (!features.verticalTargetResolution()) {
            return getOrCreateGroundNode(pos.getX(), pos.getY(), pos.getZ());
        }

        for (int stepUp = 1; stepUp <= config.getMaxStepHeight(); stepUp++) {
            var y = pos.getY() + stepUp;

            node = getPreferredNode(pos.getX(), y, pos.getZ());

            if (node != null) {
                markFeatureUsed(PathfindingFeature.VERTICAL_TARGET_RESOLUTION);
                return node;
            }
        }

        for (int stepDown = 1; stepDown <= maxStepDown; stepDown++) {
            var y = pos.getY() - stepDown;

            node = getPreferredNode(pos.getX(), y, pos.getZ());

            if (node != null) {
                markFeatureUsed(PathfindingFeature.VERTICAL_TARGET_RESOLUTION);
                return node;
            }
        }

        return getOrCreateGroundNode(pos.getX(), pos.getY(), pos.getZ());
    }

    private @Nullable PathNode getPreferredNode(int x, int y, int z) {
        var ground = tryCreateGroundNode(x, y, z);

        if (ground != null) {
            return ground;
        }

        if (!usesWaterPathfinding()) {
            return null;
        }

        return tryCreateWaterNode(x, y, z);
    }

    private @Nullable PathRejectionReason precheckGroundNodeTerrain(int x, int y, int z) {
        markFeatureUsed(PathfindingFeature.TERRAIN_PRECHECK);

        if (!isFeetOpen(x, y, z)) {
            return PathRejectionReason.NO_CLEARANCE;
        }

        if (!hasNodeSupport(x, y, z)) {
            return PathRejectionReason.UNSTABLE_SUPPORT;
        }

        return null;
    }

    private boolean hasNodeClearance(int x, int y, int z, PathPosture posture) {
        if (!usesEntityHitboxClearance(posture)) {
            return isFeetOpen(x, y, z);
        }

        markEntityBoxClearanceUsed(posture);

        return isEntityBoxClear(nodeCenterX(x), y, nodeCenterZ(z), entityWidth(), entityHeight(posture));
    }

    private boolean hasWaterNodeClearance(int x, int y, int z) {
        if (usesEntityHitboxClearance(PathPosture.STANDING)) {
            markEntityBoxClearanceUsed(PathPosture.STANDING);
        }

        return isEntityBoxClear(nodeCenterX(x), y, nodeCenterZ(z), entityWidth(), entityHeight(), true);
    }

    private boolean hasWaterFootprint(int x, int y, int z) {
        var footprintWidth = usesFootprintClearance() ? footprintCellWidth() : 1;

        if (usesFootprintClearance()) {
            markFeatureUsed(PathfindingFeature.FOOTPRINT_CLEARANCE);
        }

        if (usesFootprintScanCaching()) {
            return cachedFootprintBoolean(
                waterFootprintCache,
                x,
                y,
                z,
                () -> hasWaterFootprintUncached(x, y, z, footprintWidth)
            );
        }

        return hasWaterFootprintUncached(x, y, z, footprintWidth);
    }

    private boolean hasWaterFootprintUncached(int x, int y, int z, int footprintWidth) {
        for (var waterX = x; waterX < x + footprintWidth; waterX++) {
            for (var waterZ = z; waterZ < z + footprintWidth; waterZ++) {
                if (!isWaterAt(waterX, y, waterZ)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isFeetOpen(int x, int y, int z) {
        if (usesSearchCaching()) {
            return cachedBlockBoolean(feetOpenCache, x, y, z, () -> isFeetOpenUncached(x, y, z));
        }

        return isFeetOpenUncached(x, y, z);
    }

    private boolean isFeetOpenUncached(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        if (isDoorPassable(state)) {
            return true;
        }

        return !blockAccessor.isSolid(state) && !blockAccessor.isLiquid(state);
    }

    private boolean isWaterAt(int x, int y, int z) {
        if (usesSearchCaching()) {
            return cachedBlockBoolean(waterBlockCache, x, y, z, () -> isWaterAtUncached(x, y, z));
        }

        return isWaterAtUncached(x, y, z);
    }

    private boolean isWaterAtUncached(int x, int y, int z) {
        var state = blockAccessor.getBlockState(x, y, z);

        return blockAccessor.isWater(state);
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
        if (usesSearchCaching()) {
            return cachedBlockBoolean(groundSupportCache, x, y, z, () -> hasGroundSupportUncached(x, y, z));
        }

        return hasGroundSupportUncached(x, y, z);
    }

    private boolean hasGroundSupportUncached(int x, int y, int z) {
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
        }

        if (usesFootprintScanCaching()) {
            return cachedFootprintBoolean(
                footprintNodeSupportCache,
                x,
                y,
                z,
                () -> hasFootprintNodeSupportUncached(x, y, z)
            );
        }

        return hasFootprintNodeSupportUncached(x, y, z);
    }

    private boolean hasFootprintNodeSupportUncached(int x, int y, int z) {
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
        if (usesFootprintScanCaching()) {
            return cachedFootprintScan(
                steppedFootprintSupportCache,
                centerX,
                feetY,
                centerZ,
                requireNominalSupport,
                () -> hasSteppedFootprintSupportUncached(centerX, feetY, centerZ, requireNominalSupport)
            );
        }

        return hasSteppedFootprintSupportUncached(centerX, feetY, centerZ, requireNominalSupport);
    }

    private boolean hasSteppedFootprintSupportUncached(
        double centerX,
        double feetY,
        double centerZ,
        boolean requireNominalSupport
    ) {
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
        if (usesFootprintScanCaching()) {
            return cachedFootprintScan(
                anySteppedFootprintSupportCache,
                centerX,
                feetY,
                centerZ,
                false,
                () -> hasAnySteppedFootprintSupportUncached(centerX, feetY, centerZ)
            );
        }

        return hasAnySteppedFootprintSupportUncached(centerX, feetY, centerZ);
    }

    private boolean hasAnySteppedFootprintSupportUncached(double centerX, double feetY, double centerZ) {
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
        if (usesFootprintScanCaching()) {
            markFeatureUsed(PathfindingFeature.FOOTPRINT_SCAN_CACHE);

            var key = packBlockKey(x, feetY, z);

            if (supportTopSearchCache.containsKey(key)) {
                return supportTopSearchCache.get(key);
            }

            var supportTopY = findSupportTopYUncached(x, feetY, z);
            supportTopSearchCache.put(key, supportTopY);

            return supportTopY;
        }

        return findSupportTopYUncached(x, feetY, z);
    }

    private int findSupportTopYUncached(int x, int feetY, int z) {
        for (var supportTopY = feetY; supportTopY >= feetY - config.getMaxStepHeight(); supportTopY--) {
            if (hasSupportAtTop(x, supportTopY, z)) {
                return supportTopY;
            }
        }

        return Integer.MIN_VALUE;
    }

    private boolean hasSupportAtTop(int x, int supportTopY, int z) {
        if (usesSearchCaching()) {
            return cachedBlockBoolean(supportTopCache, x, supportTopY, z, () -> hasSupportAtTopUncached(x, supportTopY, z));
        }

        return hasSupportAtTopUncached(x, supportTopY, z);
    }

    private boolean hasSupportAtTopUncached(int x, int supportTopY, int z) {
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

    private boolean cachedBlockBoolean(Long2ByteOpenHashMap cache, int x, int y, int z, BooleanSupplier loader) {
        markFeatureUsed(PathfindingFeature.SEARCH_CACHING);

        var key = packBlockKey(x, y, z);
        var cached = cache.get(key);

        if (cached == CACHE_TRUE) {
            return true;
        }

        if (cached == CACHE_FALSE) {
            return false;
        }

        var value = loader.getAsBoolean();
        cache.put(key, value ? CACHE_TRUE : CACHE_FALSE);

        return value;
    }

    private boolean cachedFootprintBoolean(Long2ByteOpenHashMap cache, int x, int y, int z, BooleanSupplier loader) {
        markFeatureUsed(PathfindingFeature.FOOTPRINT_SCAN_CACHE);

        var key = packBlockKey(x, y, z);
        var cached = cache.get(key);

        if (cached == CACHE_TRUE) {
            return true;
        }

        if (cached == CACHE_FALSE) {
            return false;
        }

        var value = loader.getAsBoolean();
        cache.put(key, value ? CACHE_TRUE : CACHE_FALSE);

        return value;
    }

    private boolean cachedFootprintScan(
        Map<FootprintScanKey, Boolean> cache,
        double centerX,
        double feetY,
        double centerZ,
        boolean requireNominalSupport,
        BooleanSupplier loader
    ) {
        markFeatureUsed(PathfindingFeature.FOOTPRINT_SCAN_CACHE);

        var key = new FootprintScanKey(
            Double.doubleToLongBits(centerX),
            Double.doubleToLongBits(feetY),
            Double.doubleToLongBits(centerZ),
            requireNominalSupport
        );
        var cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        var value = loader.getAsBoolean();
        cache.put(key, value);

        return value;
    }

    private static long packBlockKey(int x, int y, int z) {
        return BlockPos.asLong(x, y, z);
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
