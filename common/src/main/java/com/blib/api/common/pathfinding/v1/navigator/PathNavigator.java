package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathPosture;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.search.PathfindingTuning;
import com.blib.api.common.pathfinding.v1.search.SearchConfig;
import com.blib.api.common.pathfinding.v1.search.SegmentedPathPlanner;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;

/**
 * Standalone path navigator. Manages path planning, following, stuck detection, and terrain transition callbacks. Does
 * not extend any Minecraft class.
 * <p>
 * The consuming code calls {@link #tick(double, double, double, float, float)} each tick with the entity's exact
 * position and bounding box dimensions. The navigator advances along the path and provides the next waypoint via
 * {@link #getCurrentTargetCenter()}. The calling code is responsible for actually moving the entity toward the
 * waypoint.
 * </p>
 */
public final class PathNavigator {

    private final PathNavigatorConfig config;

    private final BLibPathFinder pathFinder;

    private final @Nullable SegmentedPathPlanner planner;

    private final LevelReader level;

    private @Nullable BLibPath currentPath;

    private @Nullable BlockPos targetPos;

    private @Nullable BlockPos lastComputedTargetPos;

    private @Nullable TerrainType currentTerrain;

    private int lastPathComputeTick;

    private int lastProgressTick;

    private long lastPathComputeNanos;

    private double lastDistanceToCurrentNode;

    private double lastDistanceToNextNode;

    private int lastObservedNodeIndex = -1;

    private int dropEntryNodeIndex = -1;

    private boolean dropEntryReached;

    private int tickCount;

    private @Nullable CompletableFuture<@Nullable BLibPath> pendingPath;

    private long asyncStartNanos;

    // --- Failed path backoff ---
    private int consecutiveFailures;

    private int failureCooldownTicks;

    private long lastFailureTick;

    private final long createdNanos = System.nanoTime();

    private static final int BASE_FAILURE_COOLDOWN = 10;

    private static final int MAX_FAILURE_COOLDOWN = 200;

    private static final long NANOS_PER_TICK = 50_000_000L;

    private static final double WIDE_FOOTPRINT_REACH_XZ = 0.45;

    private static final double WAYPOINT_REACH_Y = 0.45;

    private static final double DROP_ENTRY_MIN_REACH_XZ = 0.75;

    private static final double SHAPE_WAYPOINT_SAMPLE_STEP = 0.05;

    private static final int ANY_ANGLE_SMOOTHING_MAX_LOOKAHEAD_NODES = 16;

    private static final double ANY_ANGLE_SMOOTHING_SAMPLE_INTERVAL = 0.25;

    private static final double CRAWL_POSTURE_MIN_PREP_DISTANCE = 1.5;

    private static final double COLLISION_EPSILON = 1.0E-7;

    private @Nullable Set<TerrainType> excludedTerrains;

    private SearchConfig searchConfig;

    private PathfindingTuning pathfindingTuning;

    private int stuckTimeoutInTicks;

    private int pathRecalculateIntervalInTicks;

    private boolean needsRepath;

    private @Nullable PathEdgeKey lastStuckReplannedEdge;

    private PathfindingFeatures features;

    private @Nullable PathfindingProfile profile;

    private int featuresRevision;

    private int pendingFeaturesRevision;

    private int pathfindingFeatureUsageMask;

    private double lastEntityX;

    private double lastEntityY;

    private double lastEntityZ;

    private boolean hasLastEntityPosition;

    private float lastEntityWidth = 1.0f;

    private float lastEntityHeight = 2.0f;

    private static final double PROGRESS_DISTANCE_EPSILON_SQUARED = 0.25;

    public PathNavigator(LevelReader level, PathNavigatorConfig config) {
        this(level, config, null);
    }

    public PathNavigator(LevelReader level, PathNavigatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.level = level;
        this.config = config;
        this.searchConfig = config.getSearchConfig();
        this.pathfindingTuning = config.getPathfindingTuning();
        this.stuckTimeoutInTicks = config.getStuckTimeoutInTicks();
        this.pathRecalculateIntervalInTicks = config.getPathRecalculateIntervalInTicks();
        this.pathFinder = new BLibPathFinder(
            new UnifiedTerrainEvaluator(config.getEvaluatorConfig(), classificationCache),
            searchConfig,
            classificationCache
        );
        this.pathFinder.setTuning(pathfindingTuning);
        this.planner = classificationCache != null
            ? new SegmentedPathPlanner(pathFinder)
            : null;
        this.features = config.getDefaultFeatures();
        this.profile = PathfindingProfile.matching(features).orElse(null);
        this.pathFinder.setFeatures(features);
    }

    /**
     * Plans a path to the target position and begins following it.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(BlockPos entityPos, BlockPos target) {
        return navigateTo(entityPos, target, false);
    }

    private boolean navigateTo(BlockPos entityPos, BlockPos target, boolean stuckReplan) {
        if (!stuckReplan && isInFailureCooldown(target)) {
            return false;
        }

        this.targetPos = target;
        this.lastComputedTargetPos = target;

        if (!stuckReplan) {
            this.lastStuckReplannedEdge = null;
        }

        preparePathFinder();

        var startNanos = System.nanoTime();

        if (shouldUsePlanner()) {
            markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
            this.currentPath = planner.findPath(level, entityPos, target);
        } else {
            this.currentPath = pathFinder.findPath(level, entityPos, target);
        }
        markFeatureUsage(pathFinder.consumeFeatureUsageMask());

        this.lastPathComputeNanos = System.nanoTime() - startNanos;

        this.lastPathComputeTick = tickCount;
        resetProgressTracking();

        if (isUsablePath(currentPath)) {
            var startNode = currentPath.getCurrentNode();

            this.currentTerrain = startNode.getTerrainType();
            resetFailureCooldown();
        } else {
            this.currentPath = null;
            this.currentTerrain = null;
            recordFailure();
        }

        return currentPath != null;
    }

    /**
     * Plans a path from the entity's current center position. Wide entities are converted to the nearest footprint
     * anchor before search.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(double entityX, double entityY, double entityZ, BlockPos target) {
        return navigateTo(entityAnchorPos(entityX, entityY, entityZ), target);
    }

    /**
     * Plans a path from the entity's current center position to an exact target center position. Both positions are
     * converted to footprint anchors with the same entity-width semantics.
     *
     * @return true if a path was found
     */
    public boolean navigateTo(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ
    ) {
        return navigateTo(
            entityAnchorPos(entityX, entityY, entityZ),
            targetAnchorPos(targetX, targetY, targetZ)
        );
    }

