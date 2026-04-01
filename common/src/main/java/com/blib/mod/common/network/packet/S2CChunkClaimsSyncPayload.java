package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

public record S2CChunkClaimsSyncPayload(
    int chunkX,
    int chunkZ,
    List<ResourceLocation> factionIds
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("chunk_claims_sync");

    public static final Type<S2CChunkClaimsSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CChunkClaimsSyncPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::chunkX,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::chunkZ,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CChunkClaimsSyncPayload::factionIds,
        S2CChunkClaimsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
