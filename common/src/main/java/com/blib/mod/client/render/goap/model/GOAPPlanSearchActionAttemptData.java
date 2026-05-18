package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPPlanSearchActionAttemptData(
    String actionName,
    List<String> newUnsatisfiedConditions,
    float actionCost,
    float heuristicCost
) {

    public static final StreamCodec<GOAPPlanSearchActionAttemptData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPPlanSearchActionAttemptData::actionName,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPPlanSearchActionAttemptData::newUnsatisfiedConditions,
        StreamCodecs.FLOAT,
        GOAPPlanSearchActionAttemptData::actionCost,
        StreamCodecs.FLOAT,
        GOAPPlanSearchActionAttemptData::heuristicCost,
        GOAPPlanSearchActionAttemptData::new
    );
}
