package com.blib.api.common.pathfinding.v1.navigator;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.blib.api.common.pathfinding.v1.search.PathfindingTuning;
import com.blib.api.common.pathfinding.v1.search.SearchConfig;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Owns mutable runtime pathfinding tuning and terrain policy for a navigator.
 */
final class PathNavigationRuntimeConfigComponent implements PathNavigationRuntimeConfig {

    private final PathNavigatorConfig config;

    private final Consumer<SearchConfig> searchConfigConsumer;

    private final Consumer<PathfindingTuning> tuningConsumer;

    private final Consumer<@Nullable Set<TerrainType>> excludedTerrainsConsumer;

    private final Runnable activePathInvalidator;

    private SearchConfig searchConfig;

    private PathfindingTuning pathfindingTuning;

    private int stuckTimeoutInTicks;

    private int pathRecalculateIntervalInTicks;

    private @Nullable Set<TerrainType> excludedTerrains;

    PathNavigationRuntimeConfigComponent(
        PathNavigatorConfig config,
        Consumer<SearchConfig> searchConfigConsumer,
        Consumer<PathfindingTuning> tuningConsumer,
        Consumer<@Nullable Set<TerrainType>> excludedTerrainsConsumer,
        Runnable activePathInvalidator
    ) {
        this.config = config;
        this.searchConfigConsumer = searchConfigConsumer;
        this.tuningConsumer = tuningConsumer;
        this.excludedTerrainsConsumer = excludedTerrainsConsumer;
        this.activePathInvalidator = activePathInvalidator;
        this.searchConfig = config.getSearchConfig();
        this.pathfindingTuning = config.getPathfindingTuning();
        this.stuckTimeoutInTicks = config.getStuckTimeoutInTicks();
        this.pathRecalculateIntervalInTicks = config.getPathRecalculateIntervalInTicks();
    }

    @Override
    public PathNavigatorConfig getConfig() {
        return config;
    }

    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    @Override
    public PathfindingTuning getPathfindingTuning() {
        return pathfindingTuning;
    }

    @Override
    public int getStuckTimeoutInTicks() {
        return stuckTimeoutInTicks;
    }

    @Override
    public int getPathRecalculateIntervalInTicks() {
        return pathRecalculateIntervalInTicks;
    }

    @Override
    public void setSearchConfig(SearchConfig searchConfig) {
        setPathfindingRuntimeConfig(
            searchConfig,
            pathfindingTuning,
            stuckTimeoutInTicks,
            pathRecalculateIntervalInTicks
        );
    }

    @Override
    public void setPathfindingTuning(PathfindingTuning pathfindingTuning) {
        setPathfindingRuntimeConfig(
            searchConfig,
            pathfindingTuning,
            stuckTimeoutInTicks,
            pathRecalculateIntervalInTicks
        );
    }

    @Override
    public void setStuckTimeoutInTicks(int stuckTimeoutInTicks) {
        setPathfindingRuntimeConfig(
            searchConfig,
            pathfindingTuning,
            stuckTimeoutInTicks,
            pathRecalculateIntervalInTicks
        );
    }

    @Override
    public void setPathRecalculateIntervalInTicks(int pathRecalculateIntervalInTicks) {
        setPathfindingRuntimeConfig(
            searchConfig,
            pathfindingTuning,
            stuckTimeoutInTicks,
            pathRecalculateIntervalInTicks
        );
    }

    @Override
    public void setPathfindingRuntimeConfig(
        SearchConfig searchConfig,
        PathfindingTuning pathfindingTuning,
        int stuckTimeoutInTicks,
        int pathRecalculateIntervalInTicks
    ) {
        Objects.requireNonNull(searchConfig, "searchConfig");
        Objects.requireNonNull(pathfindingTuning, "pathfindingTuning");

        var nextStuckTimeout = Math.max(1, stuckTimeoutInTicks);
        var nextPathRecalculateInterval = Math.max(1, pathRecalculateIntervalInTicks);

        if (
            this.searchConfig.equals(searchConfig)
                && this.pathfindingTuning.equals(pathfindingTuning)
                && this.stuckTimeoutInTicks == nextStuckTimeout
                && this.pathRecalculateIntervalInTicks == nextPathRecalculateInterval
        ) {
            return;
        }

        this.searchConfig = searchConfig;
        this.pathfindingTuning = pathfindingTuning;
        this.stuckTimeoutInTicks = nextStuckTimeout;
        this.pathRecalculateIntervalInTicks = nextPathRecalculateInterval;
        searchConfigConsumer.accept(searchConfig);
        tuningConsumer.accept(pathfindingTuning);
        activePathInvalidator.run();
    }

    @Override
    public void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains) {
        if (!Objects.equals(this.excludedTerrains, excludedTerrains)) {
            this.excludedTerrains = excludedTerrains;
            excludedTerrainsConsumer.accept(excludedTerrains);
            activePathInvalidator.run();
        }
    }

    @Override
    public @Nullable Set<TerrainType> getExcludedTerrains() {
        return excludedTerrains;
    }
}
