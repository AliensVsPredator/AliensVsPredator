package com.blib.api.common.pathfinding.v1.navigator;

import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluatorConfig;
import com.blib.api.common.pathfinding.v1.search.SearchConfig;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransitionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for a {@link PathNavigator}. Built via the {@link Builder}.
 */
public final class PathNavigatorConfig {

    private final TerrainEvaluatorConfig evaluatorConfig;

    private final SearchConfig searchConfig;

    private final Map<TransitionKey, List<TerrainTransitionHandler>> transitionHandlers;

    private final Map<Integer, Runnable> postureEnterCallbacks;

    private final float waypointReachDistance;

    private final int stuckTimeoutInTicks;

    private final int pathRecalculateIntervalInTicks;

    private PathNavigatorConfig(
        TerrainEvaluatorConfig evaluatorConfig,
        SearchConfig searchConfig,
        Map<TransitionKey, List<TerrainTransitionHandler>> transitionHandlers,
        Map<Integer, Runnable> postureEnterCallbacks,
        float waypointReachDistance,
        int stuckTimeoutInTicks,
        int pathRecalculateIntervalInTicks
    ) {
        this.evaluatorConfig = evaluatorConfig;
        this.searchConfig = searchConfig;
        this.transitionHandlers = Map.copyOf(transitionHandlers);
        this.postureEnterCallbacks = Map.copyOf(postureEnterCallbacks);
        this.waypointReachDistance = waypointReachDistance;
        this.stuckTimeoutInTicks = stuckTimeoutInTicks;
        this.pathRecalculateIntervalInTicks = pathRecalculateIntervalInTicks;
    }

    public static Builder builder(TerrainEvaluatorConfig evaluatorConfig) {
        return new Builder(evaluatorConfig);
    }

    public TerrainEvaluatorConfig getEvaluatorConfig() {
        return evaluatorConfig;
    }

    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    public List<TerrainTransitionHandler> getTransitionHandlers(TerrainType from, TerrainType to) {
        return transitionHandlers.getOrDefault(new TransitionKey(from, to), List.of());
    }

    public void firePostureEnter(int postureIndex) {
        var callback = postureEnterCallbacks.get(postureIndex);

        if (callback != null) {
            callback.run();
        }
    }

    public float getWaypointReachDistance() {
        return waypointReachDistance;
    }

    public int getStuckTimeoutInTicks() {
        return stuckTimeoutInTicks;
    }

    public int getPathRecalculateIntervalInTicks() {
        return pathRecalculateIntervalInTicks;
    }

    private record TransitionKey(TerrainType from, TerrainType to) {
    }

    public static final class Builder {

        private static final float DEFAULT_WAYPOINT_REACH_DISTANCE = 1.0f;

        private static final int DEFAULT_STUCK_TIMEOUT_IN_TICKS = 40;

        private static final int DEFAULT_PATH_RECALCULATE_INTERVAL_IN_TICKS = 5;

        private final TerrainEvaluatorConfig evaluatorConfig;

        private final Map<TransitionKey, List<TerrainTransitionHandler>> transitionHandlers;

        private final Map<Integer, Runnable> postureEnterCallbacks;

        private SearchConfig searchConfig;

        private float waypointReachDistance;

        private int stuckTimeoutInTicks;

        private int pathRecalculateIntervalInTicks;

        private Builder(TerrainEvaluatorConfig evaluatorConfig) {
            this.evaluatorConfig = evaluatorConfig;
            this.transitionHandlers = new HashMap<>();
            this.postureEnterCallbacks = new HashMap<>();
            this.searchConfig = SearchConfig.DEFAULT;
            this.waypointReachDistance = DEFAULT_WAYPOINT_REACH_DISTANCE;
            this.stuckTimeoutInTicks = DEFAULT_STUCK_TIMEOUT_IN_TICKS;
            this.pathRecalculateIntervalInTicks = DEFAULT_PATH_RECALCULATE_INTERVAL_IN_TICKS;
        }

        public Builder withSearchConfig(SearchConfig config) {
            this.searchConfig = config;
            return this;
        }

        public Builder onPostureEnter(int postureIndex, Runnable callback) {
            postureEnterCallbacks.put(postureIndex, callback);
            return this;
        }

        public Builder addTransitionHandler(TerrainType from, TerrainType to, TerrainTransitionHandler handler) {
            transitionHandlers.computeIfAbsent(new TransitionKey(from, to), $ -> new ArrayList<>()).add(handler);
            return this;
        }

        public Builder withWaypointReachDistance(float distance) {
            this.waypointReachDistance = distance;
            return this;
        }

        public Builder withStuckTimeoutInTicks(int ticks) {
            this.stuckTimeoutInTicks = ticks;
            return this;
        }

        public Builder withPathRecalculateIntervalInTicks(int ticks) {
            this.pathRecalculateIntervalInTicks = ticks;
            return this;
        }

        public PathNavigatorConfig build() {
            return new PathNavigatorConfig(
                evaluatorConfig,
                searchConfig,
                transitionHandlers,
                postureEnterCallbacks,
                waypointReachDistance,
                stuckTimeoutInTicks,
                pathRecalculateIntervalInTicks
            );
        }
    }
}
