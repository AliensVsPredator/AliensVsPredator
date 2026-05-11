package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: translate the placed piece identified by {@code id} so its AABB min lands at {@code newMin}.
 * Identity-preserving move — the piece's blocks are picked up and placed at the new location, the registry record's
 * AABB and anchor update accordingly, and clients receive an updated piece broadcast.
 */
public record C2SMovePlacedPiecePayload(UUID id, BlockPos newMin) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("move_placed_piece");

    public static final Type<C2SMovePlacedPiecePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SMovePlacedPiecePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.UUID,
        C2SMovePlacedPiecePayload::id,
        BLibCodecs.Stream.BLOCK_POS,
        C2SMovePlacedPiecePayload::newMin,
        C2SMovePlacedPiecePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
