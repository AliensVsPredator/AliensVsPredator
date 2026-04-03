package com.blib.api.common.pathfinding.v1.search;

/**
 * Configuration for the A* search algorithm.
 *
 * @param maxSearchNodes  maximum number of nodes to evaluate before giving up
 * @param heuristicWeight weight for the heuristic (1.0 = standard A*, >1.0 = greedy/faster but less optimal)
 * @param maxPathLength   maximum number of nodes in the result path
 */
public record SearchConfig(int maxSearchNodes, float heuristicWeight, int maxPathLength) {

    public static final SearchConfig DEFAULT = new SearchConfig(256, 1.4f, 128);
}
