package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Wire form of one row in the tag catalog. Identifies a tag by the registry it belongs to + its tag id, with two
 * provenance flags driving the browser's color coding:
 * <ul>
 * <li>{@code inProject} — the active project has its own JSON file for this tag.</li>
 * <li>{@code inUpstream} — at least one non-project pack (vanilla, mods, other datapacks) ships a JSON file for this
 * tag at the same path.</li>
 * </ul>
 * Together: both true → project has modified an existing upstream tag; only inProject → project has authored a brand
 * new tag; only inUpstream → vanilla / mods own it; neither → impossible (wouldn't be in the catalog).
 */
public record TagCatalogEntry(
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean inProject,
    boolean inUpstream
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
        TagCatalogEntry::new
    );
}
