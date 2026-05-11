package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.common.storage.ProjectTagDraftStore.TagDraftKey;
import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Client-side cache of tag drafts pushed by the server via {@code S2CTagDraftPayload}. The Tag Editor reads from this
 * in place of the live registry — registry stays frozen at pre-reload values until the user runs Reload Project.
 * <p>
 * Cleared on Reload Project (so the next read re-fetches from a freshly-imported registry through the request packet)
 * and on engine workspace close. Keyed by {@code (registryKey, tagId)}; the active project name is implicit (changing
 * projects clears the cache via workspace close).
 */
@ApiStatus.Internal
public final class TagDraftCache {

    private static final Map<TagDraftKey, TagDraft> DRAFTS = new HashMap<>();

    private TagDraftCache() {}

    /** Replace the cached draft for {@code (registryKey, tagId)}. Called from the S2C handler. */
    public static void update(
        ResourceLocation registryKey,
        ResourceLocation tagId,
        boolean replace,
        List<TagEntryDraft> entries,
        List<ResourceLocation> resolvedMembers
    ) {
        DRAFTS.put(
            new TagDraftKey(registryKey, tagId),
            new TagDraft(replace, List.copyOf(entries), List.copyOf(resolvedMembers))
        );
    }

    /** Returns the cached draft, or {@code null} if none. */
    public static @Nullable TagDraft get(ResourceLocation registryKey, ResourceLocation tagId) {
        return DRAFTS.get(new TagDraftKey(registryKey, tagId));
    }

    public static void clear() {
        DRAFTS.clear();
    }
}
