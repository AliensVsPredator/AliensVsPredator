package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * Collision or terrain block that caused a movement candidate to fail.
 */
public record PathBlockDebugEntry(
    PathDebugBlockPos node,
    PathDebugBlockPos block,
    PathEdgeDebugType type,
    int rejectionReason
) {

    public static final StreamCodec<PathBlockDebugEntry> CODEC = RecordStreamCodec.of(
        PathDebugBlockPos.CODEC,
        PathBlockDebugEntry::node,
        PathDebugBlockPos.CODEC,
        PathBlockDebugEntry::block,
        PathEdgeDebugType.CODEC,
        PathBlockDebugEntry::type,
        StreamCodecs.INT,
        PathBlockDebugEntry::rejectionReason,
        PathBlockDebugEntry::new
    );
}
