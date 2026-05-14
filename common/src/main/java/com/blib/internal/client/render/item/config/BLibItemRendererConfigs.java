package com.blib.internal.client.render.item.config;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Process-wide registry of resolved item-renderer configs, populated by {@link BLibItemRendererConfigLoader} on every
 * client resource reload. Renderers query it per-frame via {@link #getOrNull} so reloads propagate without re-creating
 * renderer instances. The replace is atomic — readers always see a fully-resolved snapshot.
 */
public final class BLibItemRendererConfigs {

    private static volatile Map<ResourceLocation, ResolvedItemRendererConfig> ENTRIES = Map.of();

    private BLibItemRendererConfigs() {
        throw new UnsupportedOperationException();
    }

    public static @Nullable ResolvedItemRendererConfig getOrNull(ResourceLocation id) {
        return ENTRIES.get(id);
    }

    public static Set<ResourceLocation> allConfigIds() {
        return ENTRIES.keySet();
    }

    /** Called by the loader at the end of each reload pass. Replaces the snapshot atomically. */
    static void replace(Map<ResourceLocation, ResolvedItemRendererConfig> next) {
        ENTRIES = Map.copyOf(next);
    }
}
