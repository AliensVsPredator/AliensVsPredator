package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.jigsaw.PlacedPiece;

/**
 * Server → client: a new placed jigsaw piece was just registered. Engine-mode clients add it to their
 * {@code ClientPlacedPieceRegistry} so the next hover frame can pick it up as a selectable thing.
 */
public record S2CAddPlacedPiecePayload(PlacedPiece piece) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("add_placed_piece");

    public static final Type<S2CAddPlacedPiecePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CAddPlacedPiecePayload> CODEC = RecordStreamCodec.of(
        PlacedPiece.CODEC,
        S2CAddPlacedPiecePayload::piece,
        S2CAddPlacedPiecePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
