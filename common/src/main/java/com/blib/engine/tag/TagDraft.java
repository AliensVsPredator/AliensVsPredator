package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Client-side cached state for one tag — both the project's source-level entries (with {@code #}-references preserved
 * for the Source view) and the live registry's expanded member set (for the read-only Resolved view). Populated from
 * {@code S2CTagDraftPayload}, read by the inspector's tag-view render loop.
 */
@ApiStatus.Internal
public record TagDraft(
    boolean replace,
    List<TagEntryDraft> entries,
    List<ResourceLocation> resolvedMembers
) {}
