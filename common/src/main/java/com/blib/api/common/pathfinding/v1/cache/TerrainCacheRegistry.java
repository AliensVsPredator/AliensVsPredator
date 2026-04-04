package com.blib.api.common.pathfinding.v1.cache;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.pathfinding.v1.terrain.TerrainClassifier;

/**
 * Global registry of terrain classification caches, keyed by Level and classifier identity. Entities with the same
 * classifier share a cache. Block change events invalidate all caches for the affected level.
 */
public final class TerrainCacheRegistry {

    private static final Map<Level, Map<TerrainClassifier, TerrainClassificationCache>> CACHES = new ConcurrentHashMap<>();

    /**
     * Gets or creates a classification cache for the given level and classifier. Entities with the same classifier
     * share the same cache.
     */
    public static TerrainClassificationCache getOrCreate(Level level, TerrainClassifier classifier) {
        return CACHES
            .computeIfAbsent(level, $ -> new ConcurrentHashMap<>())
            .computeIfAbsent(classifier, TerrainClassificationCache::new);
    }

    /**
     * Called when a block changes in the level. Invalidates the affected section in all caches for that level.
     */
    public static void onBlockChanged(Level level, BlockPos pos) {
        var levelCaches = CACHES.get(level);

        if (levelCaches == null) {
            return;
        }

        for (var cache : levelCaches.values()) {
            cache.invalidateBlock(pos);
        }
    }

    /**
     * Removes all caches for a level (call on level unload).
     */
    public static void onLevelUnload(Level level) {
        CACHES.remove(level);
    }

    private TerrainCacheRegistry() {
        throw new UnsupportedOperationException();
    }
}
