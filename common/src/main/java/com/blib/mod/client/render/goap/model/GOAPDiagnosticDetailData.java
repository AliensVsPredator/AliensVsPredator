package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

public record GOAPDiagnosticDetailData(
    String key,
    String value
) {

    public static final StreamCodec<GOAPDiagnosticDetailData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPDiagnosticDetailData::key,
        StreamCodecs.STRING_UTF8,
        GOAPDiagnosticDetailData::value,
        GOAPDiagnosticDetailData::new
    );
}
