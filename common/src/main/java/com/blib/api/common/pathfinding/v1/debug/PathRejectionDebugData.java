package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

/**
 * Aggregated rejection reason plus a small set of sample candidate positions.
 */
public record PathRejectionDebugData(
    PathRejectionReason reason,
    int count,
    List<PathDebugBlockPos> samples
) {

    public static final StreamCodec<PathRejectionDebugData> CODEC = RecordStreamCodec.of(
        PathRejectionReason.CODEC,
        PathRejectionDebugData::reason,
        StreamCodecs.INT,
        PathRejectionDebugData::count,
        PathDebugBlockPos.CODEC.asList(),
        PathRejectionDebugData::samples,
        PathRejectionDebugData::new
    );

    public PathRejectionDebugData {
        samples = List.copyOf(samples);
    }
}
