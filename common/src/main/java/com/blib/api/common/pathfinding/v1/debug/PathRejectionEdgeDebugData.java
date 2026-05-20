package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

/**
 * Aggregated rejection count for a single movement edge type.
 */
public record PathRejectionEdgeDebugData(
    PathEdgeDebugType edgeType,
    int count,
    List<PathDebugBlockPos> samples
) {

    public static final StreamCodec<PathRejectionEdgeDebugData> CODEC = RecordStreamCodec.of(
        PathEdgeDebugType.CODEC,
        PathRejectionEdgeDebugData::edgeType,
        StreamCodecs.INT,
        PathRejectionEdgeDebugData::count,
        PathDebugBlockPos.CODEC.asList(),
        PathRejectionEdgeDebugData::samples,
        PathRejectionEdgeDebugData::new
    );

    public PathRejectionEdgeDebugData {
        samples = List.copyOf(samples);
    }
}
