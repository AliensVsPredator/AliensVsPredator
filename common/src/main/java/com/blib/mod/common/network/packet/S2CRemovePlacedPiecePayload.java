package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: the placed piece identified by {@code id} no longer exists (deleted or undone). Clients drop it from
 * their hover mirror; if it was selected, the selection prunes itself on next read because {@code isValid()} returns
 * false. {@code dimension} is included so a client in a different dimension can quietly ignore the message.
 */
public record S2CRemovePlacedPiecePayload(UUID id, ResourceLocation dimension) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_placed_piece");

    public static final Type<S2CRemovePlacedPiecePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CRemovePlacedPiecePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.UUID,
        S2CRemovePlacedPiecePayload::id,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CRemovePlacedPiecePayload::dimension,
        S2CRemovePlacedPiecePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
