package com.blib.api.common.dismemberment.v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side registry mapping {@code (entityType, limbId)} to its {@link LimbVisuals}. Two tiers:
 * <ul>
 * <li><b>Tier 1</b> — Java-registered defaults pushed by {@link LimbDefinition.Builder#build()}.</li>
 * <li><b>Tier 2</b> — JSON-loaded entries from {@code assets/<ns>/blib_limb_visuals/} via
 * {@code LimbVisualsLoader}.</li>
 * </ul>
 * Lookups check tier 2 first; missing entries fall through to tier 1. Atomic swap of tier 2 happens on resource-pack
 * reload via {@link #replaceTier2(Map)}.
 * <p>
 * Server code must not touch this registry — it has no equivalent on the server side.
 */
public final class LimbVisualsRegistry {

    private static final Map<ResourceLocation, Map<ResourceLocation, LimbVisuals>> TIER1 = new ConcurrentHashMap<>();

    private static volatile Map<ResourceLocation, Map<ResourceLocation, LimbVisuals>> tier2 = Map.of();

    private LimbVisualsRegistry() {}

    /** Tier-1 (Java) registration. Called from {@link LimbDefinition.Builder#build()}. */
    public static void register(ResourceLocation entityTypeId, ResourceLocation limbId, LimbVisuals visuals) {
        TIER1.computeIfAbsent(entityTypeId, $ -> new ConcurrentHashMap<>()).put(limbId, visuals);
    }

    /** Same as the {@link ResourceLocation}-keyed variant but resolves {@code entityType} via the vanilla registry. */
    public static void register(EntityType<?> entityType, ResourceLocation limbId, LimbVisuals visuals) {
        register(BuiltInRegistries.ENTITY_TYPE.getKey(entityType), limbId, visuals);
    }

    public static @Nullable LimbVisuals get(EntityType<?> entityType, ResourceLocation limbId) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        if (entityTypeId == null) {
            return null;
        }
        return get(entityTypeId, limbId);
    }

    public static @Nullable LimbVisuals get(ResourceLocation entityTypeId, ResourceLocation limbId) {
        var t2 = tier2.get(entityTypeId);
        if (t2 != null) {
            var v = t2.get(limbId);
            if (v != null) {
                return v;
            }
        }
        var t1 = TIER1.get(entityTypeId);
        if (t1 != null) {
            return t1.get(limbId);
        }
        return null;
    }

    @ApiStatus.Internal
    public static void replaceTier2(Map<ResourceLocation, Map<ResourceLocation, LimbVisuals>> next) {
        tier2 = Map.copyOf(next);
    }
}
