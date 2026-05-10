package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: delete one capture file from {@code projectName}'s {@code captures/} folder. Op-gated; gated by a
 * {@code ConfirmDialog} client-side. Server replies with {@link S2CProjectOpResultPayload} (op {@code CAPTURE}) and a
 * refreshed {@link S2CCaptureListPayload}.
 */
public record C2SDeleteCapturePayload(
    String projectName,
    String captureName
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_capture");

    public static final Type<C2SDeleteCapturePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteCapturePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SDeleteCapturePayload::projectName,
        StreamCodecs.STRING_UTF8,
        C2SDeleteCapturePayload::captureName,
        C2SDeleteCapturePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
