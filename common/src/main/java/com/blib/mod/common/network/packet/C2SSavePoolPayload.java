package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: persist the current in-memory state of the named pool to disk. Triggered from the Pool Editor's
 * "Save" header button. Server encodes the pool via vanilla's codec and writes it under an auto-managed
 * {@code blib_engine} datapack at {@code <world>/datapacks/blib_engine/}, then triggers a resource reload so the
 * on-disk state replaces the in-memory edits (which would otherwise be lost on world close).
 * <p>
 * Op-gated server-side. The auto-managed pack is created on first save and added to the world's selected pack ids
 * so subsequent reloads pick it up automatically.
 */
public record C2SSavePoolPayload(
    ResourceLocation poolId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("save_pool");

    public static final Type<C2SSavePoolPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSavePoolPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSavePoolPayload::poolId,
        C2SSavePoolPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
