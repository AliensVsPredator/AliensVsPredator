package com.blib.api.common.pathfinding.v1.navigator;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

import com.blib.api.common.pathfinding.v1.search.PathfindingTuning;
import com.blib.api.common.pathfinding.v1.search.SearchConfig;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Runtime tuning and terrain policy controls for BLib path navigation.
 */
public interface PathNavigationRuntimeConfig {

    PathNavigatorConfig getConfig();

    SearchConfig getSearchConfig();

    PathfindingTuning getPathfindingTuning();

    int getStuckTimeoutInTicks();

    int getPathRecalculateIntervalInTicks();

    void setSearchConfig(SearchConfig searchConfig);

    void setPathfindingTuning(PathfindingTuning pathfindingTuning);

    void setStuckTimeoutInTicks(int stuckTimeoutInTicks);

    void setPathRecalculateIntervalInTicks(int pathRecalculateIntervalInTicks);

    void setPathfindingRuntimeConfig(
        SearchConfig searchConfig,
        PathfindingTuning pathfindingTuning,
        int stuckTimeoutInTicks,
        int pathRecalculateIntervalInTicks
    );

    void setExcludedTerrains(@Nullable Set<TerrainType> excludedTerrains);

    @Nullable Set<TerrainType> getExcludedTerrains();
}
