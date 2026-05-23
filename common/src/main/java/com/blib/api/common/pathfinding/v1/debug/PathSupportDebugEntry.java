package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * One support-footprint cell checked for a movement candidate.
 */
public record PathSupportDebugEntry(
    PathDebugBlockPos node,
    PathDebugBlockPos support,
    boolean supported,
    PathEdgeDebugType type
) {

    public static final StreamCodec<PathSupportDebugEntry> CODEC = RecordStreamCodec.of(
        PathDebugBlockPos.CODEC,
        PathSupportDebugEntry::node,
        PathDebugBlockPos.CODEC,
        PathSupportDebugEntry::support,
        StreamCodecs.BOOLEAN,
        PathSupportDebugEntry::supported,
        PathEdgeDebugType.CODEC,
        PathSupportDebugEntry::type,
        PathSupportDebugEntry::new
    );
}
