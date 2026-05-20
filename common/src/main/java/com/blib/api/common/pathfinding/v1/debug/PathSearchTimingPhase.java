package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * Timed phase within one path search. These are debug-only timings intended to explain search cost shape.
 */
public enum PathSearchTimingPhase {
    TOTAL_SEARCH,
    NODE_RESOLUTION,
    SEARCH_SETUP,
    OPEN_SET_POLL,
    CLOSED_NODE_RECORD,
    GOAL_TEST,
    BEST_NODE_UPDATE,
    NEIGHBOR_GENERATION,
    NEIGHBOR_FILTERING,
    EDGE_COSTING,
    QUEUE_UPDATE,
    PATH_BUILD,
    PATH_MATERIALIZATION,
    SNAPSHOT_BUILD,
    BIDIRECTIONAL_DIRECTION_SELECT,
    BIDIRECTIONAL_MEET_CHECK;

    public static final StreamCodec<PathSearchTimingPhase> CODEC = PathDebugCodecs.enumCodec(values());
}
