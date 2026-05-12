package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unified client-side overlay of tag edits the user has made this session but hasn't reloaded yet. Three layers:
 * <ul>
 * <li>{@link #stagedTags} — tags whose JSON has been touched (new tags, entry add/remove, replace-flag toggle). Drives
 * the "this tag has unreloaded changes" red color in the Tag Browser and the block inspector.</li>
 * <li>{@link #stagedAdds} — entries (direct or tag-ref) the user has added to a tag. Drives the red color on entry rows
 * in the tag editor, and the block inspector's "this tag is pending for this block" optimistic list.</li>
 * <li>{@link #stagedRemoves} — entries the user has removed from a tag's draft. Block inspector consumes this to hide
 * tags it's been removed from before the runtime catches up.</li>
 * </ul>
 * All state is in-memory and cleared on Reload Project (toolbar) and engine exit
 * ({@link com.blib.engine.session.EngineMode#exit}). The user's mental model is "color tells me what's NEW (green),
 * what's MODIFIED (blue), and what's staged (red); reload commits red into green/blue."
 */
@ApiStatus.Internal
public final class TagStagingCache {

    public record TagKey(
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}

    public record EntryKey(
        ResourceLocation registryKey,
        ResourceLocation tagId,
        boolean isTagRef,
        ResourceLocation id
    ) {

        public TagKey tagKey() {
            return new TagKey(registryKey, tagId);
        }
    }

    private static final Set<TagKey> stagedTags = new HashSet<>();

    private static final Set<EntryKey> stagedAdds = new HashSet<>();

    private static final Set<EntryKey> stagedRemoves = new HashSet<>();

    private TagStagingCache() {}

    /** Record a tag-level edit (create-tag, replace-flag toggle) that doesn't have a per-entry component. */
    public static void markTagEdited(ResourceLocation registryKey, ResourceLocation tagId) {
        stagedTags.add(new TagKey(registryKey, tagId));
    }

    /**
     * Record an entry addition. Cancels any prior staged remove of the same entry (user is re-adding before reload),
     * and marks the parent tag as staged.
     */
    public static void markEntryAdded(ResourceLocation registryKey, ResourceLocation tagId, boolean isTagRef, ResourceLocation id) {
        var key = new EntryKey(registryKey, tagId, isTagRef, id);
        stagedRemoves.remove(key);
        stagedAdds.add(key);
        stagedTags.add(key.tagKey());
    }

    /**
     * Record an entry removal. If the entry was a staged add (user is undoing pre-reload), cancel that add — net effect
     * matches what reload would produce. Otherwise stage the remove (so consumers like the block inspector can hide the
     * tag from a from-the-block list before the runtime catches up). Either way the parent tag is marked staged because
     * the JSON was touched.
     */
    public static void markEntryRemoved(ResourceLocation registryKey, ResourceLocation tagId, boolean isTagRef, ResourceLocation id) {
        var key = new EntryKey(registryKey, tagId, isTagRef, id);
        if (!stagedAdds.remove(key)) {
            stagedRemoves.add(key);
        }
        stagedTags.add(key.tagKey());
    }

    public static boolean isTagStaged(ResourceLocation registryKey, ResourceLocation tagId) {
        return stagedTags.contains(new TagKey(registryKey, tagId));
    }

    public static boolean isEntryStagedAdd(ResourceLocation registryKey, ResourceLocation tagId, boolean isTagRef, ResourceLocation id) {
        return stagedAdds.contains(new EntryKey(registryKey, tagId, isTagRef, id));
    }

    /**
     * For a direct (non-tag-ref) entry of {@code registryKey} with id {@code entryId}, return the set of tag IDs the
     * user has staged it for inclusion in. Used by the block inspector to compose its effective tag list (live runtime
     * tags ∪ this set).
     */
    public static Set<ResourceLocation> stagedDirectAddsFor(ResourceLocation registryKey, ResourceLocation entryId) {
        return stagedAdds.stream()
            .filter(e -> !e.isTagRef() && e.registryKey().equals(registryKey) && e.id().equals(entryId))
            .map(EntryKey::tagId)
            .collect(Collectors.toSet());
    }

    /** Mirror of {@link #stagedDirectAddsFor} for pending removals. */
    public static Set<ResourceLocation> stagedDirectRemovesFor(ResourceLocation registryKey, ResourceLocation entryId) {
        return stagedRemoves.stream()
            .filter(e -> !e.isTagRef() && e.registryKey().equals(registryKey) && e.id().equals(entryId))
            .map(EntryKey::tagId)
            .collect(Collectors.toSet());
    }

    public static void clear() {
        stagedTags.clear();
        stagedAdds.clear();
        stagedRemoves.clear();
    }
}
