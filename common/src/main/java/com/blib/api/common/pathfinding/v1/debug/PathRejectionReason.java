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
    NO_CLEARANCE,
    UNSTABLE_SUPPORT,
    DIAGONAL_CORNER_BLOCKED,
    DROP_OPENING_BLOCKED,
    CANDIDATE_REJECTED,
    BLOCK_BREAK_DISABLED,
    BLOCK_BREAK_POLICY_REJECTED,
    BLOCK_BREAK_LIMIT_EXCEEDED,
    BLOCK_BREAK_UNBREAKABLE,
    BLOCK_BREAK_LIQUID_BLOCKED;

    public static final StreamCodec<PathRejectionReason> CODEC = PathDebugCodecs.enumCodec(values());
}
