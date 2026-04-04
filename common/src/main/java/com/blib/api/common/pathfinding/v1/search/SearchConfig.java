package com.blib.api.common.pathfinding.v1.search;

/**
 * Configuration for the A* search algorithm.
 *
 * @param maxSearchNodes  maximum number of nodes to evaluate before giving up
 * @param heuristicWeight weight for the heuristic (1.0 = standard A*, >1.0 = greedy/faster but less optimal)
 * @param maxPathLength   maximum number of nodes in the result path
 * @param elevationWeight multiplier for the vertical (Y) component of the heuristic. Values greater than 1.0 make the
 *                        search prioritize closing vertical gaps, reducing horizontal ground sprawl when the target is
 *                        above or below. 1.0 = standard Euclidean, 2.0+ = strongly prefer gaining/losing elevation.
 */
public record SearchConfig(
    int maxSearchNodes,
    float heuristicWeight,
    int maxPathLength,
    float elevationWeight
) {

    private static final int FOLLOW_RANGE_MULTIPLIER = 16;

    private static final float DEFAULT_HEURISTIC_WEIGHT = 1.5f;

    private static final int DEFAULT_MAX_PATH_LENGTH = 128;

    private static final float DEFAULT_ELEVATION_WEIGHT = 1.0f;

    public static final SearchConfig DEFAULT = new SearchConfig(
        256,
        DEFAULT_HEURISTIC_WEIGHT,
        DEFAULT_MAX_PATH_LENGTH,
        DEFAULT_ELEVATION_WEIGHT
    );

    /**
     * Creates a SearchConfig scaled to the entity's follow range. maxSearchNodes = followRange * 16, matching vanilla
     * Minecraft's budget scaling.
     */
    public static SearchConfig fromFollowRange(float followRange) {
        var maxNodes = (int) (followRange * FOLLOW_RANGE_MULTIPLIER);

        return new SearchConfig(maxNodes, DEFAULT_HEURISTIC_WEIGHT, DEFAULT_MAX_PATH_LENGTH, DEFAULT_ELEVATION_WEIGHT);
    }

    /**
     * Returns a copy of this config with the given elevation weight.
     */
    public SearchConfig withElevationWeight(float weight) {
        return new SearchConfig(maxSearchNodes, heuristicWeight, maxPathLength, weight);
    }
}
