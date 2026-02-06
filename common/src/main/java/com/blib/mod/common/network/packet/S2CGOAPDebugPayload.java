package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.mod.BLib;

public record S2CGOAPDebugPayload(
    List<GOAPAgentDebugData> agents,
    int selectedIndex
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("goap_debug");

    public static final Type<S2CGOAPDebugPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CGOAPDebugPayload> CODEC = RecordStreamCodec.of(
        GOAPAgentDebugData.CODEC.asList(),
        S2CGOAPDebugPayload::agents,
        StreamCodecs.INT,
        S2CGOAPDebugPayload::selectedIndex,
        S2CGOAPDebugPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record GOAPAgentDebugData(
        int entityId,
        String entityName,
        long agentTick,
        boolean hasPlan,
        List<GOAPPlanDebugData> plans
    ) {

        public static final StreamCodec<GOAPAgentDebugData> CODEC = RecordStreamCodec.of(
            StreamCodecs.INT,
            GOAPAgentDebugData::entityId,
            StreamCodecs.STRING_UTF8,
            GOAPAgentDebugData::entityName,
            StreamCodecs.LONG,
            GOAPAgentDebugData::agentTick,
            StreamCodecs.BOOLEAN,
            GOAPAgentDebugData::hasPlan,
            GOAPPlanDebugData.CODEC.asList(),
            GOAPAgentDebugData::plans,
            GOAPAgentDebugData::new
        );
    }

    public record GOAPPlanDebugData(
        String goalName,
        String planState,
        float initialCost,
        int currentActionIndex,
        List<String> actionNames
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
            GOAPPlanDebugData::new
        );
    }
}
