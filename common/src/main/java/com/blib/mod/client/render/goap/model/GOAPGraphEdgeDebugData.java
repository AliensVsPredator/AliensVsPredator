package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

public record GOAPGraphEdgeDebugData(
    String fromId,
    String toId,
    String kind,
    String label,
    boolean active
) {

    public static final StreamCodec<GOAPGraphEdgeDebugData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPGraphEdgeDebugData::fromId,
        StreamCodecs.STRING_UTF8,
        GOAPGraphEdgeDebugData::toId,
        StreamCodecs.STRING_UTF8,
        GOAPGraphEdgeDebugData::kind,
        StreamCodecs.STRING_UTF8,
        GOAPGraphEdgeDebugData::label,
        StreamCodecs.BOOLEAN,
        GOAPGraphEdgeDebugData::active,
        GOAPGraphEdgeDebugData::new
    );
}
