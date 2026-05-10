package com.blib.internal.common.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server-side cache + mutation entrypoint for project-owned pool JSON. The disk file is the source of truth — this
 * class avoids re-reading it on every edit packet, but on every change it writes through to disk via
 * {@link EngineProjectIO#writePoolJson} so a crash mid-session never leaves edits stranded in memory only.
 * <p>
 * Each pool is lazy-seeded the first time it's edited within a project: if the project's datapack already has a JSON
 * for the pool, we read it; otherwise we encode the live registry pool with vanilla's
 * {@link StructureTemplatePool#DIRECT_CODEC} so the project starts with a faithful copy of what's currently in-game.
 * The first-edit JSON of a vanilla pool will look like a full rewrite (Holder→ResourceLocation, key reordering, comment
 * loss) — that's the codec's faithful round-trip, not a bug.
 * <p>
 * The {@link #isReloading} flag exists to plug a reload-vs-edit race: if a {@code reloadResources} is in flight and an
 * edit packet arrives, the edit could land between the disk write of the previous edit and the registry's re-import,
 * with non-deterministic visibility. Server handlers consult {@link #isReloading} and reject edits while set; the
 * picker / FILE menu disables their reload buttons during the reload window so users don't queue up conflicting
 * actions.
 * <p>
 * Lifecycle: {@link #onServerStopped} clears all caches and the reloading flag. The instance itself is a static
 * singleton, but its state is per-server-session — wired into {@code BLib.MOD.events().onServerStopped()}.
 */
@ApiStatus.Internal
public final class ProjectDraftStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDraftStore.class);

    public static final ProjectDraftStore INSTANCE = new ProjectDraftStore();

    /**
     * {@code project name → pool id → root pool JSON}. Outer map and inner maps are guarded by {@code synchronized}
     * blocks on this — pool edits are server-thread-confined, but {@link #onServerStopped} could fire from a shutdown
     * hook on a different thread.
     */
    private final Map<String, Map<ResourceLocation, JsonObject>> drafts = new HashMap<>();

    /**
     * Set true around the {@code reloadResources} call. Edit handlers reject packets while set so the reload's "read
     * disk → import to registry" path doesn't race with a concurrent disk-write from an edit packet.
     */
    private volatile boolean reloading;

    private ProjectDraftStore() {}

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean value) {
        this.reloading = value;
    }

    /**
     * Read-or-seed the project's JSON for one pool. Order: cache → disk (project's datapack) → seed from the live
     * registry's encoding. Returns null if seeding fails (pool not in registry, or the codec encode threw — both
     * logged).
     */
    public synchronized @Nullable JsonObject getOrSeedPool(MinecraftServer server, String projectName, ResourceLocation poolId) {
        var byPool = drafts.computeIfAbsent(projectName, k -> new HashMap<>());
        var cached = byPool.get(poolId);
        if (cached != null) {
            return cached;
        }
        var fromDisk = EngineProjectIO.readPoolJson(projectName, poolId).orElse(null);
        if (fromDisk != null && fromDisk.isJsonObject()) {
            var obj = fromDisk.getAsJsonObject();
            byPool.put(poolId, obj);
            return obj;
        }
        var seeded = seedFromRegistry(server, poolId);
        if (seeded != null) {
            byPool.put(poolId, seeded);
        }
        return seeded;
    }

    /**
     * Persist {@code json} as the project's state for {@code poolId}: update the cache and write through to disk. Disk
     * write goes through {@link EngineProjectIO#writePoolJson} so the project's marker / dir-creation rules apply (no
     * silent writes to a missing project).
     */
    public synchronized void writeAndPersist(
        String projectName,
        ResourceLocation poolId,
        JsonObject json
    ) throws IOException {
        var byPool = drafts.computeIfAbsent(projectName, k -> new HashMap<>());
        byPool.put(poolId, json);
        EngineProjectIO.writePoolJson(projectName, poolId, json);
    }

    /**
     * Drop the project's cache. Called after a reload so the next read-or-seed pulls from the freshly-imported
     * registry.
     */
    public synchronized void clearProject(String projectName) {
        drafts.remove(projectName);
    }

    public synchronized void onServerStopped(MinecraftServer server) {
        drafts.clear();
        reloading = false;
    }

    /**
     * Encode the live registry's pool into JSON. Mirrors the {@code handleSavePool} call site — uses a registry-aware
     * {@code RegistryOps} wrapper around {@code JsonOps.INSTANCE} so {@link net.minecraft.core.Holder} fields
     * (specifically the pool's fallback reference) round-trip cleanly to a {@link ResourceLocation}. Plain
     * {@link JsonOps#INSTANCE} silently fails on those holders.
     */
    private static @Nullable JsonObject seedFromRegistry(MinecraftServer server, ResourceLocation poolId) {
        var registryAccess = server.registryAccess();
        var registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        var pool = registry.get(poolId);
        if (pool == null) {
            LOGGER.warn("[BLib] ProjectDraftStore.seedFromRegistry: pool {} not in registry", poolId);
            return null;
        }
        try {
            var ops = registryAccess.createSerializationContext(JsonOps.INSTANCE);
            var json = StructureTemplatePool.DIRECT_CODEC.encodeStart(ops, pool).getOrThrow();
            if (!json.isJsonObject()) {
                LOGGER.warn("[BLib] ProjectDraftStore.seedFromRegistry: codec produced non-object for {}", poolId);
                return null;
            }
            return json.getAsJsonObject();
        } catch (Throwable t) {
            LOGGER.error("[BLib] ProjectDraftStore.seedFromRegistry: codec failed for pool {}", poolId, t);
            return null;
        }
    }

    /**
     * Mutate {@code pool.elements[rawIndex]} — set its {@code weight} and, if the wrapped element is a
     * {@code single_pool_element}, its {@code projection}. Returns true if a change was applied.
     */
    public static boolean applyUpdate(JsonObject pool, int rawIndex, int newWeight, int newProjectionOrdinal) {
        var entry = elementAt(pool, rawIndex);
        if (entry == null) {
            return false;
        }
        entry.addProperty("weight", Math.max(1, newWeight));
        var element = entry.has("element") && entry.get("element").isJsonObject() ? entry.getAsJsonObject("element") : null;
        if (element != null && isSingleElement(element)) {
            element.addProperty("projection", projectionName(newProjectionOrdinal));
        }
        return true;
    }

    /** Append a new {@code single_pool_element} entry with the given template id, weight, and projection. */
    public static void applyAdd(JsonObject pool, ResourceLocation templateId, int weight, int projectionOrdinal) {
        var elements = ensureElements(pool);
        var entry = new JsonObject();
        entry.addProperty("weight", Math.max(1, weight));
        var element = new JsonObject();
        element.addProperty("element_type", "minecraft:single_pool_element");
        element.addProperty("location", templateId.toString());
        element.addProperty("projection", projectionName(projectionOrdinal));
        // "processors" is optional in the codec; vanilla defaults to the empty processor list when absent so we
        // omit it here to keep new entries minimal.
        entry.add("element", element);
        elements.add(entry);
    }

    /** Remove the entry at {@code rawIndex}. Returns true if a change was applied. */
    public static boolean applyRemove(JsonObject pool, int rawIndex) {
        var elements = pool.has("elements") && pool.get("elements").isJsonArray() ? pool.getAsJsonArray("elements") : null;
        if (elements == null || rawIndex < 0 || rawIndex >= elements.size()) {
            return false;
        }
        elements.remove(rawIndex);
        return true;
    }

    /**
     * Walk the pool's elements array and produce a flat list of editable rows for the client. Top-level
     * {@code single_pool_element} entries get their array index as {@code rawIndex}; children of a
     * {@code list_pool_element} are emitted with {@code rawIndex == -1} so the editor can show them but won't try to
     * address them via per-row edit packets. Other element types ({@code empty_pool_element},
     * {@code feature_pool_element}) are skipped — they don't have a template id to display.
     */
    public static List<com.blib.mod.common.network.packet.DraftPoolElement> extractDraftElements(JsonObject pool) {
        var out = new ArrayList<com.blib.mod.common.network.packet.DraftPoolElement>();
        var elements = pool.has("elements") && pool.get("elements").isJsonArray() ? pool.getAsJsonArray("elements") : null;
        if (elements == null) {
            return Collections.emptyList();
        }
        for (var i = 0; i < elements.size(); i++) {
            var raw = elements.get(i);
            if (!raw.isJsonObject()) {
                continue;
            }
            var entry = raw.getAsJsonObject();
            var weight = entry.has("weight") ? entry.get("weight").getAsInt() : 1;
            var element = entry.has("element") && entry.get("element").isJsonObject() ? entry.getAsJsonObject("element") : null;
            if (element == null) {
                continue;
            }
            if (isSingleElement(element)) {
                var loc = element.has("location") ? element.get("location").getAsString() : "";
                var rl = parseLocation(loc);
                if (rl == null) {
                    continue;
                }
                var projOrd = projectionOrdinal(element);
                out.add(new com.blib.mod.common.network.packet.DraftPoolElement(i, rl, weight, projOrd));
            } else if (isListElement(element)) {
                var nested = element.has("elements") && element.get("elements").isJsonArray() ? element.getAsJsonArray("elements") : null;
                if (nested == null) {
                    continue;
                }
                for (var child : nested) {
                    if (!child.isJsonObject()) {
                        continue;
                    }
                    var childObj = child.getAsJsonObject();
                    if (!isSingleElement(childObj)) {
                        continue;
                    }
                    var loc = childObj.has("location") ? childObj.get("location").getAsString() : "";
                    var rl = parseLocation(loc);
                    if (rl == null) {
                        continue;
                    }
                    var projOrd = projectionOrdinal(childObj);
                    out.add(new com.blib.mod.common.network.packet.DraftPoolElement(-1, rl, weight, projOrd));
                }
            }
        }
        return out;
    }

    private static @Nullable JsonObject elementAt(JsonObject pool, int rawIndex) {
        var elements = pool.has("elements") && pool.get("elements").isJsonArray() ? pool.getAsJsonArray("elements") : null;
        if (elements == null || rawIndex < 0 || rawIndex >= elements.size()) {
            return null;
        }
        var raw = elements.get(rawIndex);
        return raw.isJsonObject() ? raw.getAsJsonObject() : null;
    }

    private static JsonArray ensureElements(JsonObject pool) {
        if (pool.has("elements") && pool.get("elements").isJsonArray()) {
            return pool.getAsJsonArray("elements");
        }
        var arr = new JsonArray();
        pool.add("elements", arr);
        return arr;
    }

    private static boolean isSingleElement(JsonObject element) {
        var type = element.has("element_type") ? element.get("element_type").getAsString() : "";
        return type.equals("minecraft:single_pool_element") || type.equals("single_pool_element");
    }

    private static boolean isListElement(JsonObject element) {
        var type = element.has("element_type") ? element.get("element_type").getAsString() : "";
        return type.equals("minecraft:list_pool_element") || type.equals("list_pool_element");
    }

    private static int projectionOrdinal(JsonObject element) {
        var projStr = element.has("projection") ? element.get("projection").getAsString() : "rigid";
        var values = StructureTemplatePool.Projection.values();
        for (var p : values) {
            if (p.getSerializedName().equals(projStr)) {
                return p.ordinal();
            }
        }
        return 0;
    }

    private static String projectionName(int ordinal) {
        var values = StructureTemplatePool.Projection.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return values[0].getSerializedName();
        }
        return values[ordinal].getSerializedName();
    }

    private static @Nullable ResourceLocation parseLocation(String s) {
        try {
            return ResourceLocation.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
