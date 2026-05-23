package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * User-facing result category for a path search.
 */
public enum PathSearchOutcome {
    COMPLETE,
    PARTIAL,
    FAILED;

    public static final StreamCodec<PathSearchOutcome> CODEC = PathDebugCodecs.enumCodec(values());
}
