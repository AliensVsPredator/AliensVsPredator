package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import com.blib.mod.BLib;

public record S2CGOAPDebugPayload(
    List<GOAPAgentDebugData> agents,
    int selectedIndex,
    boolean worldStateAutoPage,
    int worldStatePage
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("goap_debug");

    public static final Type<S2CGOAPDebugPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CGOAPDebugPayload> CODEC = RecordStreamCodec.of(
        GOAPAgentDebugData.CODEC.asList(),
        S2CGOAPDebugPayload::agents,
        StreamCodecs.INT,
        S2CGOAPDebugPayload::selectedIndex,
        StreamCodecs.BOOLEAN,
        S2CGOAPDebugPayload::worldStateAutoPage,
        StreamCodecs.INT,
        S2CGOAPDebugPayload::worldStatePage,
        S2CGOAPDebugPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

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
        Map<String, String> worldState
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
            GOAPAgentDebugData::new
        );
    }

    public record GOAPPlanDebugData(
        String goalName,
        String planState,
        float initialCost,
        int currentActionIndex,
        List<String> actionNames,
        Map<String, String> actionBlackboard,
        Map<String, String> planBlackboard
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
            GOAPPlanDebugData::new
        );
    }
}
