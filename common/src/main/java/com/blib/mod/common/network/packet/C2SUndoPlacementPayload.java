package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: pop the most recent jigsaw placement off the server's {@code PlacementHistory} stack and restore the
 * world to its pre-placement state. Triggered from a viewport RMB click while a piece is selected.
 * <p>
 * Empty payload — the server reads everything it needs (current player, current dimension) from the connection context.
 * {@link StreamCodec#unit} encodes the singleton without any wire bytes.
 */
public record C2SUndoPlacementPayload() implements CustomPacketPayload {

    public static final C2SUndoPlacementPayload INSTANCE = new C2SUndoPlacementPayload();

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("undo_placement");

    public static final Type<C2SUndoPlacementPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUndoPlacementPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
