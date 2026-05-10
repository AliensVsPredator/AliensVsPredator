package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: tell the server the player is about to open a project workspace. Server validates that the project
 * still exists (the picker's list could be stale if another action deleted the project between list and open) and
 * replies with {@link S2CProjectOpResultPayload} (op {@code OPEN}). The client picker waits for a SUCCESS reply before
 * transitioning to the workspace screen; on FAILURE it shows an error toast and stays on the picker.
 */
public record C2SOpenProjectPayload(String name) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("open_project");

    public static final Type<C2SOpenProjectPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SOpenProjectPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SOpenProjectPayload::name,
        C2SOpenProjectPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
