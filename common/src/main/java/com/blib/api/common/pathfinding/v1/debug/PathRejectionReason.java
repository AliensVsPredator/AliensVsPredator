package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * Reason a candidate node was rejected during pathfinding.
 */
public enum PathRejectionReason {
    ALREADY_CLOSED,
    OUTSIDE_CORRIDOR,
    NOT_BETTER,
    UNSUPPORTED_TERRAIN,
    UNCLASSIFIED_TERRAIN,
    NO_CLEARANCE,
    UNSTABLE_SUPPORT,
    DIAGONAL_BLOCKED,
    STEP_BLOCKED,
    FALL_BLOCKED;

    public static final StreamCodec<PathRejectionReason> CODEC = PathDebugCodecs.enumCodec(values());
}
