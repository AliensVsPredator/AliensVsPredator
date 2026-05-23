package com.blib.api.common.pathfinding.v1.navigator;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;

/**
 * Owns feature defaults, per-path feature overrides, feature profiles, revision tracking, and feature-usage accounting.
 */
final class PathNavigationFeatureComponent implements PathNavigationFeatureControl {

    private final Consumer<PathfindingFeatures> defaultFeaturesChangeConsumer;

    private final Consumer<Boolean> debugCaptureConsumer;

    private final Runnable activePathInvalidator;

    private PathfindingFeatures defaultFeatures;

    private @Nullable PathfindingFeatures activePathfindingFeatures;

    private @Nullable PathfindingFeatures pendingPathfindingFeatures;

    private @Nullable PathfindingProfile profile;

    private int revision;

    private int pendingRevision;

    private long usageMask;

    PathNavigationFeatureComponent(
        PathfindingFeatures defaultFeatures,
        Consumer<PathfindingFeatures> defaultFeaturesChangeConsumer,
        Consumer<Boolean> debugCaptureConsumer,
        Runnable activePathInvalidator
    ) {
        this.defaultFeatures = defaultFeatures;
        this.defaultFeaturesChangeConsumer = defaultFeaturesChangeConsumer;
        this.debugCaptureConsumer = debugCaptureConsumer;
        this.activePathInvalidator = activePathInvalidator;
        this.profile = PathfindingProfile.matching(defaultFeatures).orElse(null);
    }

    @Override
    public void setDebugCaptureEnabled(boolean debugCaptureEnabled) {
        debugCaptureConsumer.accept(debugCaptureEnabled);
    }

    @Override
    public PathfindingFeatures getPathfindingFeatures() {
        return activePathfindingFeatures != null ? activePathfindingFeatures : defaultFeatures;
    }

    @Override
    public PathfindingFeatures getDefaultPathfindingFeatures() {
        return defaultFeatures;
    }

    @Override
    public @Nullable PathfindingProfile getPathfindingProfile() {
        return profile;
    }

    @Override
    public int getPathfindingFeaturesRevision() {
        return revision;
    }

    void setActivePathfindingFeatures(@Nullable PathfindingFeatures pathfindingFeatures) {
        this.activePathfindingFeatures = pathfindingFeatures;
    }

    @Nullable PathfindingFeatures activePathfindingFeaturesOverride() {
        return activePathfindingFeatures;
    }

    void clearActivePathfindingFeatures() {
        this.activePathfindingFeatures = null;
    }

    void setPendingPathfindingFeatures(PathfindingFeatures searchFeatures) {
        this.pendingRevision = revision;
        this.pendingPathfindingFeatures = searchFeatures;
    }

    void clearPendingPathfindingFeatures() {
        this.pendingPathfindingFeatures = null;
    }

    boolean pendingPathfindingFeaturesMatchActive() {
        return pendingRevision == revision && Objects.equals(pendingPathfindingFeatures, getPathfindingFeatures());
    }

    @Override
    public long consumePathfindingFeatureUsageMask() {
        var mask = usageMask;
        usageMask = 0L;

        return mask;
    }

    void markFeatureUsed(PathfindingFeature feature) {
        markFeatureUsage(feature.mask());
    }

    void markFeatureUsage(long mask) {
        usageMask |= mask;
    }

    @Override
    public void setPathfindingProfile(PathfindingProfile profile) {
        if (this.profile == profile && defaultFeatures.equals(profile.features())) {
            return;
        }

        setDefaultFeaturesInternal(profile.features(), profile);
    }

    @Override
    public void setPathfindingFeatures(PathfindingFeatures features) {
        setDefaultFeaturesInternal(features, PathfindingProfile.matching(features).orElse(null));
    }

    @Override
    public void setPathfindingFeature(PathfindingFeature feature, boolean enabled) {
        setPathfindingFeatures(defaultFeatures.with(feature, enabled));
    }

    private void setDefaultFeaturesInternal(PathfindingFeatures features, @Nullable PathfindingProfile profile) {
        if (this.defaultFeatures.equals(features) && Objects.equals(this.profile, profile)) {
            return;
        }

        var previousDefaultFeatures = this.defaultFeatures;
        this.defaultFeatures = features;
        this.profile = profile;
        this.activePathfindingFeatures = rebaseActivePathfindingFeatures(previousDefaultFeatures, features);
        this.revision++;
        defaultFeaturesChangeConsumer.accept(features);
        activePathInvalidator.run();
    }

    private @Nullable PathfindingFeatures rebaseActivePathfindingFeatures(
        PathfindingFeatures previousDefaultFeatures,
        PathfindingFeatures nextDefaultFeatures
    ) {
        if (activePathfindingFeatures == null) {
            return null;
        }

        var rebased = nextDefaultFeatures;

        for (var feature : PathfindingFeature.values()) {
            if (activePathfindingFeatures.enabled(feature) != previousDefaultFeatures.enabled(feature)) {
                rebased = rebased.with(feature, activePathfindingFeatures.enabled(feature));
            }
        }

        return rebased;
    }
}
