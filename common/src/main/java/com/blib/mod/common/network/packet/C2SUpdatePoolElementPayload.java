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
 * Client → server: edit one element of a structure template pool — set its weight and projection. Triggered from the
 * engine's Pool Editor panel when the user commits a weight input or clicks a projection segment. Server validates
 * op permissions, looks up the pool from the {@code TEMPLATE_POOL} registry, locates the element by {@code rawIndex},
 * and rewrites the {@code rawTemplates} entry + rebuilds the expanded {@code templates} list so subsequent structure
 * generations use the new values.
 * <p>
 * Edits are <em>live</em> mutations of the registry's pool object — they take effect immediately for new generations
 * but are not persisted to disk. Closing the world reverts to the on-disk JSON. Persistence is a separate Phase 3
 * "Save to Datapack" feature.
 * <p>
 * {@code rawIndex} addresses the top-level entry in the pool's {@code rawTemplates} list; nested children of a
 * {@code ListPoolElement} are not directly editable (their rawIndex is {@code -1} client-side, and the server
 * rejects out-of-range or non-{@code SinglePoolElement} indices defensively).
 */
public record C2SUpdatePoolElementPayload(
    ResourceLocation poolId,
    int rawIndex,
    int newWeight,
    int newProjectionOrdinal
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("update_pool_element");

    public static final Type<C2SUpdatePoolElementPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUpdatePoolElementPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SUpdatePoolElementPayload::poolId,
        StreamCodecs.INT,
        C2SUpdatePoolElementPayload::rawIndex,
        StreamCodecs.INT,
        C2SUpdatePoolElementPayload::newWeight,
        StreamCodecs.INT,
        C2SUpdatePoolElementPayload::newProjectionOrdinal,
        C2SUpdatePoolElementPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
