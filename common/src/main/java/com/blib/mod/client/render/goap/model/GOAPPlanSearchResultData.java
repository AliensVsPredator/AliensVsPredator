package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;

public record GOAPPlanSearchResultData(
    long tick,
    String goalName,
    boolean goalPreconditionsSatisfied,
    boolean desiredConditionsSatisfied,
    List<String> rootUnsatisfiedConditions,
    String status,
    List<String> selectedActions,
    float cost,
    boolean truncated,
    List<GOAPPlanSearchStepData> steps
) {

    public static final StreamCodec<GOAPPlanSearchResultData> CODEC = RecordStreamCodec.of(
        StreamCodecs.LONG,
        GOAPPlanSearchResultData::tick,
        StreamCodecs.STRING_UTF8,
        GOAPPlanSearchResultData::goalName,
        StreamCodecs.BOOLEAN,
        GOAPPlanSearchResultData::goalPreconditionsSatisfied,
        StreamCodecs.BOOLEAN,
        GOAPPlanSearchResultData::desiredConditionsSatisfied,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPPlanSearchResultData::rootUnsatisfiedConditions,
        StreamCodecs.STRING_UTF8,
        GOAPPlanSearchResultData::status,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPPlanSearchResultData::selectedActions,
        StreamCodecs.FLOAT,
        GOAPPlanSearchResultData::cost,
        StreamCodecs.BOOLEAN,
        GOAPPlanSearchResultData::truncated,
        GOAPPlanSearchStepData.CODEC.asList(),
        GOAPPlanSearchResultData::steps,
        GOAPPlanSearchResultData::new
    );
}
