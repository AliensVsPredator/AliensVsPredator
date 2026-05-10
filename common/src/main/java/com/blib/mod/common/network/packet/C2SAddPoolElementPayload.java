package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: append a new {@link net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement} pointing
 * at {@code templateId} to the project's pool JSON, with the supplied weight + projection. Used by the Pool Editor's
 * footer "Add piece" picker.
 * <p>
 * Like the other pool-edit packets, the change goes to the active project's datapack file — the live registry is
 * untouched until Reload Project. Server echoes an {@link S2CPoolDraftPayload} with the updated element list. Op-gated.
 */
public record C2SAddPoolElementPayload(
    String projectName,
    ResourceLocation poolId,
    ResourceLocation templateId,
    int weight,
    int projectionOrdinal
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("add_pool_element");

    public static final Type<C2SAddPoolElementPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SAddPoolElementPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SAddPoolElementPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddPoolElementPayload::poolId,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddPoolElementPayload::templateId,
        StreamCodecs.INT,
        C2SAddPoolElementPayload::weight,
        StreamCodecs.INT,
        C2SAddPoolElementPayload::projectionOrdinal,
        C2SAddPoolElementPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
