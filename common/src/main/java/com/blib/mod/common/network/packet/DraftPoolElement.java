package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Wire form of one element row in a project's pool override. Mirrors the shape of
 * {@code JigsawPoolLibrary.PoolElementInfo} but with the projection encoded as an int ordinal so it round-trips through
 * the stream codec without needing a custom enum codec. Used by {@link S2CPoolDraftPayload} to push the
 * server-authoritative project state for a pool back to the client editor — the client uses this in place of reading
 * the live registry, since edits no longer mutate the registry (they only mutate disk).
 * <p>
 * {@code rawIndex} addresses the entry's position in the project's pool JSON {@code elements} array. Nested children of
 * a {@code list_pool_element} are flattened and emitted with {@code rawIndex == -1} so the editor can display them but
 * the per-row remove/edit packets don't try to address them directly.
 */
public record DraftPoolElement(
    int rawIndex,
    ResourceLocation templateId,
    int weight,
    int projectionOrdinal
) {

    public static final StreamCodec<DraftPoolElement> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        DraftPoolElement::rawIndex,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        DraftPoolElement::templateId,
        StreamCodecs.INT,
        DraftPoolElement::weight,
        StreamCodecs.INT,
        DraftPoolElement::projectionOrdinal,
        DraftPoolElement::new
    );
}
