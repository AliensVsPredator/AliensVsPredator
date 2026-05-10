package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: reload the named project's datapack so the live registry catches up to the on-disk JSON. Server
 * ensures the project pack is in {@code packRepo.getSelectedIds()}, then runs {@code packRepo.reload()} +
 * {@code server.reloadResources(...)}. The Pool Editor's "Save & Reload" button and the FILE menu's "Reload Project"
 * both fire this. Op-gated.
 */
public record C2SReloadProjectPayload(String projectName) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("reload_project");

    public static final Type<C2SReloadProjectPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SReloadProjectPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SReloadProjectPayload::projectName,
        C2SReloadProjectPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
