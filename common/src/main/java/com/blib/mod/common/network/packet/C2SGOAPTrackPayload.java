package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.mod.BLib;

/**
 * Client → server: ask the server to mirror the selected entity ids into the GOAP debug tracker. An empty list clears
 * tracking for this player.
 */
public record C2SGOAPTrackPayload(List<Integer> entityIds) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("goap_track");

    public static final Type<C2SGOAPTrackPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SGOAPTrackPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT.asList(),
        C2SGOAPTrackPayload::entityIds,
        C2SGOAPTrackPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
