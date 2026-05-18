package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;

/**
 * High-level routing mode used by the block-level path search.
 */
public enum PathSearchMode {
    DIRECT,
    CORRIDOR,
    DIRECT_FALLBACK;

    public static final StreamCodec<PathSearchMode> CODEC = PathDebugCodecs.enumCodec(values());
}
