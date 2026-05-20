package com.blib.api.common.pathfinding.v1.navigator;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;

/**
 * Internal-facing hooks for executors that contribute progress to an active path.
 */
public interface PathNavigationProgressSink {

    void markPathProgress();

    void markPathfindingFeatureUsed(PathfindingFeature feature);
}
