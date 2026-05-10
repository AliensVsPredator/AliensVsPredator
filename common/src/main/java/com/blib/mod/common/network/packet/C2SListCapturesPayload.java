package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: ask for the list of capture names in {@code projectName}'s {@code captures/} folder. Used by the
 * Capture Panel on open and after every successful capture / delete. Server replies with {@link S2CCaptureListPayload}.
 * No op-gating; capture names are not sensitive.
 */
public record C2SListCapturesPayload(String projectName) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("list_captures");

    public static final Type<C2SListCapturesPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SListCapturesPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SListCapturesPayload::projectName,
        C2SListCapturesPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
