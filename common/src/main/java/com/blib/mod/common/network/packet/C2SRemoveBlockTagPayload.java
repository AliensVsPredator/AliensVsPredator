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
 * Client → server: remove a registry-element entry from a tag's project override JSON by its {@code entryId}, looking
 * up the on-disk {@code rawIndex} server-side. Sister packet to {@link C2SRemoveTagEntryPayload} — index-by-id rather
 * than index-by-position. Used by the block inspector's Tags section, where the user thinks in "remove this block from
 * that tag" semantics and the inspector doesn't carry the tag's source-array layout.
 * <p>
 * Server scans the tag's draft entries, finds the first direct (non-tag-ref) entry whose id equals {@code entryId}, and
 * removes it. If no matching direct entry exists (block is in the tag only via a tag-ref or via upstream packs), the
 * server silently no-ops — the user might be looking at a stale view, or the block is included transitively and the
 * inspector can't represent the negation. Echoes a fresh {@link S2CTagDraftPayload} either way so the source view
 * doesn't fall out of sync.
 */
public record C2SRemoveBlockTagPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    ResourceLocation entryId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_block_tag");

    public static final Type<C2SRemoveBlockTagPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemoveBlockTagPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRemoveBlockTagPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveBlockTagPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveBlockTagPayload::tagId,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveBlockTagPayload::entryId,
        C2SRemoveBlockTagPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
