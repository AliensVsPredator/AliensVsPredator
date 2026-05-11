package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Wire form of one entry inside a project's tag override JSON. Each entry is either a direct registry-element reference
 * (e.g. {@code minecraft:cherry_log}) or a tag reference (e.g. {@code #minecraft:logs_that_burn}); the {@code isTagRef}
 * flag distinguishes the two and the {@code id} is the bare {@link ResourceLocation} (no {@code #} prefix on the wire,
 * the editor renders it).
 * <p>
 * {@code rawIndex} addresses the entry's position in the on-disk tag JSON {@code values} array. Used by
 * {@link S2CTagDraftPayload} to push authoritative tag state to the client editor; also by per-row remove packets to
 * address an entry by its position in the array.
 * <p>
 * {@code inUpstream} is set server-side: true if this entry's {@code (isTagRef, id)} pair also appears in at least one
 * non-project pack's contribution to the tag. In merge mode (replace=false), upstream-duplicate entries are no-ops to
 * remove or toggle — vanilla's load-order merge means the upstream contribution sticks regardless of the project's
 * edits — so the inspector hides the X / req-opt controls on those rows. Replace mode ignores the flag (the project
 * is the sole contributor).
 */
public record TagEntryDraft(
    int rawIndex,
    boolean isTagRef,
    ResourceLocation id,
    boolean required,
    boolean inUpstream
) {

    public static final StreamCodec<TagEntryDraft> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        TagEntryDraft::rawIndex,
        StreamCodecs.BOOLEAN,
        TagEntryDraft::isTagRef,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        TagEntryDraft::id,
        StreamCodecs.BOOLEAN,
        TagEntryDraft::required,
        StreamCodecs.BOOLEAN,
        TagEntryDraft::inUpstream,
        TagEntryDraft::new
    );
}
