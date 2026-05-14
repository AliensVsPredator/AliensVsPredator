package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

public record C2SRequestTerritoryClaimsPayload(
    ResourceLocation clientDimensionHint,
    int minChunkX,
    int minChunkZ,
    int maxChunkX,
    int maxChunkZ
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_territory_claims");

    public static final Type<C2SRequestTerritoryClaimsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestTerritoryClaimsPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestTerritoryClaimsPayload::clientDimensionHint,
        StreamCodecs.INT,
        C2SRequestTerritoryClaimsPayload::minChunkX,
        StreamCodecs.INT,
        C2SRequestTerritoryClaimsPayload::minChunkZ,
        StreamCodecs.INT,
        C2SRequestTerritoryClaimsPayload::maxChunkX,
        StreamCodecs.INT,
        C2SRequestTerritoryClaimsPayload::maxChunkZ,
        C2SRequestTerritoryClaimsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
