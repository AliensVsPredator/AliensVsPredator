package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPDiagnosticsDebugData(
    long tick,
    boolean enabled,
    List<GOAPPlanSearchResultData> planSearches,
    List<GOAPDiagnosticEventData> events
) {

    public static final GOAPDiagnosticsDebugData EMPTY = new GOAPDiagnosticsDebugData(0L, false, List.of(), List.of());

    public static final StreamCodec<GOAPDiagnosticsDebugData> CODEC = RecordStreamCodec.of(
        StreamCodecs.LONG,
        GOAPDiagnosticsDebugData::tick,
        StreamCodecs.BOOLEAN,
        GOAPDiagnosticsDebugData::enabled,
        GOAPPlanSearchResultData.CODEC.asList(),
        GOAPDiagnosticsDebugData::planSearches,
        GOAPDiagnosticEventData.CODEC.asList(),
        GOAPDiagnosticsDebugData::events,
        GOAPDiagnosticsDebugData::new
    );
}
