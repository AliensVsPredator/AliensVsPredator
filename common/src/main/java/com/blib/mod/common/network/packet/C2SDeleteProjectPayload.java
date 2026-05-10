package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: delete the named BLib project. Server unselects the pack from the world's pack repo, triggers a
 * {@code reloadResources} so vanilla doesn't hold a dangling reference, then deletes the folder recursively. Refuses to
 * delete a folder lacking the {@code blib_project.json} marker (defensive — never touches non-BLib packs). Op-gated.
 */
public record C2SDeleteProjectPayload(String name) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_project");

    public static final Type<C2SDeleteProjectPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteProjectPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SDeleteProjectPayload::name,
        C2SDeleteProjectPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
