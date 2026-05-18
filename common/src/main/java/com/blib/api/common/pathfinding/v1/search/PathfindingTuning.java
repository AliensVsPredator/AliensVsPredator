package com.blib.api.common.pathfinding.v1.search;

/**
 * Runtime tuning values for pathfinding behavior that are not part of the terrain evaluator or A* search config.
 *
 * @param corridorDistanceThreshold minimum Manhattan distance before section-corridor planning is used
 * @param sectionSearchNodeBudget   maximum section-level nodes evaluated while finding a corridor
 * @param corridorBufferRadius      number of sections to buffer around each section in the corridor path
 * @param asyncChunkMargin          chunk margin preloaded around async path searches
 * @param minImprovement            minimum A* cost improvement required before replacing a node's parent
 */
public record PathfindingTuning(
    int corridorDistanceThreshold,
    int sectionSearchNodeBudget,
    int corridorBufferRadius,
    int asyncChunkMargin,
    float minImprovement
) {

    public static final int DEFAULT_CORRIDOR_DISTANCE_THRESHOLD = 32;

    public static final int DEFAULT_SECTION_SEARCH_NODE_BUDGET = 128;

    public static final int DEFAULT_CORRIDOR_BUFFER_RADIUS = 1;

    public static final int DEFAULT_ASYNC_CHUNK_MARGIN = 2;

    public static final float DEFAULT_MIN_IMPROVEMENT = 0.01f;

    public static final PathfindingTuning DEFAULT = new PathfindingTuning(
        DEFAULT_CORRIDOR_DISTANCE_THRESHOLD,
        DEFAULT_SECTION_SEARCH_NODE_BUDGET,
        DEFAULT_CORRIDOR_BUFFER_RADIUS,
        DEFAULT_ASYNC_CHUNK_MARGIN,
        DEFAULT_MIN_IMPROVEMENT
    );

    public PathfindingTuning {
        corridorDistanceThreshold = Math.max(0, corridorDistanceThreshold);
        sectionSearchNodeBudget = Math.max(1, sectionSearchNodeBudget);
        corridorBufferRadius = Math.max(0, corridorBufferRadius);
        asyncChunkMargin = Math.max(0, asyncChunkMargin);
        minImprovement = finiteAtLeast(minImprovement, 0.0f, DEFAULT_MIN_IMPROVEMENT);
    }

    private static float finiteAtLeast(float value, float minimum, float fallback) {
        if (!Float.isFinite(value)) {
            return fallback;
        }

        return Math.max(minimum, value);
    }
}
