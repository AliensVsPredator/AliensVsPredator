package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPDiagnosticEventData(
    long tick,
    String kind,
    long planId,
    String goalName,
    List<String> planActions,
    float initialCost,
    int actionIndex,
    String actionName,
    List<GOAPDiagnosticDetailData> details
) {

    public static final StreamCodec<GOAPDiagnosticEventData> CODEC = RecordStreamCodec.of(
        StreamCodecs.LONG,
        GOAPDiagnosticEventData::tick,
        StreamCodecs.STRING_UTF8,
        GOAPDiagnosticEventData::kind,
        StreamCodecs.LONG,
        GOAPDiagnosticEventData::planId,
        StreamCodecs.STRING_UTF8,
        GOAPDiagnosticEventData::goalName,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPDiagnosticEventData::planActions,
        StreamCodecs.FLOAT,
        GOAPDiagnosticEventData::initialCost,
        StreamCodecs.INT,
        GOAPDiagnosticEventData::actionIndex,
        StreamCodecs.STRING_UTF8,
        GOAPDiagnosticEventData::actionName,
        GOAPDiagnosticDetailData.CODEC.asList(),
        GOAPDiagnosticEventData::details,
        GOAPDiagnosticEventData::new
    );
}
