package com.blib.mod.client.render.goap.model;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.List;
import java.util.Map;

public record GOAPAgentDebugData(
    int entityId,
    String entityName,
    String entityUuid,
    int posX,
    int posY,
    int posZ,
    long agentTick,
    boolean hasPlan,
    List<GOAPPlanDebugData> plans,
    int graphGoalCount,
    int graphActionCount,
    List<String> graphSensorKeys,
    Map<String, String> worldState,
    Map<String, String> agentBlackboard,
    Map<String, String> graphBlackboard,
    GOAPGraphDebugData graph
) {

    public static final StreamCodec<GOAPAgentDebugData> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        GOAPAgentDebugData::entityId,
        StreamCodecs.STRING_UTF8,
        GOAPAgentDebugData::entityName,
        StreamCodecs.STRING_UTF8,
        GOAPAgentDebugData::entityUuid,
        StreamCodecs.INT,
        GOAPAgentDebugData::posX,
        StreamCodecs.INT,
        GOAPAgentDebugData::posY,
        StreamCodecs.INT,
        GOAPAgentDebugData::posZ,
        StreamCodecs.LONG,
        GOAPAgentDebugData::agentTick,
        StreamCodecs.BOOLEAN,
        GOAPAgentDebugData::hasPlan,
        GOAPPlanDebugData.CODEC.asList(),
        GOAPAgentDebugData::plans,
        StreamCodecs.INT,
        GOAPAgentDebugData::graphGoalCount,
        StreamCodecs.INT,
        GOAPAgentDebugData::graphActionCount,
        StreamCodecs.STRING_UTF8.asList(),
        GOAPAgentDebugData::graphSensorKeys,
        StreamCodec.unboundedMap(StreamCodecs.STRING_UTF8, StreamCodecs.STRING_UTF8),
        GOAPAgentDebugData::worldState,
        StreamCodec.unboundedMap(StreamCodecs.STRING_UTF8, StreamCodecs.STRING_UTF8),
        GOAPAgentDebugData::agentBlackboard,
        StreamCodec.unboundedMap(StreamCodecs.STRING_UTF8, StreamCodecs.STRING_UTF8),
        GOAPAgentDebugData::graphBlackboard,
        GOAPGraphDebugData.CODEC,
        GOAPAgentDebugData::graph,
        GOAPAgentDebugData::new
    );
}
