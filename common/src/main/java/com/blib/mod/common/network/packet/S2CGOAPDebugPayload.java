package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.mod.BLib;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;

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

}
