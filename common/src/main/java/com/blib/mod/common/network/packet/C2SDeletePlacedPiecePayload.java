package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.mod.BLib;

/**
 * Client → server: delete the placed piece identified by {@code id}. The server clears every block in the piece's AABB
 * to air, removes the record from the {@code PlacedPieceStore}, pushes a
 * {@link com.blib.mod.common.gameplay.jigsaw.PlacementHistory} snapshot so the deletion is itself undoable, and
 * broadcasts an {@code S2CRemovePlacedPiecePayload}.
 */
public record C2SDeletePlacedPiecePayload(UUID id) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_placed_piece");

    public static final Type<C2SDeletePlacedPiecePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeletePlacedPiecePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.UUID,
        C2SDeletePlacedPiecePayload::id,
        C2SDeletePlacedPiecePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
