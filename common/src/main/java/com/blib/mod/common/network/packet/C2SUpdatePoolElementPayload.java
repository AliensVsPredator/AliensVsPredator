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
 * engine's Pool Editor panel when the user commits a weight input or clicks a projection segment.
 * <p>
 * The server applies the edit to the active project's pool JSON on disk via {@code EngineProjectIO.writePoolJson} — it
 * does <em>not</em> mutate the live {@code Registries.TEMPLATE_POOL} object. Live structure generation only reflects
 * the edit after the user runs Reload Project. After the disk write completes, the server echoes an
 * {@link S2CPoolDraftPayload} with the new element list so the client editor can re-render without round-tripping back
 * through the registry.
 * <p>
 * {@code rawIndex} addresses the entry's position in the project's pool JSON {@code elements} array. Nested children of
 * a {@code list_pool_element} aren't directly editable (their {@code rawIndex} is {@code -1} client-side and the server
 * rejects out-of-range indices defensively).
 */
public record C2SUpdatePoolElementPayload(
    String projectName,
    ResourceLocation poolId,
    int rawIndex,
    int newWeight,
    int newProjectionOrdinal
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("update_pool_element");

    public static final Type<C2SUpdatePoolElementPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUpdatePoolElementPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SUpdatePoolElementPayload::projectName,
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
