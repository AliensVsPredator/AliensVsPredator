package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: unclaim a chunk from a faction. The server uses the caller's current {@code ServerLevel} as the
 * target dimension. Op-gated on the receiving side; on success the existing chunk-claim sync push fires.
 */
public record C2SRemoveChunkClaimPayload(
    ResourceLocation factionId,
    int chunkX,
    int chunkZ
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_chunk_claim");

    public static final Type<C2SRemoveChunkClaimPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemoveChunkClaimPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveChunkClaimPayload::factionId,
        StreamCodecs.INT,
        C2SRemoveChunkClaimPayload::chunkX,
        StreamCodecs.INT,
        C2SRemoveChunkClaimPayload::chunkZ,
        C2SRemoveChunkClaimPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