    /**
     * Asynchronously plans a path to the target position. Chunk data is snapshotted and the terrain cache is
     * pre-populated on the calling thread, then the A* search runs on a background thread. Call
     * {@link #isPathPending()} to check if an async computation is in progress. The path is automatically applied on
     * the next {@link #tick} call after the computation completes.
     */
    public void navigateToAsync(BlockPos entityPos, BlockPos target) {
        if (!features.asyncPathfinding()) {
            navigateTo(entityPos, target);
            return;
        }

        if (isInFailureCooldown(target)) {
            return;
        }

        this.targetPos = target;
        this.lastComputedTargetPos = target;
        this.lastStuckReplannedEdge = null;
        preparePathFinder();
        markFeatureUsed(PathfindingFeature.ASYNC_PATHFINDING);

        this.asyncStartNanos = System.nanoTime();
        this.pendingFeaturesRevision = featuresRevision;
        this.pendingPath = pathFinder.findPathAsync(level, entityPos, target);
    }

    /**
     * Asynchronously plans a path from the entity's current center position. Wide entities are converted to the nearest
     * footprint anchor before search.
     */
    public void navigateToAsync(double entityX, double entityY, double entityZ, BlockPos target) {
        navigateToAsync(entityAnchorPos(entityX, entityY, entityZ), target);
    }

    /**
     * Asynchronously plans a path from the entity's current center position to an exact target center position.
     */
    public void navigateToAsync(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ
    ) {
        navigateToAsync(
            entityAnchorPos(entityX, entityY, entityZ),
            targetAnchorPos(targetX, targetY, targetZ)
        );
    }

    /**
     * Returns true if an asynchronous path computation is in progress.
     */
    public boolean isPathPending() {
        return pendingPath != null;
    }

    private void checkPendingPath() {
        if (pendingPath == null || !pendingPath.isDone()) {
            return;
        }

        var path = pendingPath.join();
        pendingPath = null;

        var searchFeatureUsage = pathFinder.consumeFeatureUsageMask();

        if (pendingFeaturesRevision != featuresRevision) {
            if (targetPos != null) {
                needsRepath = true;
            }
            return;
        }

        markFeatureUsage(searchFeatureUsage);

        this.currentPath = path;
        this.lastPathComputeNanos = System.nanoTime() - asyncStartNanos;
        this.lastPathComputeTick = tickCount;
        resetProgressTracking();

        if (isUsablePath(currentPath)) {
            var startNode = currentPath.getCurrentNode();

            this.currentTerrain = startNode.getTerrainType();
            resetFailureCooldown();
        } else {
            this.currentPath = null;
            this.currentTerrain = null;
            recordFailure();
        }
    }

    /**
     * Advances the navigator one tick. Call this every tick with the entity's exact position. The navigator checks
     * waypoint proximity using per-axis distance and entity dimensions, advances the path, fires transition handlers,
     * and detects stuck conditions.
     *
     * @param entityX      exact X position of the entity
     * @param entityY      exact Y position of the entity (feet)
     * @param entityZ      exact Z position of the entity
     * @param entityWidth  bounding box width of the entity
     * @param entityHeight bounding box height of the entity
     */
    public void tick(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        tickCount++;
        lastEntityX = entityX;
        lastEntityY = entityY;
        lastEntityZ = entityZ;
        hasLastEntityPosition = true;
        lastEntityWidth = entityWidth;
        lastEntityHeight = entityHeight;
        checkPendingPath();

        if (needsRepath) {
            needsRepath = false;

            if (targetPos != null) {
                navigateTo(entityAnchorPos(entityX, entityY, entityZ), targetPos);
            }
        }

        // Advance to next segment if current path is done but route hasn't reached the final target.
        if (currentPath != null && currentPath.isDone() && shouldUsePlanner() && planner.hasActiveRoute()) {
            if (currentPath.isReached()) {
                planner.clear();
            } else {
                advanceToNextSegment(entityX, entityY, entityZ);
            }
        }

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        var entityAnchorPos = entityAnchorPos(entityX, entityY, entityZ);

        advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);

        // After waypoint advancement, check again for segment transition.
        if (currentPath != null && currentPath.isDone() && shouldUsePlanner() && planner.hasActiveRoute()) {
            if (currentPath.isReached()) {
                planner.clear();
            } else {
                advanceToNextSegment(entityX, entityY, entityZ);
            }
        }

        if (currentPath == null || currentPath.isDone()) {
            return;
        }

        detectStuck(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
        checkRecalculate(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
    }

    /**
     * Stops navigation and clears the current path.
     */
    public void stop() {
        if (pendingPath != null) {
            pendingPath.cancel(false);
            pendingPath = null;
        }

        this.currentPath = null;
        this.targetPos = null;
        this.currentTerrain = null;
        this.needsRepath = false;
        this.lastStuckReplannedEdge = null;
        resetProgressTracking();

        if (planner != null) {
            planner.clear();
        }
    }

    public PathNavigatorConfig getConfig() {
        return config;
    }

    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    public PathfindingTuning getPathfindingTuning() {
        return pathfindingTuning;
    }

    public int getStuckTimeoutInTicks() {
        return stuckTimeoutInTicks;
    }

    public int getPathRecalculateIntervalInTicks() {
        return pathRecalculateIntervalInTicks;
    }

    public void setSearchConfig(SearchConfig searchConfig) {
        setPathfindingRuntimeConfig(searchConfig, pathfindingTuning, stuckTimeoutInTicks, pathRecalculateIntervalInTicks);
    }

    public void setPathfindingTuning(PathfindingTuning pathfindingTuning) {
        setPathfindingRuntimeConfig(searchConfig, pathfindingTuning, stuckTimeoutInTicks, pathRecalculateIntervalInTicks);
    }

    public void setStuckTimeoutInTicks(int stuckTimeoutInTicks) {
        setPathfindingRuntimeConfig(searchConfig, pathfindingTuning, stuckTimeoutInTicks, pathRecalculateIntervalInTicks);
    }

    public void setPathRecalculateIntervalInTicks(int pathRecalculateIntervalInTicks) {
        setPathfindingRuntimeConfig(searchConfig, pathfindingTuning, stuckTimeoutInTicks, pathRecalculateIntervalInTicks);
    }

    public void setPathfindingRuntimeConfig(
        SearchConfig searchConfig,
        PathfindingTuning pathfindingTuning,
        int stuckTimeoutInTicks,
        int pathRecalculateIntervalInTicks
    ) {
        Objects.requireNonNull(searchConfig, "searchConfig");
        Objects.requireNonNull(pathfindingTuning, "pathfindingTuning");

        var nextStuckTimeout = Math.max(1, stuckTimeoutInTicks);
        var nextPathRecalculateInterval = Math.max(1, pathRecalculateIntervalInTicks);

        if (
            this.searchConfig.equals(searchConfig)
                && this.pathfindingTuning.equals(pathfindingTuning)
                && this.stuckTimeoutInTicks == nextStuckTimeout
                && this.pathRecalculateIntervalInTicks == nextPathRecalculateInterval
        ) {
            return;
        }

        this.searchConfig = searchConfig;
        this.pathfindingTuning = pathfindingTuning;
        this.stuckTimeoutInTicks = nextStuckTimeout;
        this.pathRecalculateIntervalInTicks = nextPathRecalculateInterval;
        pathFinder.setSearchConfig(searchConfig);
        pathFinder.setTuning(pathfindingTuning);
        invalidateActivePathForReplan();
    }

    /**
     * Returns the navigator's internal tick counter, incremented once per {@link #tick} call.
     */
    public int getTickCount() {
        return tickCount;
    }

    /**
     * Returns the tick at which the current path was computed.
     */
    public int getLastPathComputeTick() {
        return lastPathComputeTick;
    }

    /**
     * Returns the tick at which the navigator last advanced to a new node (i.e. made measurable progress).
     */
    public int getLastProgressTick() {
        return lastProgressTick;
    }

    /**
     * Returns the wall-clock time in nanoseconds that the most recent path computation took.
     */
    public long getLastPathComputeNanos() {
        return lastPathComputeNanos;
    }

    /**
     * Returns the snapshot from the most recent A* search, or null if no search has been performed.
     */
    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return pathFinder.getLastSearchSnapshot();
    }

