package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.jigsaw.PlacedPiece;

/**
 * Server → client: full snapshot of every {@link PlacedPiece} for the given dimension. Sent in response to a
 * {@link C2SRequestPlacedPiecesPayload} on engine-mode entry. Replaces (not merges) the client's mirror — any pieces
 * the client had cached but the server doesn't will be dropped, which is the desired behavior for re-syncs.
 */
public record S2CSyncPlacedPiecesPayload(
    ResourceLocation dimension,
    List<PlacedPiece> pieces
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("sync_placed_pieces");

    public static final Type<S2CSyncPlacedPiecesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CSyncPlacedPiecesPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CSyncPlacedPiecesPayload::dimension,
        PlacedPiece.CODEC.asList(),
        S2CSyncPlacedPiecesPayload::pieces,
        S2CSyncPlacedPiecesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
