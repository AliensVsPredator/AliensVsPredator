package com.blib.engine.tag;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Client-side cached state for one tag: the {@code replace} flag plus the entry list. Populated from
 * {@code S2CTagDraftPayload}, read by the {@code TagEditorPanel} render loop. Not transmitted on the wire — the wire
 * uses the payload record directly.
 */
@ApiStatus.Internal
public record TagDraft(
    boolean replace,
    List<TagEntryDraft> entries
) {}
