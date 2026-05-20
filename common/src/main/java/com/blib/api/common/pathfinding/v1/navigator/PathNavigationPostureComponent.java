package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.node.PathPosture;

/**
 * Owns posture decisions derived from the active navigation path.
 */
final class PathNavigationPostureComponent implements PathNavigationPostureView {

    private static final double CRAWL_POSTURE_MIN_PREP_DISTANCE = 1.5;

    private static final double WAYPOINT_REACH_Y = 0.45;

    private final PathNavigatorConfig config;

    private final PathNavigationStateComponent state;

    private final Supplier<PathfindingFeatures> activeFeaturesSupplier;

    private final Consumer<PathfindingFeature> featureUsageConsumer;

    PathNavigationPostureComponent(
        PathNavigatorConfig config,
        PathNavigationStateComponent state,
        Supplier<PathfindingFeatures> activeFeaturesSupplier,
        Consumer<PathfindingFeature> featureUsageConsumer
    ) {
        this.config = config;
        this.state = state;
        this.activeFeaturesSupplier = activeFeaturesSupplier;
        this.featureUsageConsumer = featureUsageConsumer;
    }

    /**
     * Returns the posture required by the active waypoint. A null or completed path requires standing by default.
     */
    @Override
    public PathPosture getCurrentRequiredPosture() {
        if (state.currentPath == null || state.currentPath.isDone()) {
            return PathPosture.STANDING;
        }

        var posture = state.currentPath.getCurrentNode().getPosture();

        if (posture.isCrawling()) {
            if (!usesCrawling()) {
                return PathPosture.STANDING;
            }

            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
        }

        if (posture.isSwimming()) {
            markFeatureUsed(PathfindingFeature.WATER_SWIM_CLEARANCE);
        }

        return posture;
    }

    /**
     * Returns the posture required by the active waypoint. Use
     * {@link #getDesiredPosture(double, double, double)} when movement code wants near-entry crawl anticipation.
     */
    @Override
    public PathPosture getDesiredPosture() {
        if (state.currentPath == null || state.currentPath.isDone()) {
            return PathPosture.STANDING;
        }

        if (state.currentPath.getCurrentNode().requiresCrawling()) {
            if (!usesCrawling()) {
                return PathPosture.STANDING;
            }

            markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
            return PathPosture.CRAWLING;
        }

        if (state.currentPath.getCurrentNode().requiresSwimming()) {
            markFeatureUsed(PathfindingFeature.WATER_SWIM_CLEARANCE);
            return PathPosture.SWIMMING;
        }

        return PathPosture.STANDING;
    }

    /**
     * Returns the posture movement code should prefer right now, including a short distance-gated lookahead so the
     * entity lowers its hitbox only when it is physically close to a crawl-only waypoint.
     */
    @Override
    public PathPosture getDesiredPosture(double entityX, double entityY, double entityZ) {
        var currentPosture = getDesiredPosture();

        if (
            currentPosture.isCrawling()
                || currentPosture.isSwimming()
                || state.currentPath == null
                || state.currentPath.isDone()
                || !usesCrawling()
        ) {
            return currentPosture;
        }

        var currentIndex = state.currentPath.getCurrentNodeIndex();
        var lookahead = config.getEvaluatorConfig().getCrawlConfig().postureLookaheadNodes();
        var endIndex = Math.min(state.currentPath.getNodeCount() - 1, currentIndex + lookahead);

        for (var index = currentIndex + 1; index <= endIndex; index++) {
            if (state.currentPath.getNode(index).requiresCrawling()) {
                if (isNearCrawlPostureEntry(entityX, entityY, entityZ, state.currentPath.getNode(index))) {
                    markFeatureUsed(PathfindingFeature.CRAWL_THROUGH_GAPS);
                    return PathPosture.CRAWLING;
                }

                return PathPosture.STANDING;
            }
        }

        return PathPosture.STANDING;
    }

    @Override
    public boolean shouldCrawl() {
        if (state.hasLastEntityPosition) {
            return getDesiredPosture(state.lastEntityX, state.lastEntityY, state.lastEntityZ).isCrawling();
        }

        return getDesiredPosture().isCrawling();
    }

    private boolean usesCrawling() {
        return activeFeaturesSupplier.get().crawlThroughGaps() && config.getEvaluatorConfig().getCrawlConfig().enabled();
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
        var observedWidth = Math.max(configuredWidth, state.lastEntityWidth);

        return Math.max(CRAWL_POSTURE_MIN_PREP_DISTANCE, observedWidth + 0.5d);
    }

    private Vec3 nodeCenter(PathNode node) {
        var centerOffset = Math.max(1, config.getEvaluatorConfig().getEntityWidth()) / 2.0;

        return new Vec3(node.getX() + centerOffset, node.getY(), node.getZ() + centerOffset);
    }

    private void markFeatureUsed(PathfindingFeature feature) {
        featureUsageConsumer.accept(feature);
    }
}
