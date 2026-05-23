package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPPlanSearchStepData(
    String condition,
    List<String> candidateActions,
    List<GOAPPlanSearchActionAttemptData> attempts
) {

    public static final StreamCodec<GOAPPlanSearchStepData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPPlanSearchStepData::condition,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPPlanSearchStepData::candidateActions,
        GOAPPlanSearchActionAttemptData.CODEC.asList(),
        GOAPPlanSearchStepData::attempts,
        GOAPPlanSearchStepData::new
    );
}
