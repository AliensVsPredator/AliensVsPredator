package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Wire form of one row in the tag catalog. Identifies a tag by the registry it belongs to + its tag id, with three
 * provenance flags driving the browser's color coding:
 * <ul>
 * <li>{@code inProject} — the active project has its own JSON file for this tag.</li>
 * <li>{@code inUpstream} — at least one non-project pack (vanilla, mods, other datapacks) ships a JSON file for this
 * tag at the same path.</li>
 * <li>{@code equivalentToUpstream} — the project's JSON is {@code replace=false} with every entry also contributed by
 * upstream packs, so the merged tag is identical to what upstream produces alone. Set true ⇒ the project's JSON has no
 * net effect; the browser paints the row neutral so it doesn't read as "modified".</li>
 * </ul>
 * Together: inProject + inUpstream + !equivalentToUpstream → project has actually modified an existing upstream tag;
 * only inProject → project has authored a brand new tag; only inUpstream → vanilla / mods own it; inProject +
 * equivalentToUpstream → there's a JSON on disk but it's redundant (good candidate for cleanup).
 */
public record TagCatalogEntry(
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean inProject,
    boolean inUpstream,
    boolean equivalentToUpstream
) {

    public static final StreamCodec<TagCatalogEntry> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        TagCatalogEntry::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        TagCatalogEntry::tagId,
        StreamCodecs.BOOLEAN,
        TagCatalogEntry::inProject,
        StreamCodecs.BOOLEAN,
        TagCatalogEntry::inUpstream,
        StreamCodecs.BOOLEAN,
        TagCatalogEntry::equivalentToUpstream,
        TagCatalogEntry::new
    );
}
