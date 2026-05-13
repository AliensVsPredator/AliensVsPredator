package com.blib.internal.common.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Server-side cache + mutation entrypoint for project-owned tag JSON. Sibling to {@link ProjectDraftStore} but keyed by
 * {@code (registryKey, tagId)} since one project can author overrides for many tags across many registries. The disk
 * file is the source of truth — every write goes straight through to disk via {@link EngineProjectIO#writeTagJson} so a
 * crash mid-session never strands edits in memory only.
 * <p>
 * First-read seed for a tag the project hasn't authored yet is the merge of every NON-project pack's TagFile JSON for
 * the tag, applied in load order with vanilla's {@code replace} semantics — so the inspector's Source view shows the
 * actual {@code #}-references and direct entries the way they appear in vanilla / mod / datapack source files. The seed
 * lives in memory only; it doesn't get written to disk until the user actually edits the tag (at which point
 * {@link #writeAndPersist} fires and the project's JSON gets created with the seeded contents + the edit). The
 * inspector pairs the source seed with a separately-fetched "resolved" member list (computed by
 * {@link #extractResolvedMembers}) so the user can toggle between the editable Source view and a read-only registry
 * preview.
 * <p>
 * Reuses {@link ProjectDraftStore#isReloading} as the single global reload gate — a project reload re-imports the whole
 * pack, so one flag covers both pool edits and tag edits.
 */
@ApiStatus.Internal
public final class ProjectTagDraftStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTagDraftStore.class);

    public static final ProjectTagDraftStore INSTANCE = new ProjectTagDraftStore();

    /** Compound key for the inner-map. Both halves are immutable, so equality / hashing on records is correct. */
    public record TagDraftKey(
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}

    /**
     * {@code project name → (registryKey, tagId) → root tag JSON}. Outer map and inner maps are guarded by
     * {@code synchronized} blocks on this — tag edits are server-thread-confined, but {@link #onServerStopped} could
     * fire from a shutdown hook on a different thread.
     */
    private final Map<String, Map<TagDraftKey, JsonObject>> drafts = new HashMap<>();

    private ProjectTagDraftStore() {}

    /**
     * Read-or-seed the project's JSON for one tag. Order: cache → disk (project's datapack) → seed from upstream pack
     * source files (in-memory only). Returns null only if the disk read produced something other than a JSON object
     * (e.g. malformed file) — logged.
     */
    public synchronized @Nullable JsonObject getOrSeedTag(
        MinecraftServer server,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        var key = new TagDraftKey(registryKey.location(), tagId);
        var byKey = drafts.computeIfAbsent(projectName, k -> new HashMap<>());
        var cached = byKey.get(key);
        if (cached != null) {
            return cached;
        }
        var fromDisk = EngineProjectIO.readTagJson(projectName, registryKey, tagId).orElse(null);
        if (fromDisk != null) {
            if (!fromDisk.isJsonObject()) {
                LOGGER.warn(
                    "[BLib] ProjectTagDraftStore.getOrSeedTag: tag {} for registry {} has non-object JSON",
                    tagId,
                    registryKey.location()
                );
                return null;
            }
            var obj = fromDisk.getAsJsonObject();
            byKey.put(key, obj);
            return obj;
        }
        var seeded = seedFromUpstreamJsons(server, projectName, registryKey, tagId);
        byKey.put(key, seeded);
        return seeded;
    }

    /**
     * Build the source-level seed by walking every pack's contribution for the tag's data path (excluding the project's
     * own pack), applying vanilla's load-order merge with {@code replace} semantics. Result is a fresh {@code TagFile}
     * object with {@code replace: false} and a {@code values} array containing the merged source entries — preserves
     * any {@code #}-references intact. Used when the project has no on-disk JSON for this tag yet, so the inspector
     * Source view shows what's already in the tag instead of an empty list.
     */
    private static JsonObject seedFromUpstreamJsons(
        MinecraftServer server,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        var seeded = new JsonObject();
        seeded.addProperty("replace", false);
        var values = new JsonArray();
        seeded.add("values", values);

        var resourcePath = ResourceLocation.fromNamespaceAndPath(
            tagId.getNamespace(),
            Registries.tagsDirPath(registryKey) + "/" + tagId.getPath() + ".json"
        );
        var projectPackId = EngineProjectIO.PACK_ID_PREFIX + projectName;

        for (var resource : server.getResourceManager().getResourceStack(resourcePath)) {
            if (projectPackId.equals(resource.sourcePackId())) {
                // Skip our own pack — getOrSeedTag handles the project JSON via readTagJson; we don't want to
                // double-include it here.
                continue;
            }
            try (var reader = resource.openAsReader()) {
                var json = JsonParser.parseReader(reader);
                if (!json.isJsonObject()) {
                    continue;
                }
                var obj = json.getAsJsonObject();
                // replace: true wipes everything before this pack and starts fresh from this pack's values.
                if (obj.has("replace") && obj.get("replace").isJsonPrimitive() && obj.get("replace").getAsBoolean()) {
                    values = new JsonArray();
                    seeded.add("values", values);
                }
                if (obj.has("values") && obj.get("values").isJsonArray()) {
                    for (var entry : obj.get("values").getAsJsonArray()) {
                        values.add(entry);
                    }
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.warn(
                    "[BLib] seedFromUpstreamJsons: failed to read tag {} from pack {}",
                    tagId,
                    resource.sourcePackId(),
                    e
                );
            }
        }
        return seeded;
    }

    /**
     * Snapshot the live registry's expanded member set for one tag — the post-reload "what's actually in this tag"
     * view, with all {@code #}-references already resolved into their constituent element ids. Used by the inspector's
     * Resolved view as a read-only preview alongside the editable Source view (which renders the project's raw JSON
     * entries from {@link #getOrSeedTag} with tag-references preserved).
     */
    public static List<ResourceLocation> extractResolvedMembers(
        MinecraftServer server,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        var out = new ArrayList<ResourceLocation>();
        // Delegate to a typed helper so Registry<T> + TagKey<T> line up — at the wildcard Registry<?> call site,
        // HolderSet.Named<?>'s Iterable<Holder<?>> bound is erased and the enhanced-for loop doesn't typecheck.
        collectResolvedMembersTyped(server, registryKey, tagId, out);
        return out;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> void collectResolvedMembersTyped(
        MinecraftServer server,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId,
        List<ResourceLocation> out
    ) {
        var rawRegistry = server.registryAccess().registry((ResourceKey) registryKey).orElse(null);
        if (rawRegistry == null) {
            return;
        }
        Registry<T> registry = (Registry<T>) rawRegistry;
        TagKey<T> tagKey = TagKey.create((ResourceKey<? extends Registry<T>>) registryKey, tagId);
        var holderSet = registry.getTag(tagKey).orElse(null);
        if (holderSet == null) {
            return;
        }
        for (var holder : holderSet) {
            holder.unwrapKey().ifPresent(elementKey -> out.add(elementKey.location()));
        }
    }

    /**
     * Persist {@code json} as the project's state for {@code tagId}: update the cache and write through to disk.
     */
    public synchronized void writeAndPersist(
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId,
        JsonObject json
    ) throws IOException {
        var key = new TagDraftKey(registryKey.location(), tagId);
        var byKey = drafts.computeIfAbsent(projectName, k -> new HashMap<>());
        byKey.put(key, json);
        EngineProjectIO.writeTagJson(projectName, registryKey, tagId, json);
    }

    /** Drop one tag's cache entry. Used after a delete so a future re-create doesn't see the old contents. */
    public synchronized void invalidate(String projectName, ResourceLocation registryKey, ResourceLocation tagId) {
        var byKey = drafts.get(projectName);
        if (byKey != null) {
            byKey.remove(new TagDraftKey(registryKey, tagId));
        }
    }

    /** Drop the project's entire cache. Called from the reload completion callback. */
    public synchronized void clearProject(String projectName) {
        drafts.remove(projectName);
    }

    /** Wipe everything. Called from server-stopped lifecycle. */
    public synchronized void onServerStopped() {
        drafts.clear();
    }

    /**
     * True if a tag JSON object is fully empty by our standards (no values, replace=false). Caller can use this to skip
     * persisting a no-op create or to choose an empty-state UI hint.
     */
    public static boolean isEmpty(JsonObject tag) {
        var replace = tag.has("replace") && tag.get("replace").getAsBoolean();
        var values = tag.has("values") && tag.get("values").isJsonArray() ? tag.getAsJsonArray("values") : null;
        return !replace && (values == null || values.isEmpty());
    }

    /**
     * Append one entry to the tag's {@code values} array. Tag refs always emit object form (so the {@code #} prefix
     * survives); direct entries with {@code required=true} emit a bare string (terser, matches vanilla's preferred
     * form), while direct entries with {@code required=false} emit object form to preserve the flag.
     */
    public static void applyAddEntry(JsonObject tag, boolean isTagRef, ResourceLocation entryId, boolean required) {
        var values = ensureValues(tag);
        if (isTagRef) {
            var entry = new JsonObject();
            entry.addProperty("id", "#" + entryId.toString());
            entry.addProperty("required", required);
            values.add(entry);
        } else if (required) {
            values.add(entryId.toString());
        } else {
            var entry = new JsonObject();
            entry.addProperty("id", entryId.toString());
            entry.addProperty("required", false);
            values.add(entry);
        }
    }

    /**
     * Toggle a tag entry's {@code required} flag in place. {@code true} writes the bare-string form (vanilla's
     * implicit-required compact form); {@code false} writes the object form with {@code required: false}. No-ops
     * (returns false) when the index is out of range or the existing entry has no parseable id field — those would have
     * been silently dropped by vanilla anyway, so the inspector shouldn't pretend it edited them.
     */
    public static boolean applySetEntryRequired(JsonObject tag, int rawIndex, boolean required) {
        var values = tag.has("values") && tag.get("values").isJsonArray() ? tag.getAsJsonArray("values") : null;
        if (values == null || rawIndex < 0 || rawIndex >= values.size()) {
            return false;
        }
        var existing = values.get(rawIndex);
        String idStr;
        if (existing.isJsonPrimitive() && existing.getAsJsonPrimitive().isString()) {
            idStr = existing.getAsString();
        } else if (existing.isJsonObject() && existing.getAsJsonObject().has("id")) {
            var idElem = existing.getAsJsonObject().get("id");
            if (!idElem.isJsonPrimitive() || !idElem.getAsJsonPrimitive().isString()) {
                return false;
            }
            idStr = idElem.getAsString();
        } else {
            return false;
        }
        if (required) {
            values.set(rawIndex, new JsonPrimitive(idStr));
        } else {
            var entry = new JsonObject();
            entry.addProperty("id", idStr);
            entry.addProperty("required", false);
            values.set(rawIndex, entry);
        }
        return true;
    }

    /** Remove the entry at {@code rawIndex}. Returns true if a change was applied. */
    public static boolean applyRemoveEntry(JsonObject tag, int rawIndex) {
        var values = tag.has("values") && tag.get("values").isJsonArray() ? tag.getAsJsonArray("values") : null;
        if (values == null || rawIndex < 0 || rawIndex >= values.size()) {
            return false;
        }
        values.remove(rawIndex);
        return true;
    }

    /** Set the tag's top-level {@code replace} flag. Always returns true (the field exists or gets created). */
    public static boolean applySetReplace(JsonObject tag, boolean replace) {
        tag.addProperty("replace", replace);
        return true;
    }

    /**
     * Walk the tag's {@code values} array and produce the wire-form list. Handles both bare-string entries (treated as
     * {@code required=true}; {@code #} prefix denotes tag-ref) and the object form ({@code id} + optional
     * {@code required}; {@code #} prefix on {@code id} denotes tag-ref). Entries that don't parse as a valid
     * {@link ResourceLocation} are silently skipped — they wouldn't be loadable by vanilla either.
     */
    public static List<TagEntryDraft> extractDraftEntries(JsonObject tag) {
        var out = new ArrayList<TagEntryDraft>();
        var values = tag.has("values") && tag.get("values").isJsonArray() ? tag.getAsJsonArray("values") : null;
        if (values == null) {
            return out;
        }
        for (var i = 0; i < values.size(); i++) {
            var raw = values.get(i);
            var parsed = parseEntry(i, raw);
            if (parsed != null) {
                out.add(parsed);
            }
        }
        return out;
    }

    /** Top-level {@code replace} flag, defaulting to false when absent or wrong type. */
    public static boolean readReplace(JsonObject tag) {
        return tag.has("replace") && tag.get("replace").isJsonPrimitive() && tag.get("replace").getAsBoolean();
    }

    private static @Nullable TagEntryDraft parseEntry(int rawIndex, JsonElement raw) {
        if (raw.isJsonPrimitive() && raw.getAsJsonPrimitive().isString()) {
            return parseIdString(rawIndex, raw.getAsString(), true);
        }
        if (raw.isJsonObject()) {
            var obj = raw.getAsJsonObject();
            if (!obj.has("id")) {
                return null;
            }
            var idStr = obj.get("id").getAsString();
            var required = !obj.has("required") || !obj.get("required").isJsonPrimitive() || obj.get("required").getAsBoolean();
            return parseIdString(rawIndex, idStr, required);
        }
        return null;
    }

    private static @Nullable TagEntryDraft parseIdString(int rawIndex, String idStr, boolean required) {
        var isTagRef = idStr.startsWith("#");
        var bare = isTagRef ? idStr.substring(1) : idStr;
        try {
            var id = ResourceLocation.parse(bare);
            // inUpstream is left false at parse time; sendTagDraft post-processes the list with the upstream-keys
            // set, so per-entry parsing stays oblivious to the seed lookup (it's only needed for the wire form).
            return new TagEntryDraft(rawIndex, isTagRef, id, required, false);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Stringified {@code (isTagRef, id)} key, suitable for {@code Set} membership tests against the upstream entry
     * list. Pairs with {@link #extractUpstreamEntryKeys}.
     */
    public static String entryKey(boolean isTagRef, ResourceLocation id) {
        return (isTagRef ? "#" : "") + id;
    }

    /**
     * Build the set of {@link #entryKey} strings contributed by every non-project pack for the given tag. Re-runs the
     * seed-from-upstream pass internally, so it sees the same view that {@code sendTagDraft} would have if the project
     * hadn't yet authored the tag. Used to flag draft entries that duplicate upstream — those are no-ops to remove or
     * toggle in merge mode.
     */
    public static Set<String> extractUpstreamEntryKeys(
        MinecraftServer server,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        var seed = seedFromUpstreamJsons(server, projectName, registryKey, tagId);
        var entries = extractDraftEntries(seed);
        var keys = new HashSet<String>(entries.size());
        for (var entry : entries) {
            keys.add(entryKey(entry.isTagRef(), entry.id()));
        }
        return keys;
    }

    /**
     * Does the project's JSON for this tag have no net effect on the merged result? True when {@code replace=false} and
     * every entry in the JSON's {@code values} array is also contributed by some non-project pack — i.e. the merged tag
     * is byte-identical to what upstream would produce alone. {@code replace=true} can't be equivalent (it explicitly
     * wipes upstream); an empty {@code values} array with {@code replace=false} is equivalent (the project contributes
     * nothing, so upstream wins). Used by the Tag Browser to neutralize "looks modified but isn't" rows and signal that
     * the project's JSON is a candidate for cleanup.
     */
    public static boolean isEquivalentToUpstream(
        MinecraftServer server,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) {
        var tag = INSTANCE.getOrSeedTag(server, projectName, registryKey, tagId);
        if (tag == null) {
            return false;
        }
        return isEquivalentToUpstream(server, projectName, registryKey, tagId, tag);
    }

    /**
     * Variant that operates against a caller-supplied {@link JsonObject}. Lets edit handlers run the check against the
     * just-mutated JSON in-hand, without re-reading the cache or disk — avoids any race between the cache state and
     * what's about to be persisted.
     */
    public static boolean isEquivalentToUpstream(
        MinecraftServer server,
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId,
        JsonObject tag
    ) {
        if (readReplace(tag)) {
            return false;
        }
        var entries = extractDraftEntries(tag);
        if (entries.isEmpty()) {
            return true;
        }
        var upstreamKeys = extractUpstreamEntryKeys(server, projectName, registryKey, tagId);
        for (var entry : entries) {
            if (!upstreamKeys.contains(entryKey(entry.isTagRef(), entry.id()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove the project's authored JSON for one tag from both the in-memory cache and disk. Called by the auto-
     * cleanup pass when an edit results in an equivalent-to-upstream JSON — the datapack shouldn't claim ownership of
     * tags it isn't actually modifying. Subsequent reads re-seed from upstream packs as if the project had never
     * authored the tag.
     */
    public synchronized void deleteProjectTag(
        String projectName,
        ResourceKey<? extends Registry<?>> registryKey,
        ResourceLocation tagId
    ) throws IOException {
        var byKey = drafts.get(projectName);
        if (byKey != null) {
            byKey.remove(new TagDraftKey(registryKey.location(), tagId));
        }
        EngineProjectIO.deleteTagJson(projectName, registryKey, tagId);
    }

    private static JsonArray ensureValues(JsonObject tag) {
        if (tag.has("values") && tag.get("values").isJsonArray()) {
            return tag.getAsJsonArray("values");
        }
        var arr = new JsonArray();
        tag.add("values", arr);
        return arr;
    }
}
