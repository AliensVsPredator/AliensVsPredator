package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Owns target anchoring, target projection, projection stabilization, and target-change checks for path navigation.
 */
final class PathNavigationTargetComponent implements PathNavigationAnchorResolver {

    private static final double MIN_TARGET_MOVE_DISTANCE_SQUARED = 9.0;

    private static final double TARGET_PROJECTION_REUSE_DISTANCE_SQUARED = 1.0;

    private static final int TARGET_PROJECTION_MIN_VERTICAL_SCAN = 32;

    private static final int TARGET_PROJECTION_MAX_VERTICAL_SCAN = 128;

    private final LevelReader level;

    private final PathNavigatorConfig config;

    private final PathNavigationStateComponent state;

    private final PathNavigationSpaceQuery spaceQuery;

    private final Supplier<PathfindingFeatures> activeFeaturesSupplier;

    private final Consumer<PathfindingFeature> featureUsageConsumer;

    private @Nullable BlockPos lastComputedTargetPos;

    private @Nullable BlockPos lastComputedRawTargetPos;

    private @Nullable BlockPos lastProjectionRawTargetPos;

    private @Nullable BlockPos lastProjectionTargetPos;

    PathNavigationTargetComponent(
        LevelReader level,
        PathNavigatorConfig config,
        PathNavigationStateComponent state,
        PathNavigationSpaceQuery spaceQuery,
        Supplier<PathfindingFeatures> activeFeaturesSupplier,
        Consumer<PathfindingFeature> featureUsageConsumer
    ) {
        this.level = level;
        this.config = config;
        this.state = state;
        this.spaceQuery = spaceQuery;
        this.activeFeaturesSupplier = activeFeaturesSupplier;
        this.featureUsageConsumer = featureUsageConsumer;
    }

    BlockPos resolveAndStoreTarget(BlockPos entityPos, BlockPos rawTarget) {
        return resolveSearchTarget(entityPos, rawTarget);
    }

    void updateTarget(BlockPos newRawTarget) {
        if (state.currentRequest() == null) {
            return;
        }

        var entityPos = state.hasLastEntityPosition
            ? entityAnchorPos(state.lastEntityX, state.lastEntityY, state.lastEntityZ)
            : null;

        var searchTarget = entityPos != null ? resolveSearchTarget(entityPos, newRawTarget) : newRawTarget;

        this.state.updateLifecycleTarget(newRawTarget, searchTarget);
    }

    void recordComputedTarget(BlockPos searchTarget, BlockPos rawTarget) {
        this.lastComputedTargetPos = searchTarget;
        this.lastComputedRawTargetPos = rawTarget;
    }

    void recordPrefixReuse(BlockPos target) {
        recordComputedTarget(target, activeRawTargetPos());
    }

    void clearActiveTarget() {
        lastComputedRawTargetPos = null;
        lastProjectionRawTargetPos = null;
        lastProjectionTargetPos = null;
    }

    boolean hasComputedTargetMovedForFailureCooldown(BlockPos target) {
        return lastComputedTargetPos != null
            && target.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED;
    }

    boolean hasTargetMovedForRecalculation() {
        var searchTarget = state.currentSearchTarget();

        if (searchTarget == null) {
            return false;
        }

        return lastComputedTargetPos == null
            || searchTarget.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED
            || hasRawTargetMovedForRecalculation();
    }

    BlockPos activeRawTargetPos() {
        var rawTarget = state.currentRawTarget();

        if (rawTarget == null) {
            throw new IllegalStateException("Path navigator has no active raw target");
        }

        return rawTarget;
    }

    public BlockPos entityAnchorPos(double entityX, double entityY, double entityZ) {
        var centerOffset = nodeCenterOffset();

        return BlockPos.containing(entityX - centerOffset + 0.5, entityY, entityZ - centerOffset + 0.5);
    }

    public BlockPos targetAnchorPos(double targetX, double targetY, double targetZ) {
        var centerOffset = nodeCenterOffset();

        return BlockPos.containing(targetX - centerOffset + 0.5, targetY, targetZ - centerOffset + 0.5);
    }

    private BlockPos resolveSearchTarget(BlockPos entityPos, BlockPos rawTarget) {
        if (!usesGroundedTargetProjection()) {
            return rawTarget;
        }

        if (!shouldProjectRawTarget(rawTarget)) {
            lastProjectionRawTargetPos = rawTarget;
            lastProjectionTargetPos = rawTarget;
            return rawTarget;
        }

        var reusedProjection = reuseStableProjection(rawTarget);

        if (reusedProjection != null) {
            markFeatureUsed(PathfindingFeature.GROUNDED_TARGET_PROJECTION);
            return reusedProjection;
        }

        var projectedTarget = findGroundedTargetProjection(entityPos, rawTarget);

        if (projectedTarget == null) {
            lastProjectionRawTargetPos = rawTarget;
            lastProjectionTargetPos = rawTarget;
            return rawTarget;
        }

        var stabilizedTarget = stabilizeProjectedTarget(rawTarget, projectedTarget);

        if (!stabilizedTarget.equals(rawTarget)) {
            markFeatureUsed(PathfindingFeature.GROUNDED_TARGET_PROJECTION);
        }

        return stabilizedTarget;
    }

