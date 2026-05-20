package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * Movement category attempted while expanding a path node.
 */
public enum PathEdgeDebugType {
    SAME_LEVEL,
    STEP_UP,
    STEP_DOWN,
    DROP_OPENING,
    WATER_TRAVEL,
    WATER_ENTRY,
    WATER_EXIT,
    WATER_VERTICAL,
    BLOCK_BREAKING,
    SEARCH_PRUNING,
    UNKNOWN;

    public static final StreamCodec<PathEdgeDebugType> CODEC = PathDebugCodecs.enumCodec(values());
}
