package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPGraphDebugData(
    List<GOAPGraphNodeDebugData> nodes,
    List<GOAPGraphEdgeDebugData> edges,
    List<String> diagnostics,
    GOAPDiagnosticsDebugData agentDiagnostics
) {

    public static final GOAPGraphDebugData EMPTY = new GOAPGraphDebugData(
        List.of(),
        List.of(),
        List.of(),
        GOAPDiagnosticsDebugData.EMPTY
    );

    public static final StreamCodec<GOAPGraphDebugData> CODEC = RecordStreamCodec.of(
        GOAPGraphNodeDebugData.CODEC.asList(),
        GOAPGraphDebugData::nodes,
        GOAPGraphEdgeDebugData.CODEC.asList(),
        GOAPGraphDebugData::edges,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPGraphDebugData::diagnostics,
        GOAPDiagnosticsDebugData.CODEC,
        GOAPGraphDebugData::agentDiagnostics,
        GOAPGraphDebugData::new
    );
}
