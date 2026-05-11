package com.blib.engine.tag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-side cache of per-registry element + tag-name lists. Populated lazily — the Tag Editor sends a request the
 * first time the user picks (or changes) a registry in its header. Used to populate the Add-entry picker with both
 * direct entries and tag refs. Cleared on workspace close (entries are world-session-scoped).
 */
@ApiStatus.Internal
public final class RegistryEntriesCache {

    public record Entries(
        List<ResourceLocation> elements,
        List<ResourceLocation> tagIds
    ) {}

    private static final Map<ResourceLocation, Entries> BY_REGISTRY = new HashMap<>();

    private RegistryEntriesCache() {}

    public static void update(ResourceLocation registryKey, List<ResourceLocation> elements, List<ResourceLocation> tagIds) {
        BY_REGISTRY.put(registryKey, new Entries(List.copyOf(elements), List.copyOf(tagIds)));
    }

    public static @Nullable Entries get(ResourceLocation registryKey) {
        return BY_REGISTRY.get(registryKey);
    }

    public static void clear() {
        BY_REGISTRY.clear();
    }
}
