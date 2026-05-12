package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: pop the most recent {@code EditorAction} (matching the player's current dimension) off the server's
 * {@code ActionHistory} undo stack and revert it. Triggered by Ctrl+Z and by the Edit → Undo menu item.
 * <p>
 * Empty payload — the server reads the player + their current dimension from the connection context.
 */
public record C2SUndoActionPayload() implements CustomPacketPayload {

    public static final C2SUndoActionPayload INSTANCE = new C2SUndoActionPayload();

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("undo_action");

    public static final Type<C2SUndoActionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUndoActionPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
