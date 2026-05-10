package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.mod.BLib;

/**
 * Server → client: list of capture names in {@code projectName}'s {@code captures/} folder. Sent in response to
 * {@link C2SListCapturesPayload} and proactively after a successful capture / delete so the Capture Panel doesn't have
 * to re-request.
 */
public record S2CCaptureListPayload(
    String projectName,
    List<String> captureNames
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("capture_list");

    public static final Type<S2CCaptureListPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CCaptureListPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CCaptureListPayload::projectName,
        StreamCodecs.STRING_UTF8.asList(),
        S2CCaptureListPayload::captureNames,
        S2CCaptureListPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
