package com.blib.api.common.pathfinding.v1.navigator;

import com.blib.api.common.pathfinding.v1.node.PathPosture;

/**
 * Read-only posture decisions derived from the active BLib path.
 */
public interface PathNavigationPostureView {

    PathPosture getCurrentRequiredPosture();

    PathPosture getDesiredPosture();

    PathPosture getDesiredPosture(double entityX, double entityY, double entityZ);

    boolean shouldCrawl();
}
