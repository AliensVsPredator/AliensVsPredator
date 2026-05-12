package com.blib.api.common.dismemberment.v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.registry.v1.BLibHolder;

/**
 * Static registry binding entity types to the limbs they expose for dismemberment.
 * <p>
 * Two-tier storage:
 * <ul>
 * <li><b>Tier 1</b> — Java-registered defaults via {@link #register}. Populated at mod init.</li>
 * <li><b>Tier 2</b> — JSON-loaded entries via {@link #replaceTier2}. Populated by {@code LimbDefinitionDataLoader} on
 * server data-pack reload, and by {@code S2CLimbDefinitionsSyncPayload} on the client.</li>
 * </ul>
 * Lookups merge both tiers, with tier 2 winning on collision by {@link LimbDefinition#id()}. Registration is keyed by
 * the {@link ResourceLocation} of the entity type so that callers can register before holders are bound (mod init
 * time). Lookups by live {@link EntityType} resolve the type's registry id at query time, by which point the registry
 * is fully populated.
 */
public final class LimbDefinitionRegistry {

    private static final Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> TIER1 = new ConcurrentHashMap<>();

    private static volatile Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> tier2 = Map.of();

    public static void register(BLibHolder<? extends EntityType<?>> entityTypeHolder, LimbDefinition limbDefinition) {
        register(entityTypeHolder.getResourceLocation(), limbDefinition);
    }

    public static void register(ResourceLocation entityTypeId, LimbDefinition limbDefinition) {
        TIER1
            .computeIfAbsent(entityTypeId, $ -> Collections.synchronizedMap(new LinkedHashMap<>()))
            .put(limbDefinition.id(), limbDefinition);
    }

    public static List<LimbDefinition> getDefinitions(EntityType<?> entityType) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return getDefinitions(entityTypeId);
    }

    public static List<LimbDefinition> getDefinitions(@Nullable ResourceLocation entityTypeId) {
        if (entityTypeId == null) {
            return List.of();
        }

        var t1 = TIER1.get(entityTypeId);
        var t2 = tier2.get(entityTypeId);

        if (t1 == null && t2 == null) {
            return List.of();
        }
        if (t2 == null) {
            synchronized (t1) {
                return List.copyOf(t1.values());
            }
        }
        if (t1 == null) {
            return List.copyOf(t2.values());
        }

        // Merge with tier 2 overriding tier 1 per limb id. LinkedHashMap to keep tier-1 insertion order stable for the
        // entries tier 2 doesn't replace.
        var merged = new LinkedHashMap<ResourceLocation, LimbDefinition>();
        synchronized (t1) {
            merged.putAll(t1);
        }
        merged.putAll(t2);
        return List.copyOf(merged.values());
    }

    public static List<LimbDefinition> getDefinitions(Entity entity) {
        return getDefinitions(entity.getType());
    }

    public static List<LimbDefinition> getDefinitionsByCategory(EntityType<?> entityType, LimbCategory category) {
        return getDefinitions(entityType).stream()
            .filter(definition -> definition.category().equals(category))
            .toList();
    }

    public static @Nullable LimbDefinition getDefinition(EntityType<?> entityType, ResourceLocation limbId) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

        if (entityTypeId == null) {
            return null;
        }

        var t2 = tier2.get(entityTypeId);
        if (t2 != null) {
            var d = t2.get(limbId);
            if (d != null) {
                return d;
            }
        }
        var t1 = TIER1.get(entityTypeId);
        if (t1 != null) {
            return t1.get(limbId);
        }
        return null;
    }

    public static boolean hasDefinitions(EntityType<?> entityType) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        if (entityTypeId == null) {
            return false;
        }
        var t1 = TIER1.get(entityTypeId);
        if (t1 != null && !t1.isEmpty()) {
            return true;
        }
        var t2 = tier2.get(entityTypeId);
        return t2 != null && !t2.isEmpty();
    }

    /**
     * Atomically swap the tier-2 storage. Called by the server data-pack reload listener and by the S2C sync handler on
     * the client. Pass an empty map to clear tier 2 (Java defaults remain).
     */
    @ApiStatus.Internal
    public static void replaceTier2(Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> next) {
        tier2 = Map.copyOf(next);
    }

    /**
     * Returns a defensive copy of the merged registry state (tier 1 + tier 2, with tier 2 overriding by limb id). Used
     * by {@code S2CLimbDefinitionsSyncPayload} to ship the full server-side picture to clients on player join — the
     * client may not have run the same Java-side {@code Builder} registrations (e.g. server-only mods), so we sync the
     * merged snapshot rather than just tier 2.
     */
    @ApiStatus.Internal
    public static Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> snapshotAll() {
        var merged = new LinkedHashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        for (var entry : TIER1.entrySet()) {
            var inner = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            synchronized (entry.getValue()) {
                inner.putAll(entry.getValue());
            }
            merged.put(entry.getKey(), inner);
        }
        for (var entry : tier2.entrySet()) {
            merged.computeIfAbsent(entry.getKey(), $ -> new LinkedHashMap<>()).putAll(entry.getValue());
        }
        return merged;
    }

    private LimbDefinitionRegistry() {}
}