    private @Nullable BlockPos reuseStableProjection(BlockPos rawTarget) {
        if (
            lastProjectionRawTargetPos == null
                || lastProjectionTargetPos == null
                || lastProjectionTargetPos.equals(lastProjectionRawTargetPos)
                || rawTarget.getY() != lastProjectionRawTargetPos.getY()
                || horizontalDistanceSquared(rawTarget, lastProjectionRawTargetPos)
                    >= TARGET_PROJECTION_REUSE_DISTANCE_SQUARED
                || !isProjectionTargetCandidate(lastProjectionTargetPos)
        ) {
            return null;
        }

        lastProjectionRawTargetPos = rawTarget;

        return lastProjectionTargetPos;
    }

    private boolean usesGroundedTargetProjection() {
        return activePathfindingFeatures().groundedTargetProjection() && !config.getEvaluatorConfig().canFly();
    }

    private @Nullable BlockPos findGroundedTargetProjection(BlockPos entityPos, BlockPos rawTarget) {
        var radius = targetProjectionHorizontalRadius();
        var minY = Math.max(
            level.getMinBuildHeight(),
            rawTarget.getY() - targetProjectionVerticalScan(entityPos, rawTarget)
        );

        for (var y = rawTarget.getY(); y >= minY; y--) {
            var candidate = findProjectionCandidateAtY(rawTarget.getX(), y, rawTarget.getZ(), radius);

            if (candidate != null) {
                return candidate;
            }
        }

        return null;
    }

