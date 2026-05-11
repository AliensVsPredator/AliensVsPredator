package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.blib.mod.common.network.packet.TagCatalogEntry;

/**
 * Client-side cache of the most recent tag catalog pushed by the server. Holds the flat list of every
 * {@link TagCatalogEntry} plus a lazy registry-grouped view that the Tag Browser uses to render its collapsible
 * sections. Cleared on workspace close and on every fresh catalog push (the server sends the full snapshot — never a
 * delta).
 */
@ApiStatus.Internal
public final class TagCatalogCache {

    private static List<TagCatalogEntry> all = List.of();

    /** Lazily-computed grouped view; nulled out when {@link #update} replaces {@link #all}. */
    private static Map<ResourceLocation, List<TagCatalogEntry>> grouped;

    private TagCatalogCache() {}

    public static void update(List<TagCatalogEntry> entries) {
        all = List.copyOf(entries);
        grouped = null;
    }

    public static List<TagCatalogEntry> all() {
        return all;
    }

    /**
     * Returns the catalog grouped by registry key, with the registry-key map kept in sorted-namespaced-id order so the
     * browser's section order is deterministic. Each per-registry list is sorted by {@code (namespace, path)} of the
     * tag id.
     */
    public static Map<ResourceLocation, List<TagCatalogEntry>> groupedByRegistry() {
        if (grouped == null) {
            var working = new LinkedHashMap<ResourceLocation, List<TagCatalogEntry>>();
            // Sort registries by namespaced id, tags within a registry by namespaced id. We rebuild on every update,
            // so this LinkedHashMap insertion order is the render order.
            var byRegistry = new LinkedHashMap<ResourceLocation, ArrayList<TagCatalogEntry>>();
            for (var entry : all) {
                byRegistry.computeIfAbsent(entry.registryKey(), k -> new ArrayList<>()).add(entry);
            }
            byRegistry.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(ResourceLocation::toString)))
                .forEach(e -> {
                    e.getValue().sort(Comparator.comparing(c -> c.tagId().toString()));
                    working.put(e.getKey(), List.copyOf(e.getValue()));
                });
            grouped = working;
        }
        return grouped;
    }

    public static void clear() {
        all = List.of();
        grouped = null;
    }
}
