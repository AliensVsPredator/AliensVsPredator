package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

public record S2CFactionMetadataSyncPayload(
    ResourceLocation factionId,
    String name,
    int color
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("faction_metadata_sync");

    public static final Type<S2CFactionMetadataSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CFactionMetadataSyncPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CFactionMetadataSyncPayload::factionId,
        StreamCodecs.STRING_UTF8,
        S2CFactionMetadataSyncPayload::name,
        StreamCodecs.INT,
        S2CFactionMetadataSyncPayload::color,
        S2CFactionMetadataSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
