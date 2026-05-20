package com.blib.api.common.pathfinding.v1.navigator;

import org.jetbrains.annotations.Nullable;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;

/**
 * Dispatches terrain transition callbacks for the active navigation path.
 */
final class PathNavigationTransitionDispatcher {

    private final PathNavigatorConfig config;

    private final PathNavigationStateComponent state;

    PathNavigationTransitionDispatcher(PathNavigatorConfig config, PathNavigationStateComponent state) {
        this.config = config;
        this.state = state;
    }

    void fireTransitionHandlers(@Nullable TerrainType from, TerrainType to) {
        if (from == null) {
            return;
        }

        var handlers = config.getTransitionHandlers(from, to);
        var transition = new TerrainTransition(from, to, state.currentPath.getCurrentNodeIndex());

        for (var handler : handlers) {
            handler.onTransition(transition, state.currentPath);
        }
    }
}
