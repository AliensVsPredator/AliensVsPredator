package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.pathfinding.v1.cache.TerrainClassificationCache;
import com.blib.api.common.pathfinding.v1.evaluator.UnifiedTerrainEvaluator;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.search.BLibPathFinder;
import com.blib.api.common.pathfinding.v1.search.SegmentedPathPlanner;

/**
 * Standalone path navigator. Manages path planning, following, stuck detection, and terrain transition callbacks. Does
 * not extend any Minecraft class.
 * <p>
 * The consuming code calls {@link #tick(double, double, double, float, float)} each tick with the entity's exact
 * position and bounding box dimensions. The navigator advances along the path and exposes the next waypoint through
 * {@link #getState()}. The calling code is responsible for actually moving the entity toward the
 * waypoint.
 * </p>
 */
public final class PathNavigator implements PathNavigatorApi {

    private final PathNavigatorConfig config;

    private final PathNavigationStateComponent state;

    private final PathNavigationTargetComponent targets;

    private final PathNavigationFeatureComponent featureControl;

    private final PathNavigationRuntimeConfigComponent runtimeConfig;

    private final PathNavigationPostureComponent postureView;

    private final PathNavigationWaypointFollower waypointFollower;

    private final PathNavigationTransitionDispatcher transitionDispatcher;

    private final PathNavigationProgressTracker progressTracker;

    private final PathNavigationPlanningComponent planning;

    public PathNavigator(LevelReader level, PathNavigatorConfig config) {
        this(level, config, null);
    }

    public PathNavigator(LevelReader level, PathNavigatorConfig config, @Nullable TerrainClassificationCache classificationCache) {
        this.config = config;
        var pathFinder = new BLibPathFinder(
            new UnifiedTerrainEvaluator(config.getEvaluatorConfig(), classificationCache),
            config.getSearchConfig(),
            classificationCache
        );
        var failureBackoff = new PathNavigationFailureBackoff(level);
        this.state = new PathNavigationStateComponent(
            pathFinder::getLastSearchSnapshot,
            this::resolveCurrentTargetCenter,
            this::resolveCanOpenDoors,
            failureBackoff::cooldownClock
        );
        this.runtimeConfig = new PathNavigationRuntimeConfigComponent(
            config,
            pathFinder::setSearchConfig,
            pathFinder::setTuning,
            pathFinder::setExcludedTerrains,
            this::invalidateActivePathForReplan
        );
        this.featureControl = new PathNavigationFeatureComponent(
            config.getDefaultFeatures(),
            pathFinder::setFeatures,
            pathFinder::setDebugCaptureEnabled,
            this::invalidateActivePathForFeatureChange
        );
        this.postureView = new PathNavigationPostureComponent(
            config,
            state,
            this::activePathfindingFeatures,
            this::markFeatureUsed
        );
        var spaceQuery = new PathNavigationSpaceQuery(level, config, runtimeConfig::getExcludedTerrains);
        this.transitionDispatcher = new PathNavigationTransitionDispatcher(config, state);
        this.waypointFollower = new PathNavigationWaypointFollower(
            config,
            state,
            spaceQuery,
            this::activePathfindingFeatures,
            this::markFeatureUsed,
            this::markProgress,
            transitionDispatcher::fireTransitionHandlers
        );
        this.progressTracker = new PathNavigationProgressTracker(
            state,
            waypointFollower,
            runtimeConfig::getStuckTimeoutInTicks,
            this::activePathfindingFeatures,
            this::markFeatureUsed
        );
        this.targets = new PathNavigationTargetComponent(
            level,
            config,
            state,
            spaceQuery,
            this::activePathfindingFeatures,
            this::markFeatureUsed
        );
        pathFinder.setTuning(runtimeConfig.getPathfindingTuning());
        var planner = classificationCache != null
            ? new SegmentedPathPlanner(pathFinder)
            : null;
        pathFinder.setFeatures(featureControl.getDefaultPathfindingFeatures());
        this.planning = new PathNavigationPlanningComponent(
            level,
            state,
            pathFinder,
            planner,
            targets,
            featureControl,
            runtimeConfig,
            failureBackoff,
            waypointFollower,
            progressTracker,
            this::resetProgressTracking
        );
    }

