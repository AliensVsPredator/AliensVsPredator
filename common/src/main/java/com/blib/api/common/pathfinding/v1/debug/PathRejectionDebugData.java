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
    List<PathDebugBlockPos> samples,
    List<PathRejectionEdgeDebugData> edgeBreakdown
) {

    public static final StreamCodec<PathRejectionDebugData> CODEC = RecordStreamCodec.of(
        PathRejectionReason.CODEC,
        PathRejectionDebugData::reason,
        StreamCodecs.INT,
        PathRejectionDebugData::count,
        PathDebugBlockPos.CODEC.asList(),
        PathRejectionDebugData::samples,
        PathRejectionEdgeDebugData.CODEC.asList(),
        PathRejectionDebugData::edgeBreakdown,
        PathRejectionDebugData::new
    );

    public PathRejectionDebugData(
        PathRejectionReason reason,
        int count,
        List<PathDebugBlockPos> samples
    ) {
        this(reason, count, samples, List.of());
    }

    public PathRejectionDebugData {
        samples = List.copyOf(samples);
        edgeBreakdown = List.copyOf(edgeBreakdown);
    }
}
