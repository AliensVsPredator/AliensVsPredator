package com.blib.api.common.pathfinding.v1.navigator;

/**
 * Public control and inspection contract for BLib path navigation.
 */
public interface PathNavigatorApi extends
    PathNavigationCommands,
    PathNavigationProgressSink {

    /**
     * Returns the read-only state view for this navigator.
     */
    PathNavigationState getState();

    /**
     * Returns the read-only posture decision view for the active path.
     */
    PathNavigationPostureView getPostureView();

    /**
     * Returns the feature/profile control surface for this navigator.
     */
    PathNavigationFeatureControl getFeatureControl();

    /**
     * Returns the runtime tuning and terrain-policy control surface for this navigator.
     */
    PathNavigationRuntimeConfig getRuntimeConfig();
}
