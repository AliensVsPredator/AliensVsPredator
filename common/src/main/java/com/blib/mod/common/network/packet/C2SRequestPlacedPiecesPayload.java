package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: send me every {@link com.blib.mod.common.gameplay.jigsaw.PlacedPiece} you have for my current
 * dimension. Sent on engine-mode entry so the client can populate its hover / selection mirror without waiting for
 * incremental add packets. The {@code clientDimensionHint} is informational only — the server picks pieces based on the
 * requesting player's actual server-side level.
 */
public record C2SRequestPlacedPiecesPayload(ResourceLocation clientDimensionHint) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_placed_pieces");

    public static final Type<C2SRequestPlacedPiecesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestPlacedPiecesPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestPlacedPiecesPayload::clientDimensionHint,
        C2SRequestPlacedPiecesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
