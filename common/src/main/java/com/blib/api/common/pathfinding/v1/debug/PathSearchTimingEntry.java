package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * Aggregated timing for one search phase.
 */
public record PathSearchTimingEntry(
    PathSearchTimingPhase phase,
    long totalNanos,
    int count,
    long maxNanos
) {

    public static final StreamCodec<PathSearchTimingEntry> CODEC = RecordStreamCodec.of(
        PathSearchTimingPhase.CODEC,
        PathSearchTimingEntry::phase,
        StreamCodecs.LONG,
        PathSearchTimingEntry::totalNanos,
        StreamCodecs.INT,
        PathSearchTimingEntry::count,
        StreamCodecs.LONG,
        PathSearchTimingEntry::maxNanos,
        PathSearchTimingEntry::new
    );
}
