package com.blib.api.common.pathfinding.v1.navigator;

import org.jetbrains.annotations.Nullable;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;
import com.blib.api.common.pathfinding.v1.feature.PathfindingProfile;

/**
 * Feature flag controls and feature-usage inspection for BLib path navigation.
 */
public interface PathNavigationFeatureControl {

    void setDebugCaptureEnabled(boolean debugCaptureEnabled);

    PathfindingFeatures getPathfindingFeatures();

    PathfindingFeatures getDefaultPathfindingFeatures();

    @Nullable PathfindingProfile getPathfindingProfile();

    int getPathfindingFeaturesRevision();

    long consumePathfindingFeatureUsageMask();

    void setPathfindingProfile(PathfindingProfile profile);

    void setPathfindingFeatures(PathfindingFeatures features);

    void setPathfindingFeature(PathfindingFeature feature, boolean enabled);
}
