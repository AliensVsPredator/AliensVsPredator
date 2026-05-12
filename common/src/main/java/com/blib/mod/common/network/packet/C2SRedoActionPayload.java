package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: pop the most recent action off the server's {@code ActionHistory} redo stack and re-apply it.
 * Triggered by Ctrl+Y and by the Edit → Redo menu item. Empty payload — same context-driven resolution as
 * {@link C2SUndoActionPayload}.
 */
public record C2SRedoActionPayload() implements CustomPacketPayload {

    public static final C2SRedoActionPayload INSTANCE = new C2SRedoActionPayload();

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("redo_action");

    public static final Type<C2SRedoActionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRedoActionPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
