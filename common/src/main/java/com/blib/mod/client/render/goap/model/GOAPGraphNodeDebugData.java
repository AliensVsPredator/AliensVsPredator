package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPGraphNodeDebugData(
    String id,
    String kind,
    String label,
    String status,
    String value,
    int column,
    List<String> details
) {

    public static final StreamCodec<GOAPGraphNodeDebugData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPGraphNodeDebugData::id,
        StreamCodecs.STRING_UTF8,
        GOAPGraphNodeDebugData::kind,
        StreamCodecs.STRING_UTF8,
        GOAPGraphNodeDebugData::label,
        StreamCodecs.STRING_UTF8,
        GOAPGraphNodeDebugData::status,
        StreamCodecs.STRING_UTF8,
        GOAPGraphNodeDebugData::value,
        StreamCodecs.INT,
        GOAPGraphNodeDebugData::column,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPGraphNodeDebugData::details,
        GOAPGraphNodeDebugData::new
    );
}