    private @Nullable BlockPos findProjectionCandidateAtY(int centerX, int y, int centerZ, int radius) {
        for (var ring = 0; ring <= radius; ring++) {
            for (var dx = -ring; dx <= ring; dx++) {
                for (var dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
                        continue;
                    }

                    var candidate = new BlockPos(centerX + dx, y, centerZ + dz);

                    if (isProjectionTargetCandidate(candidate)) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }

    private BlockPos stabilizeProjectedTarget(BlockPos rawTarget, BlockPos projectedTarget) {
        if (projectedTarget.equals(rawTarget)) {
            lastProjectionRawTargetPos = rawTarget;
            lastProjectionTargetPos = projectedTarget;
            return projectedTarget;
        }

        if (
            lastProjectionRawTargetPos != null
                && lastProjectionTargetPos != null
                && rawTarget.getY() == lastProjectionRawTargetPos.getY()
                && lastProjectionTargetPos.getY() == projectedTarget.getY()
                && horizontalDistanceSquared(rawTarget, lastProjectionRawTargetPos)
                    < TARGET_PROJECTION_REUSE_DISTANCE_SQUARED
                && horizontalDistanceSquared(projectedTarget, lastProjectionTargetPos)
                    < TARGET_PROJECTION_REUSE_DISTANCE_SQUARED
                && isProjectionTargetCandidate(lastProjectionTargetPos)
        ) {
            lastProjectionRawTargetPos = rawTarget;
            return lastProjectionTargetPos;
        }

        lastProjectionRawTargetPos = rawTarget;
        lastProjectionTargetPos = projectedTarget;

        return projectedTarget;
    }

    private boolean isProjectionTargetCandidate(BlockPos candidate) {
        return isGroundProjectionTarget(candidate) || isWaterProjectionTarget(candidate);
    }

    private boolean shouldProjectRawTarget(BlockPos rawTarget) {
        return !isRawTargetGrounded(rawTarget) && !isRawTargetSwimmable(rawTarget);
    }

    private boolean isRawTargetGrounded(BlockPos rawTarget) {
        var center = anchorCenter(rawTarget);

        return spaceQuery.hasSupportAt((int) Math.floor(center.x), rawTarget.getY(), (int) Math.floor(center.z));
    }

    private boolean isRawTargetSwimmable(BlockPos rawTarget) {
        if (!activePathfindingFeatures().waterPathfinding() || !spaceQuery.isTerrainAllowed(TerrainType.WATER)) {
            return false;
        }

        var center = anchorCenter(rawTarget);
        var cursor = new BlockPos(
            (int) Math.floor(center.x),
            rawTarget.getY(),
            (int) Math.floor(center.z)
        );

        return level.getBlockState(cursor).getFluidState().is(FluidTags.WATER);
    }

    private boolean isGroundProjectionTarget(BlockPos candidate) {
        if (!spaceQuery.isTerrainAllowed(TerrainType.GROUND)) {
            return false;
        }

        var center = anchorCenter(candidate);
        var entityWidth = projectionEntityWidth();

        if (!spaceQuery.hasEntitySupport(center, entityWidth)) {
            return false;
        }

        if (spaceQuery.isEntityBoxClear(center, entityWidth, projectionEntityHeight(), false)) {
            return true;
        }

        return usesCrawling()
            && spaceQuery.isEntityBoxClear(center, entityWidth, projectionCrawlHeight(), false);
    }

    private boolean isWaterProjectionTarget(BlockPos candidate) {
        if (
            !activePathfindingFeatures().waterPathfinding()
                || !spaceQuery.isTerrainAllowed(TerrainType.WATER)
                || !hasWaterFootprint(candidate)
        ) {
            return false;
        }

        return spaceQuery.isEntityBoxClear(
            anchorCenter(candidate),
            projectionEntityWidth(),
            projectionEntityHeight(),
            true
        );
    }

    private boolean hasWaterFootprint(BlockPos candidate) {
        var footprintWidth = projectionFootprintCellWidth();
        var cursor = new BlockPos.MutableBlockPos();

        for (var x = candidate.getX(); x < candidate.getX() + footprintWidth; x++) {
            for (var z = candidate.getZ(); z < candidate.getZ() + footprintWidth; z++) {
                cursor.set(x, candidate.getY(), z);

                if (!level.getBlockState(cursor).getFluidState().is(FluidTags.WATER)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int targetProjectionHorizontalRadius() {
        return Math.min(3, Math.max(1, config.getEvaluatorConfig().getEntityWidth() + 1));
    }

    private int targetProjectionVerticalScan(BlockPos entityPos, BlockPos rawTarget) {
        var configuredScan = Math.max(
            TARGET_PROJECTION_MIN_VERTICAL_SCAN,
            config.getEvaluatorConfig().getMaxFallDistance()
        );
        var entityDeltaScan = Math.abs(rawTarget.getY() - entityPos.getY())
            + config.getEvaluatorConfig().getMaxFallDistance();

        return Math.min(TARGET_PROJECTION_MAX_VERTICAL_SCAN, Math.max(configuredScan, entityDeltaScan));
    }

    private int projectionFootprintCellWidth() {
        if (!activePathfindingFeatures().footprintClearance()) {
            return 1;
        }

        return Math.max(1, config.getEvaluatorConfig().getEntityWidth());
    }

    private float projectionEntityWidth() {
        var configuredWidth = Math.max(1.0f, (float) config.getEvaluatorConfig().getEntityWidth());

        return state.hasLastEntityPosition ? Math.max(configuredWidth, state.lastEntityWidth) : configuredWidth;
    }

    private float projectionEntityHeight() {
        return Math.max(1.0f, (float) config.getEvaluatorConfig().getEntityHeight());
    }

    private float projectionCrawlHeight() {
        return Math.max(1.0f, (float) config.getEvaluatorConfig().getCrawlConfig().crawlHeight());
    }

    private boolean usesCrawling() {
        return activePathfindingFeatures().crawlThroughGaps() && config.getEvaluatorConfig().getCrawlConfig().enabled();
    }

    private Vec3 anchorCenter(BlockPos anchorPos) {
        var centerOffset = nodeCenterOffset();

        return new Vec3(anchorPos.getX() + centerOffset, anchorPos.getY(), anchorPos.getZ() + centerOffset);
    }

    private double horizontalDistanceSquared(BlockPos left, BlockPos right) {
        var dx = left.getX() - right.getX();
        var dz = left.getZ() - right.getZ();

        return dx * dx + dz * dz;
    }

    private boolean hasRawTargetMovedForRecalculation() {
        var rawTarget = state.currentRawTarget();

        if (rawTarget == null || lastComputedRawTargetPos == null) {
            return false;
        }

        var threshold = usesGroundedTargetProjection() && shouldProjectRawTarget(rawTarget)
            ? TARGET_PROJECTION_REUSE_DISTANCE_SQUARED
            : MIN_TARGET_MOVE_DISTANCE_SQUARED;

        return rawTarget.distSqr(lastComputedRawTargetPos) >= threshold;
    }

    private PathfindingFeatures activePathfindingFeatures() {
        return activeFeaturesSupplier.get();
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureUsageConsumer.accept(feature);
    }

    private double nodeCenterOffset() {
        return Math.max(1, config.getEvaluatorConfig().getEntityWidth()) / 2.0;
    }
}
