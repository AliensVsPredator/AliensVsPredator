package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.codec.v1.BLibCodecs;

/**
 * Wire form of one row in the tag catalog. Identifies a tag by the registry it belongs to + its tag id, and carries an
 * {@code inProject} flag indicating whether the active project has authored an override for this tag (true) or whether
 * the tag exists only in upstream packs / vanilla (false). The Tag Browser uses this to show project tags with a
 * different visual treatment and to drive the "Project tags only" filter.
 */
public record TagCatalogEntry(
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean inProject
) {

    public static final StreamCodec<TagCatalogEntry> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        TagCatalogEntry::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        TagCatalogEntry::tagId,
        StreamCodecs.BOOLEAN,
        TagCatalogEntry::inProject,
        TagCatalogEntry::new
    );
}
