package com.blib.api.common.pathfinding.v1.transition;

import com.blib.api.common.pathfinding.v1.path.BLibPath;

/**
 * Callback fired when an entity crosses a terrain boundary during path following. Implementations handle
 * terrain-specific behavior like switching movement physics or triggering animations.
 */
@FunctionalInterface
public interface TerrainTransitionHandler {

    void onTransition(TerrainTransition transition, BLibPath path);
}