    /**
     * Returns the current path node, or null if not navigating.
     */
    public @Nullable PathNode getCurrentNode() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return currentPath.getCurrentNode();
    }

    /**
     * Returns the footprint-anchor position of the current path node, or null if not navigating. Movement code should
     * use {@link #getCurrentTargetCenter()} instead.
     */
    public @Nullable BlockPos getCurrentTargetPos() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        var node = currentPath.getCurrentNode();

        return new BlockPos(node.getX(), node.getY(), node.getZ());
    }

    /**
     * Returns the world-space center the entity should move toward for the current path node, or null if not navigating.
     */
    public @Nullable Vec3 getCurrentTargetCenter() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return currentNodeTargetCenter(currentPath.getCurrentNode(), lastEntityWidth, lastEntityHeight);
    }

    public boolean isNavigating() {
        return currentPath != null && !currentPath.isDone();
    }

    public boolean isDone() {
        return currentPath == null || currentPath.isDone();
    }

    public @Nullable BLibPath getCurrentPath() {
        return currentPath;
    }

    public @Nullable TerrainType getCurrentTerrain() {
        return currentTerrain;
    }

    public boolean canOpenDoors() {
        return features.doorOpening() && config.getEvaluatorConfig().canOpenDoors();
    }

    /**
     * Returns the posture required by the active waypoint. A null or completed path requires standing by default.
     */
    public PathPosture getCurrentRequiredPosture() {
        if (currentPath == null || currentPath.isDone() || !usesCrawling()) {
            return PathPosture.STANDING;
        }

        var posture = currentPath.getCurrentNode().getPosture();

        if (posture.isCrawling()) {
            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
        }

        return posture;
    }

    /**
     * Returns the posture required by the active waypoint. Use
     * {@link #getDesiredPosture(double, double, double)} when movement code wants near-entry crawl anticipation.
     */
    public PathPosture getDesiredPosture() {
        if (currentPath == null || currentPath.isDone() || !usesCrawling()) {
            return PathPosture.STANDING;
        }

        if (currentPath.getCurrentNode().requiresCrawling()) {
            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
            return PathPosture.CRAWLING;
        }

        return PathPosture.STANDING;
    }

    /**
     * Returns the posture movement code should prefer right now, including a short distance-gated lookahead so the
     * entity lowers its hitbox only when it is physically close to a crawl-only waypoint.
     */
    public PathPosture getDesiredPosture(double entityX, double entityY, double entityZ) {
        var currentPosture = getDesiredPosture();

        if (currentPosture.isCrawling() || currentPath == null || currentPath.isDone() || !usesCrawling()) {
            return currentPosture;
        }

        var currentIndex = currentPath.getCurrentNodeIndex();
        var lookahead = config.getEvaluatorConfig().getCrawlConfig().postureLookaheadNodes();
        var endIndex = Math.min(currentPath.getNodeCount() - 1, currentIndex + lookahead);

        for (var index = currentIndex + 1; index <= endIndex; index++) {
            if (currentPath.getNode(index).requiresCrawling()) {
                if (isNearCrawlPostureEntry(entityX, entityY, entityZ, currentPath.getNode(index))) {
                    markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
                    return PathPosture.CRAWLING;
                }

                return PathPosture.STANDING;
            }
        }

        return PathPosture.STANDING;
    }

    public boolean shouldCrawl() {
        if (hasLastEntityPosition) {
            return getDesiredPosture(lastEntityX, lastEntityY, lastEntityZ).isCrawling();
        }

        return getDesiredPosture().isCrawling();
    }

    public @Nullable BlockPos getTargetPos() {
        return targetPos;
    }

    /**
     * Sets terrain types to exclude from the next pathfinding search. Excluded terrains are treated as impassable.
     * Useful for restricting behavior without rebuilding the navigator.
     * <p>
     * Pass {@code null} to clear exclusions.
     * </p>
     */
    public void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains) {
        if (!Objects.equals(this.excludedTerrains, excludedTerrains)) {
            this.excludedTerrains = excludedTerrains;

            invalidateActivePathForReplan();
        }
    }

    public @Nullable Set<TerrainType> getExcludedTerrains() {
        return excludedTerrains;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public int getFailureCooldownRemainingTicks() {
        if (consecutiveFailures == 0) {
            return 0;
        }

        return (int) Math.max(0, failureCooldownTicks - (cooldownClock() - lastFailureTick));
    }

    public void setDebugCaptureEnabled(boolean debugCaptureEnabled) {
        pathFinder.setDebugCaptureEnabled(debugCaptureEnabled);
    }

    public PathfindingFeatures getPathfindingFeatures() {
        return features;
    }

    public @Nullable PathfindingProfile getPathfindingProfile() {
        return profile;
    }

    public int getPathfindingFeaturesRevision() {
        return featuresRevision;
    }

    public int consumePathfindingFeatureUsageMask() {
        var mask = pathfindingFeatureUsageMask;
        pathfindingFeatureUsageMask = 0;

        return mask;
    }

    public void setPathfindingProfile(PathfindingProfile profile) {
        if (this.profile == profile && features.equals(profile.features())) {
            return;
        }

        this.profile = profile;
        setPathfindingFeaturesInternal(profile.features(), profile);
    }

    public void setPathfindingFeatures(PathfindingFeatures features) {
        setPathfindingFeaturesInternal(features, PathfindingProfile.matching(features).orElse(null));
    }

    public void setPathfindingFeature(PathfindingFeature feature, boolean enabled) {
        setPathfindingFeatures(features.with(feature, enabled));
    }

    /**
     * Updates the destination without forcing an immediate path recomputation. The navigator will recompute the path on
     * its next recalculation cycle using this updated target.
     */
    public void updateTarget(BlockPos newTarget) {
        this.targetPos = newTarget;
    }

    /**
     * Updates the destination to an exact target center position without forcing an immediate path recomputation.
     */
    public void updateTarget(double targetX, double targetY, double targetZ) {
        this.targetPos = targetAnchorPos(targetX, targetY, targetZ);
    }

    private void setPathfindingFeaturesInternal(PathfindingFeatures features, @Nullable PathfindingProfile profile) {
        if (this.features.equals(features) && Objects.equals(this.profile, profile)) {
            return;
        }

        this.features = features;
        this.profile = profile;
        this.featuresRevision++;
        pathFinder.setFeatures(features);
        invalidateActivePathForReplan();
    }

    private void invalidateActivePathForReplan() {
        if (pendingPath != null) {
            pendingPath.cancel(false);
            pendingPath = null;
        }

        if (planner != null) {
            planner.clear();
        }

        currentPath = null;
        currentTerrain = null;
        lastStuckReplannedEdge = null;
        resetProgressTracking();

        if (targetPos != null) {
            needsRepath = true;
        }
    }

    // --- Failure backoff ---

    private boolean isInFailureCooldown(BlockPos target) {
        if (consecutiveFailures == 0) {
            return false;
        }

        // Reset cooldown if target changed significantly.
        if (lastComputedTargetPos != null && target.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED) {
            resetFailureCooldown();
            return false;
        }

        return cooldownClock() - lastFailureTick < failureCooldownTicks;
    }

    private void recordFailure() {
        consecutiveFailures++;
        lastFailureTick = cooldownClock();
        var shift = Math.min(consecutiveFailures - 1, 30);
        failureCooldownTicks = Math.min(BASE_FAILURE_COOLDOWN * (1 << shift), MAX_FAILURE_COOLDOWN);
    }

    private void resetFailureCooldown() {
        consecutiveFailures = 0;
        failureCooldownTicks = 0;
    }

    private long cooldownClock() {
        if (level instanceof Level concreteLevel) {
            return concreteLevel.getGameTime();
        }

        return (System.nanoTime() - createdNanos) / NANOS_PER_TICK;
    }

    private void advanceWaypoints(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var reachXZ = waypointReachXZ(entityWidth);
        var reachY = waypointReachY(entityHeight);

        while (!currentPath.isDone()) {
            var waypoint = currentPath.getCurrentNode();
            var waypointCenter = currentNodeTargetCenter(waypoint, entityWidth, entityHeight);
            var awaitingDropEntry = isCurrentDropEntryWaypointPending(waypoint);
            var activeReachXZ = awaitingDropEntry ? dropEntryReachXZ(entityWidth) : reachXZ;

            var dx = Math.abs(waypointCenter.x - entityX);
            var dy = Math.abs(waypointCenter.y - entityY);
            var dz = Math.abs(waypointCenter.z - entityZ);

            var withinVerticalReach = awaitingDropEntry ? entityY <= waypointCenter.y + reachY : dy <= reachY;
            if (
                !awaitingDropEntry
                    && !withinVerticalReach
                    && isDescendingSteppedFootprintWaypointWithinReach(currentPath.getCurrentNodeIndex(), entityY, reachY)
            ) {
                withinVerticalReach = true;
                markFeatureUsed(PathfindingFeature.STEPPED_FOOTPRINT_SUPPORT);
            }

            var withinReach = dx <= activeReachXZ && withinVerticalReach && dz <= activeReachXZ;
            var enteredDropShaft = awaitingDropEntry
                && entityY < waypointCenter.y - reachY
                && dx <= entityWidth
                && dz <= entityWidth;
            var skippedAhead = !withinReach
                && features.pathSkipAhead()
                && shouldSkipToNextNode(entityX, entityY, entityZ, entityWidth, entityHeight);

            if (skippedAhead) {
                markFeatureUsed(PathfindingFeature.PATH_SKIP_AHEAD);
            }

            var shouldAdvance = withinReach || enteredDropShaft || skippedAhead;

            if (!shouldAdvance) {
                break;
            }

            if (awaitingDropEntry) {
                dropEntryReached = true;
                markFeatureUsed(PathfindingFeature.DROP_DOWN_OPENINGS);
                markProgress();
                continue;
            }

            var previousTerrain = currentTerrain;

            currentPath.advance();
            markProgress();

            if (!currentPath.isDone()) {
                var nextNode = currentPath.getCurrentNode();
                var newTerrain = nextNode.getTerrainType();

                if (newTerrain != previousTerrain) {
                    fireTransitionHandlers(previousTerrain, newTerrain);
                    currentTerrain = newTerrain;
                    break;
                }
            }
        }

        smoothAnyAngleWaypoint(entityX, entityY, entityZ, entityWidth, entityHeight);
    }

    private void smoothAnyAngleWaypoint(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        if (!features.anyAngleSmoothing() || currentPath == null || currentPath.isDone()) {
            return;
        }

        var currentIndex = currentPath.getCurrentNodeIndex();
        var targetIndex = findAnyAngleSmoothingTargetIndex(entityX, entityY, entityZ, entityWidth, entityHeight);

        if (targetIndex <= currentIndex) {
            return;
        }

        markFeatureUsed(PathfindingFeature.ANY_ANGLE_SMOOTHING);

        var previousTerrain = currentTerrain;

        while (currentPath.getCurrentNodeIndex() < targetIndex && !currentPath.isDone()) {
            currentPath.advance();
        }

        markProgress();

        if (!currentPath.isDone()) {
            var newTerrain = currentPath.getCurrentNode().getTerrainType();

            if (newTerrain != previousTerrain) {
                fireTransitionHandlers(previousTerrain, newTerrain);
                currentTerrain = newTerrain;
            }
        }
    }

    private int findAnyAngleSmoothingTargetIndex(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var currentIndex = currentPath.getCurrentNodeIndex();
        var maxIndex = Math.min(
            currentPath.getNodeCount() - 1,
            currentIndex + ANY_ANGLE_SMOOTHING_MAX_LOOKAHEAD_NODES
        );

        for (var targetIndex = maxIndex; targetIndex > currentIndex; targetIndex--) {
            if (!isSmoothableNodeRange(currentIndex, targetIndex)) {
                continue;
            }

            var targetCenter = nodeTargetCenter(currentPath.getNode(targetIndex), entityWidth, entityHeight);
            var terrain = currentPath.getNode(currentIndex).getTerrainType();

            if (canTraverseDirectly(entityX, entityY, entityZ, targetCenter, entityWidth, entityHeight, terrain)) {
                return targetIndex;
            }
        }

        return currentIndex;
    }

    private boolean isSmoothableNodeRange(int fromIndex, int toIndex) {
        var from = currentPath.getNode(fromIndex);
        var y = from.getY();
        var terrain = from.getTerrainType();
        var posture = from.getPosture();

        if (from.hasDropEntryWaypoint()) {
            return false;
        }

        for (var index = fromIndex + 1; index <= toIndex; index++) {
            var node = currentPath.getNode(index);

            if (
                node.hasDropEntryWaypoint()
                    || node.getY() != y
                    || node.getTerrainType() != terrain
                    || node.getPosture() != posture
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean canTraverseDirectly(
        double entityX,
        double entityY,
        double entityZ,
        Vec3 targetCenter,
        float entityWidth,
        float entityHeight,
        TerrainType terrain
    ) {
        if (entityWidth <= 0.0f || entityHeight <= 0.0f) {
            return false;
        }

        var dx = targetCenter.x - entityX;
        var dy = targetCenter.y - entityY;
        var dz = targetCenter.z - entityZ;
        var distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        var sampleCount = Math.max(1, (int) Math.ceil(distance / ANY_ANGLE_SMOOTHING_SAMPLE_INTERVAL));

        for (var i = 1; i <= sampleCount; i++) {
            var progress = i / (double) sampleCount;
            var feetCenter = new Vec3(
                entityX + dx * progress,
                entityY + dy * progress,
                entityZ + dz * progress
            );

            if (
                !isEntityBoxClear(feetCenter, entityWidth, entityHeight)
                    || !hasEntitySupport(feetCenter, entityWidth)
                    || !hasExpectedTerrain(feetCenter, entityWidth, terrain)
            ) {
                return false;
            }
        }

        return true;
    }

    private double waypointReachXZ(float entityWidth) {
        var reach = entityWidth > 0.75f ? entityWidth / 2.0 : 0.75 - entityWidth / 2.0;

        if (config.getEvaluatorConfig().getEntityWidth() > 1) {
            return Math.min(reach, WIDE_FOOTPRINT_REACH_XZ);
        }

        return reach;
    }

    private double dropEntryReachXZ(float entityWidth) {
        return Math.max(DROP_ENTRY_MIN_REACH_XZ, entityWidth / 2.0d);
    }

    private double waypointReachY(float entityHeight) {
        var reach = Math.max(1.0, entityHeight > 0.75f ? entityHeight / 2.0 : 0.75 - entityHeight / 2.0);

        return Math.min(reach, WAYPOINT_REACH_Y);
    }

    private boolean isDescendingSteppedFootprintWaypointWithinReach(int nodeIndex, double entityY, double reachY) {
        if (!usesSteppedFootprintSupport() || currentPath == null || nodeIndex <= 0) {
            return false;
        }

        var previousNode = currentPath.getNode(nodeIndex - 1);
        var node = currentPath.getNode(nodeIndex);
        var stepDown = previousNode.getY() - node.getY();

        if (stepDown <= 0 || stepDown > config.getEvaluatorConfig().getMaxStepHeight()) {
            return false;
        }

        var dx = Math.abs(previousNode.getX() - node.getX());
        var dz = Math.abs(previousNode.getZ() - node.getZ());

        if (dx > 1 || dz > 1) {
            return false;
        }

        return entityY >= node.getY() - reachY && entityY <= previousNode.getY() + reachY;
    }

    private boolean usesSteppedFootprintSupport() {
        var evaluatorConfig = config.getEvaluatorConfig();

        return features.footprintClearance()
            && features.steppedFootprintSupport()
            && evaluatorConfig.getEntityWidth() > 1
            && evaluatorConfig.getMaxStepHeight() > 0;
    }

    private boolean usesCrawling() {
        return features.crawlThroughGaps() && config.getEvaluatorConfig().getCrawlConfig().enabled();
    }

    private boolean isNearCrawlPostureEntry(double entityX, double entityY, double entityZ, PathNode crawlNode) {
        var crawlCenter = nodeCenter(crawlNode);
        var dx = crawlCenter.x - entityX;
        var dz = crawlCenter.z - entityZ;
        var prepDistance = crawlPosturePrepDistance();

        if (dx * dx + dz * dz > prepDistance * prepDistance) {
            return false;
        }

        var verticalSlack = Math.max(1.0d, config.getEvaluatorConfig().getMaxStepHeight() + WAYPOINT_REACH_Y);

        return Math.abs(crawlCenter.y - entityY) <= verticalSlack;
    }

    private double crawlPosturePrepDistance() {
        var configuredWidth = Math.max(1.0d, config.getEvaluatorConfig().getEntityWidth());
        var observedWidth = Math.max(configuredWidth, lastEntityWidth);

        return Math.max(CRAWL_POSTURE_MIN_PREP_DISTANCE, observedWidth + 0.5d);
    }

    /**
     * Checks whether the entity has already passed the current node and should skip ahead to the next one. Skip-ahead is
     * intentionally limited to straight, same-height runs. Corners and vertical transitions are real navigation
     * waypoints, especially for wide entities on stairs.
     */
    private boolean shouldSkipToNextNode(
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var nextIndex = currentPath.getCurrentNodeIndex() + 1;

        if (nextIndex >= currentPath.getNodeCount()) {
            return false;
        }

        var currentNode = currentPath.getCurrentNode();
        var nextNode = currentPath.getNode(nextIndex);

        if (currentNode.hasDropEntryWaypoint() || nextNode.hasDropEntryWaypoint()) {
            return false;
        }

        if (currentNode.getPosture() != nextNode.getPosture()) {
            return false;
        }

        if (currentNode.getY() != nextNode.getY() || isCornerWaypoint(currentPath.getCurrentNodeIndex(), nextIndex)) {
            return false;
        }

        var currentCenter = nodeTargetCenter(currentNode, entityWidth, entityHeight);

        var toCurrentX = currentCenter.x - entityX;
        var toCurrentY = currentCenter.y - entityY;
        var toCurrentZ = currentCenter.z - entityZ;
        var distToCurrentSq = toCurrentX * toCurrentX + toCurrentY * toCurrentY + toCurrentZ * toCurrentZ;

        if (distToCurrentSq > 4.0) {
            return false;
        }

        var nextCenter = nodeTargetCenter(nextNode, entityWidth, entityHeight);

        var toNextX = nextCenter.x - entityX;
        var toNextY = nextCenter.y - entityY;
        var toNextZ = nextCenter.z - entityZ;
        var distToNextSq = toNextX * toNextX + toNextY * toNextY + toNextZ * toNextZ;

        var closerToNext = distToNextSq < distToCurrentSq;
        var veryCloseToCurrent = distToCurrentSq < 0.5;

        if (!closerToNext && !veryCloseToCurrent) {
            return false;
        }

        // Negative dot product means the current node is behind the entity relative to the next node.
        var dot = toNextX * toCurrentX + toNextY * toCurrentY + toNextZ * toCurrentZ;

        return dot < 0.0;
    }

    private boolean isCornerWaypoint(int currentIndex, int nextIndex) {
        if (currentIndex <= 0) {
            return false;
        }

        var previousNode = currentPath.getNode(currentIndex - 1);
        var currentNode = currentPath.getNode(currentIndex);
        var nextNode = currentPath.getNode(nextIndex);

        if (previousNode.getY() != currentNode.getY() || currentNode.getY() != nextNode.getY()) {
            return true;
        }

        var previousDx = Integer.compare(currentNode.getX(), previousNode.getX());
        var previousDz = Integer.compare(currentNode.getZ(), previousNode.getZ());
        var nextDx = Integer.compare(nextNode.getX(), currentNode.getX());
        var nextDz = Integer.compare(nextNode.getZ(), currentNode.getZ());

        return previousDx != nextDx || previousDz != nextDz;
    }

    private void detectStuck(
        BlockPos entityAnchorPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        if (targetPos == null || currentPath == null || currentPath.isDone()) {
            return;
        }

        var progressed = observePathProgress(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
        var ticksSinceProgress = tickCount - lastProgressTick;

        if (!progressed && ticksSinceProgress >= stuckTimeoutInTicks) {
            handleStuckEdge(entityAnchorPos);
        }
    }

    private boolean observePathProgress(
        BlockPos entityAnchorPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var currentIndex = currentPath.getCurrentNodeIndex();
        var currentCenter = currentNodeTargetCenter(currentPath.getCurrentNode(), entityWidth, entityHeight);
        var distanceToCurrent = distanceSquared(entityX, entityY, entityZ, currentCenter);
        var distanceToNext = Double.MAX_VALUE;

        if (currentIndex + 1 < currentPath.getNodeCount()) {
            distanceToNext = distanceSquared(
                entityX,
                entityY,
                entityZ,
                nodeTargetCenter(currentPath.getNode(currentIndex + 1), entityWidth, entityHeight)
            );
        }

        var progressed = currentIndex != lastObservedNodeIndex
            || distanceToCurrent < lastDistanceToCurrentNode - PROGRESS_DISTANCE_EPSILON_SQUARED
            || distanceToNext < lastDistanceToNextNode - PROGRESS_DISTANCE_EPSILON_SQUARED;

        if (progressed) {
            lastProgressTick = tickCount;
            lastObservedNodeIndex = currentIndex;
            lastDistanceToCurrentNode = distanceToCurrent;
            lastDistanceToNextNode = distanceToNext;

            var edge = currentEdgeKey(entityAnchorPos);
            if (lastStuckReplannedEdge != null && edge != null && !lastStuckReplannedEdge.equals(edge)) {
                lastStuckReplannedEdge = null;
            }
        }

        return progressed;
    }

    private void handleStuckEdge(BlockPos entityAnchorPos) {
        if (!features.stuckReplan()) {
            recordFailure();
            stop();
            return;
        }

        var edge = currentEdgeKey(entityAnchorPos);

        if (edge != null && edge.equals(lastStuckReplannedEdge)) {
            recordFailure();
            stop();
            return;
        }

        lastStuckReplannedEdge = edge;

        if (targetPos == null) {
            stop();
            return;
        }

        markFeatureUsed(PathfindingFeature.STUCK_REPLAN);

        if (planner != null) {
            planner.clear();
        }

        navigateTo(entityAnchorPos, targetPos, true);
    }

    private static final double MIN_TARGET_MOVE_DISTANCE_SQUARED = 9.0;

    private void checkRecalculate(
        BlockPos entityPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        if (targetPos == null) {
            return;
        }

        if (tickCount - lastPathComputeTick < pathRecalculateIntervalInTicks) {
            return;
        }

        var targetMoved = lastComputedTargetPos == null
            || targetPos.distSqr(lastComputedTargetPos) >= MIN_TARGET_MOVE_DISTANCE_SQUARED;

        if (targetMoved) {
            if (planner != null) {
                planner.clear();
            }

            navigateTo(entityPos, targetPos);

            // Advance past any nodes the entity has already reached so the new
            // path doesn't briefly target the start node behind the entity.
            if (currentPath != null && !currentPath.isDone()) {
                advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);
            }
        }
    }

    private void advanceToNextSegment(double entityX, double entityY, double entityZ) {
        var entityPos = entityAnchorPos(entityX, entityY, entityZ);
        var startNanos = System.nanoTime();

        preparePathFinder();
        markFeatureUsed(PathfindingFeature.SEGMENTED_PATH_PLANNING);
        this.currentPath = planner.computeNextSegment(level, entityPos);
        markFeatureUsage(pathFinder.consumeFeatureUsageMask());
        this.lastPathComputeNanos = System.nanoTime() - startNanos;
        this.lastPathComputeTick = tickCount;
        resetProgressTracking();

        if (isUsablePath(currentPath)) {
            this.currentTerrain = currentPath.getCurrentNode().getTerrainType();
            resetFailureCooldown();
        } else {
            this.currentPath = null;
            this.currentTerrain = null;
            recordFailure();

            if (planner != null) {
                planner.clear();
            }
        }
    }

    private boolean isUsablePath(@Nullable BLibPath path) {
        return path != null
            && (path.isReached()
                || path.getNodeCount() > 1
                || (shouldUsePlanner() && planner.hasActiveRoute()));
    }

    private void preparePathFinder() {
        pathFinder.setFeatures(features);
        pathFinder.setExcludedTerrains(excludedTerrains);
    }

    private boolean shouldUsePlanner() {
        return features.segmentedPathPlanning() && planner != null;
    }

    private void markProgress() {
        lastProgressTick = tickCount;
        lastDistanceToCurrentNode = Double.MAX_VALUE;
        lastDistanceToNextNode = Double.MAX_VALUE;
        lastObservedNodeIndex = currentPath != null ? currentPath.getCurrentNodeIndex() : -1;
    }

    private void resetProgressTracking() {
        lastProgressTick = tickCount;
        lastDistanceToCurrentNode = Double.MAX_VALUE;
        lastDistanceToNextNode = Double.MAX_VALUE;
        lastObservedNodeIndex = -1;
        dropEntryNodeIndex = -1;
        dropEntryReached = false;
    }

    private void fireTransitionHandlers(@Nullable TerrainType from, TerrainType to) {
        if (from == null) {
            return;
        }

        var handlers = config.getTransitionHandlers(from, to);
        var transition = new TerrainTransition(from, to, currentPath.getCurrentNodeIndex());

        for (var handler : handlers) {
            handler.onTransition(transition, currentPath);
        }
    }

    private BlockPos entityAnchorPos(double entityX, double entityY, double entityZ) {
        var centerOffset = nodeCenterOffset();

        return BlockPos.containing(entityX - centerOffset + 0.5, entityY, entityZ - centerOffset + 0.5);
    }

    private BlockPos targetAnchorPos(double targetX, double targetY, double targetZ) {
        var centerOffset = nodeCenterOffset();

        return BlockPos.containing(targetX - centerOffset + 0.5, targetY, targetZ - centerOffset + 0.5);
    }

    private Vec3 nodeCenter(PathNode node) {
        var centerOffset = nodeCenterOffset();

        return new Vec3(node.getX() + centerOffset, node.getY(), node.getZ() + centerOffset);
    }

    private Vec3 nodeTargetCenter(PathNode node, float entityWidth, float entityHeight) {
        var center = nodeCenter(node);

        if (!features.collisionShapeWaypoints()) {
            return center;
        }

        // Multi-cell footprint nodes use a different anchor model; keep those on the stable footprint center.
        if (config.getEvaluatorConfig().getEntityWidth() != 1) {
            return center;
        }

        var adjusted = shapeAwareNodeCenter(node, entityWidth, entityHeight);

        if (adjusted != null && !samePosition(adjusted, center)) {
            markFeatureUsed(PathfindingFeature.COLLISION_SHAPE_WAYPOINTS);
        }

        return adjusted != null ? adjusted : center;
    }

    private Vec3 currentNodeTargetCenter(PathNode node, float entityWidth, float entityHeight) {
        syncDropEntryTracking(node);

        if (isCurrentDropEntryWaypointPending(node)) {
            markFeatureUsed(PathfindingFeature.DROP_DOWN_OPENINGS);
            return new Vec3(node.getDropEntryX(), node.getDropEntryY(), node.getDropEntryZ());
        }

        return nodeTargetCenter(node, entityWidth, entityHeight);
    }

    private void syncDropEntryTracking(PathNode node) {
        if (currentPath == null || currentPath.isDone() || !node.hasDropEntryWaypoint()) {
            dropEntryNodeIndex = -1;
            dropEntryReached = false;
            return;
        }

        var nodeIndex = currentPath.getCurrentNodeIndex();

        if (dropEntryNodeIndex != nodeIndex) {
            dropEntryNodeIndex = nodeIndex;
            dropEntryReached = false;
        }
    }

    private boolean isCurrentDropEntryWaypointPending(PathNode node) {
        return currentPath != null
            && !currentPath.isDone()
            && node.hasDropEntryWaypoint()
            && currentPath.getCurrentNodeIndex() == dropEntryNodeIndex
            && !dropEntryReached;
    }

    private @Nullable Vec3 shapeAwareNodeCenter(PathNode node, float entityWidth, float entityHeight) {
        if (entityWidth <= 0.0f || entityHeight <= 0.0f || entityWidth > 1.0f) {
            return null;
        }

        var center = nodeCenter(node);

        if (isEntityBoxClear(center, entityWidth, entityHeight)) {
            return center;
        }

        for (var candidate : shapeWaypointCandidates(entityWidth)) {
            var candidateCenter = new Vec3(node.getX() + candidate.localX(), node.getY(), node.getZ() + candidate.localZ());

            if (isEntityBoxClear(candidateCenter, entityWidth, entityHeight)) {
                return candidateCenter;
            }
        }

        return null;
    }

    private boolean isEntityBoxClear(Vec3 feetCenter, float entityWidth, float entityHeight) {
        var halfWidth = entityWidth / 2.0d;
        var entityBox = new AABB(
            feetCenter.x - halfWidth,
            feetCenter.y,
            feetCenter.z - halfWidth,
            feetCenter.x + halfWidth,
            feetCenter.y + entityHeight,
            feetCenter.z + halfWidth
        ).deflate(COLLISION_EPSILON, 0.0, COLLISION_EPSILON);

        var minX = (int) Math.floor(entityBox.minX);
        var minY = (int) Math.floor(entityBox.minY);
        var minZ = (int) Math.floor(entityBox.minZ);
        var maxX = (int) Math.floor(entityBox.maxX - COLLISION_EPSILON);
        var maxY = (int) Math.floor(entityBox.maxY - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(entityBox.maxZ - COLLISION_EPSILON);
        var cursor = new BlockPos.MutableBlockPos();

        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                for (var z = minZ; z <= maxZ; z++) {
                    cursor.set(x, y, z);

                    var state = level.getBlockState(cursor);

                    if (state.liquid()) {
                        return false;
                    }

                    var shape = state.getCollisionShape(level, cursor, CollisionContext.empty());

                    if (shape.isEmpty()) {
                        continue;
                    }

                    for (var blockBox : shape.toAabbs()) {
                        if (blockBox.move(cursor).intersects(entityBox)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean hasEntitySupport(Vec3 feetCenter, float entityWidth) {
        var halfWidth = entityWidth / 2.0d;
        var minX = (int) Math.floor(feetCenter.x - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(feetCenter.z - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(feetCenter.x + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(feetCenter.z + halfWidth - COLLISION_EPSILON);

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                if (!hasSupportAt(x, feetCenter.y, z)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasExpectedTerrain(Vec3 feetCenter, float entityWidth, TerrainType expectedTerrain) {
        var halfWidth = entityWidth / 2.0d;
        var minX = (int) Math.floor(feetCenter.x - halfWidth + COLLISION_EPSILON);
        var minZ = (int) Math.floor(feetCenter.z - halfWidth + COLLISION_EPSILON);
        var maxX = (int) Math.floor(feetCenter.x + halfWidth - COLLISION_EPSILON);
        var maxZ = (int) Math.floor(feetCenter.z + halfWidth - COLLISION_EPSILON);
        var y = (int) Math.floor(feetCenter.y);
        var cursor = new BlockPos.MutableBlockPos();
        var supportedTerrains = config.getEvaluatorConfig().getSupportedTerrains();
        var terrainClassifier = config.getEvaluatorConfig().getTerrainClassifier();

        for (var x = minX; x <= maxX; x++) {
            for (var z = minZ; z <= maxZ; z++) {
                cursor.set(x, y, z);

                var terrain = terrainClassifier.classify(level, cursor);

                if (
                    terrain != expectedTerrain
                        || !supportedTerrains.contains(terrain)
                        || (excludedTerrains != null && excludedTerrains.contains(terrain))
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasSupportAt(int x, double feetY, int z) {
        var supportY = (int) Math.floor(feetY) - 1;
        var cursor = new BlockPos.MutableBlockPos(x, supportY, z);
        var state = level.getBlockState(cursor);

        if (state.liquid()) {
            return false;
        }

        var shape = state.getCollisionShape(level, cursor, CollisionContext.empty());

        if (shape.isEmpty()) {
            return false;
        }

        var supportProbe = new AABB(
            x + COLLISION_EPSILON,
            feetY - COLLISION_EPSILON,
            z + COLLISION_EPSILON,
            x + 1.0d - COLLISION_EPSILON,
            feetY + COLLISION_EPSILON,
            z + 1.0d - COLLISION_EPSILON
        );

        for (var blockBox : shape.toAabbs()) {
            if (blockBox.move(cursor).intersects(supportProbe)) {
                return true;
            }
        }

        return false;
    }

    private static List<WaypointCandidate> shapeWaypointCandidates(float entityWidth) {
        var halfWidth = entityWidth / 2.0d;
        var minLocal = halfWidth;
        var maxLocal = 1.0d - halfWidth;

        if (minLocal > maxLocal + COLLISION_EPSILON) {
            return List.of();
        }

        var axisSamples = shapeWaypointAxisSamples(minLocal, maxLocal);
        var candidates = new ArrayList<WaypointCandidate>();

        for (var localX : axisSamples) {
            for (var localZ : axisSamples) {
                if (Math.abs(localX - 0.5d) <= COLLISION_EPSILON && Math.abs(localZ - 0.5d) <= COLLISION_EPSILON) {
                    continue;
                }

                var dx = localX - 0.5d;
                var dz = localZ - 0.5d;
                candidates.add(new WaypointCandidate(localX, localZ, dx * dx + dz * dz));
            }
        }

        candidates.sort(
            Comparator.comparingDouble(WaypointCandidate::distanceSquared)
                .thenComparingDouble(WaypointCandidate::localX)
                .thenComparingDouble(WaypointCandidate::localZ)
        );

        return candidates;
    }

    private static List<Double> shapeWaypointAxisSamples(double minLocal, double maxLocal) {
        var samples = new ArrayList<Double>();
        addShapeWaypointAxisSample(samples, 0.5d, minLocal, maxLocal);

        var sampleCount = (int) Math.ceil(0.5d / SHAPE_WAYPOINT_SAMPLE_STEP);

        for (var i = 1; i <= sampleCount; i++) {
            var offset = i * SHAPE_WAYPOINT_SAMPLE_STEP;

            addShapeWaypointAxisSample(samples, 0.5d - offset, minLocal, maxLocal);
            addShapeWaypointAxisSample(samples, 0.5d + offset, minLocal, maxLocal);
        }

        addShapeWaypointAxisSample(samples, minLocal, minLocal, maxLocal);
        addShapeWaypointAxisSample(samples, maxLocal, minLocal, maxLocal);

        samples.sort(Comparator.comparingDouble(sample -> Math.abs(sample - 0.5d)));

        return samples;
    }

    private static void addShapeWaypointAxisSample(List<Double> samples, double sample, double minLocal, double maxLocal) {
        if (sample < minLocal - COLLISION_EPSILON || sample > maxLocal + COLLISION_EPSILON) {
            return;
        }

        var clamped = Math.max(minLocal, Math.min(maxLocal, sample));

        for (var existing : samples) {
            if (Math.abs(existing - clamped) <= COLLISION_EPSILON) {
                return;
            }
        }

        samples.add(clamped);
    }

    private record WaypointCandidate(
        double localX,
        double localZ,
        double distanceSquared
    ) {}

    private double distanceSquared(double x, double y, double z, Vec3 target) {
        var dx = target.x - x;
        var dy = target.y - y;
        var dz = target.z - z;

        return dx * dx + dy * dy + dz * dz;
    }

    private boolean samePosition(Vec3 left, Vec3 right) {
        return Math.abs(left.x - right.x) <= COLLISION_EPSILON
            && Math.abs(left.y - right.y) <= COLLISION_EPSILON
            && Math.abs(left.z - right.z) <= COLLISION_EPSILON;
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        markFeatureUsage(feature.mask());
    }

    private void markFeatureUsage(int mask) {
        pathfindingFeatureUsageMask |= mask;
    }

    private double nodeCenterOffset() {
        return Math.max(1, config.getEvaluatorConfig().getEntityWidth()) / 2.0;
    }

    private @Nullable PathEdgeKey currentEdgeKey(BlockPos entityAnchorPos) {
        if (currentPath == null || currentPath.isDone() || targetPos == null) {
            return null;
        }

        var currentIndex = currentPath.getCurrentNodeIndex();
        var to = currentPath.getCurrentNode();
        var from = currentIndex > 0 ? currentPath.getNode(currentIndex - 1) : null;
        var fromX = from != null ? from.getX() : entityAnchorPos.getX();
        var fromY = from != null ? from.getY() : entityAnchorPos.getY();
        var fromZ = from != null ? from.getZ() : entityAnchorPos.getZ();

        return new PathEdgeKey(
            fromX,
            fromY,
            fromZ,
            from != null ? from.getPosture() : PathPosture.STANDING,
            to.getX(),
            to.getY(),
            to.getZ(),
            to.getPosture(),
            targetPos.getX(),
            targetPos.getY(),
            targetPos.getZ()
        );
    }

    private record PathEdgeKey(
        int fromX,
        int fromY,
        int fromZ,
        PathPosture fromPosture,
        int toX,
        int toY,
        int toZ,
        PathPosture toPosture,
        int targetX,
        int targetY,
        int targetZ
    ) {}

}
