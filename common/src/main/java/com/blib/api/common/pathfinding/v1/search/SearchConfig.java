package com.blib.api.common.pathfinding.v1.search;

/**
 * Configuration for the A* search algorithm.
 *
 * @param maxSearchNodes  maximum number of nodes to evaluate before giving up
 * @param heuristicWeight weight for the heuristic (1.0 = standard A*, >1.0 = greedy/faster but less optimal)
 * @param maxPathLength   maximum number of nodes in the result path
 */
public record SearchConfig(int maxSearchNodes, float heuristicWeight, int maxPathLength) {

    private static final int FOLLOW_RANGE_MULTIPLIER = 16;

    private static final float DEFAULT_HEURISTIC_WEIGHT = 1.0f;

    private static final int DEFAULT_MAX_PATH_LENGTH = 128;

    public static final SearchConfig DEFAULT = new SearchConfig(256, DEFAULT_HEURISTIC_WEIGHT, DEFAULT_MAX_PATH_LENGTH);

    /**
     * Creates a SearchConfig scaled to the entity's follow range.
     * maxSearchNodes = followRange * 16, matching vanilla Minecraft's budget scaling.
     */
    public static SearchConfig fromFollowRange(float followRange) {
        var maxNodes = (int) (followRange * FOLLOW_RANGE_MULTIPLIER);

        return new SearchConfig(maxNodes, DEFAULT_HEURISTIC_WEIGHT, DEFAULT_MAX_PATH_LENGTH);
    }
}
