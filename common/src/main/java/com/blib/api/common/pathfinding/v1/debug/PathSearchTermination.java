package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * Exact reason a path search stopped.
 */
public enum PathSearchTermination {
    GOAL_REACHED,
    BUDGET_EXHAUSTED,
    OPEN_SET_EXHAUSTED,
    START_ONLY;

    public static final StreamCodec<PathSearchTermination> CODEC = PathDebugCodecs.enumCodec(values());
}
