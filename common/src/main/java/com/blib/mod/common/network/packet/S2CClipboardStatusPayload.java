package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Server → client: state of the server-side clipboard. Pushed after each Cut/Copy so the client knows whether to enable
 * the Paste button and what size the pasted volume will be. Carries no block NBT — only the metadata the UI needs.
 * {@code hasContents=false} signals an empty / cleared clipboard (e.g. server stop or explicit clear).
 */
public record S2CClipboardStatusPayload(
    boolean hasContents,
    int sizeX,
    int sizeY,
    int sizeZ,
    long filledAtMs
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("clipboard_status");

    public static final Type<S2CClipboardStatusPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CClipboardStatusPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.BOOLEAN,
        S2CClipboardStatusPayload::hasContents,
        StreamCodecs.INT,
        S2CClipboardStatusPayload::sizeX,
        StreamCodecs.INT,
        S2CClipboardStatusPayload::sizeY,
        StreamCodecs.INT,
        S2CClipboardStatusPayload::sizeZ,
        StreamCodecs.LONG,
        S2CClipboardStatusPayload::filledAtMs,
        S2CClipboardStatusPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