    /**
     * Creates a navigation request for the target position. The request begins only when {@link PathNavigationRequest#start()}
     * is called.
     */
    public PathNavigationRequest navigateTo(BlockPos entityPos, BlockPos target) {
        return new PathNavigationRequestComponent(planning, entityPos, target, null);
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
        state.tickCount++;
        state.lastEntityX = entityX;
        state.lastEntityY = entityY;
        state.lastEntityZ = entityZ;
        state.hasLastEntityPosition = true;
        state.lastEntityWidth = entityWidth;
        state.lastEntityHeight = entityHeight;
        planning.checkPendingPath();

        planning.handleQueuedRepath(entityX, entityY, entityZ);

        // Advance to next segment if current path is done but route hasn't reached the final target.
        planning.advanceSegmentIfNeeded(entityX, entityY, entityZ);
        state.refreshTerminalLifecycle();

        if (!state.hasActivePath()) {
            return;
        }

        var entityAnchorPos = targets.entityAnchorPos(entityX, entityY, entityZ);

        waypointFollower.advanceWaypoints(entityX, entityY, entityZ, entityWidth, entityHeight);

        // After waypoint advancement, check again for segment transition.
        planning.advanceSegmentIfNeeded(entityX, entityY, entityZ);
        state.refreshTerminalLifecycle();

        if (!state.hasActivePath()) {
            return;
        }

        detectStuck(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
        planning.checkRecalculate(entityAnchorPos, entityX, entityY, entityZ, entityWidth, entityHeight);
    }

    /**
     * Stops navigation and clears the current path.
     */
    public void stop() {
        planning.stopPlanning();

        targets.clearActiveTarget();
        progressTracker.clearStuckReplanHistory();
        featureControl.clearActivePathfindingFeatures();
        resetProgressTracking();
        state.enterIdle();
    }

    public void requestReplan() {
        invalidateActivePathForReplan();
    }

    public PathNavigationState getState() {
        return state;
    }

    public PathNavigationPostureView getPostureView() {
        return postureView;
    }

    public PathNavigationAnchorResolver getAnchorResolver() {
        return targets;
    }

    private @Nullable Vec3 resolveCurrentTargetCenter() {
        if (!state.hasActivePath()) {
            return null;
        }

        return waypointFollower.resolveCurrentTargetCenter();
    }

    private boolean resolveCanOpenDoors() {
        return activePathfindingFeatures().doorOpening() && config.getEvaluatorConfig().canOpenDoors();
    }

    public PathNavigationFeatureControl getFeatureControl() {
        return featureControl;
    }

    public PathNavigationRuntimeConfig getRuntimeConfig() {
        return runtimeConfig;
    }

    /**
     * Updates the destination without forcing an immediate path recomputation. The navigator will recompute the path on
     * its next recalculation cycle using this updated target.
     */
    public void updateTarget(BlockPos newRawTarget) {
        targets.updateTarget(newRawTarget);
    }

    private PathfindingFeatures activePathfindingFeatures() {
        return featureControl.getPathfindingFeatures();
    }

    private void invalidateActivePathForReplan() {
        planning.invalidateActivePathForReplan();
    }

    private void invalidateActivePathForFeatureChange() {
        planning.invalidateActivePathForReplan(true);
    }

    private void detectStuck(
        BlockPos entityAnchorPos,
        double entityX,
        double entityY,
        double entityZ,
        float entityWidth,
        float entityHeight
    ) {
        var decision = progressTracker.detectStuck(
            entityAnchorPos,
            entityX,
            entityY,
            entityZ,
            entityWidth,
            entityHeight
        );

        if (decision == PathNavigationProgressTracker.StuckDecision.NONE) {
            return;
        }

        if (decision == PathNavigationProgressTracker.StuckDecision.STOP) {
            planning.recordFailure();
            stop();
            return;
        }

        planning.replanAfterStuck(entityAnchorPos);
    }

    private void markProgress() {
        progressTracker.markProgress();
    }

    public void markPathProgress() {
        markProgress();
    }

    private void resetProgressTracking() {
        progressTracker.resetProgressTracking();
        waypointFollower.reset();
    }

    public void markPathfindingFeatureUsed(PathfindingFeature feature) {
        featureControl.markFeatureUsed(feature);
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureControl.markFeatureUsed(feature);
    }

}
