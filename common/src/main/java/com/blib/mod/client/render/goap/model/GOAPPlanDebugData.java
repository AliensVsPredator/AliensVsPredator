package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;
import java.util.Map;

public record GOAPPlanDebugData(
    String goalName,
    String planState,
    float initialCost,
    int currentActionIndex,
    List<String> actionNames,
    Map<String, String> actionBlackboard,
    Map<String, String> planBlackboard,
    int planTick,
    int actionTick,
    float remainingCost,
    String currentActionName
) {

    public static final StreamCodec<GOAPPlanDebugData> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        GOAPPlanDebugData::goalName,
        StreamCodecs.STRING_UTF8,
        GOAPPlanDebugData::planState,
        StreamCodecs.FLOAT,
        GOAPPlanDebugData::initialCost,
        StreamCodecs.INT,
        GOAPPlanDebugData::currentActionIndex,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPPlanDebugData::actionNames,
        StreamCodec.unboundedMap(StreamCodecs.STRING_UTF8, StreamCodecs.STRING_UTF8),
        GOAPPlanDebugData::actionBlackboard,
        StreamCodec.unboundedMap(StreamCodecs.STRING_UTF8, StreamCodecs.STRING_UTF8),
        GOAPPlanDebugData::planBlackboard,
        StreamCodecs.INT,
        GOAPPlanDebugData::planTick,
        StreamCodecs.INT,
        GOAPPlanDebugData::actionTick,
        StreamCodecs.FLOAT,
        GOAPPlanDebugData::remainingCost,
        StreamCodecs.STRING_UTF8,
        GOAPPlanDebugData::currentActionName,
        GOAPPlanDebugData::new
    );
}
