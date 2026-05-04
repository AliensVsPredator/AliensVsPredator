package com.blib.api.common.dismemberment.v1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
 * Registration is keyed by the {@link ResourceLocation} of the entity type so that callers can register before holders
 * are bound (mod init time). Lookups by live {@link EntityType} resolve the type's registry id at query time, by which
 * point the registry is fully populated.
 */
public final class LimbDefinitionRegistry {

    private static final Map<ResourceLocation, Map<ResourceLocation, LimbDefinition>> LIMBS_BY_ENTITY_TYPE_ID =
        new ConcurrentHashMap<>();

    public static void register(BLibHolder<? extends EntityType<?>> entityTypeHolder, LimbDefinition limbDefinition) {
        register(entityTypeHolder.getResourceLocation(), limbDefinition);
    }

    public static void register(ResourceLocation entityTypeId, LimbDefinition limbDefinition) {
        LIMBS_BY_ENTITY_TYPE_ID
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

        var entityLimbs = LIMBS_BY_ENTITY_TYPE_ID.get(entityTypeId);

        if (entityLimbs == null) {
            return List.of();
        }

        synchronized (entityLimbs) {
            return List.copyOf(entityLimbs.values());
        }
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

        var entityLimbs = LIMBS_BY_ENTITY_TYPE_ID.get(entityTypeId);

        if (entityLimbs == null) {
            return null;
        }

        return entityLimbs.get(limbId);
    }

    public static boolean hasDefinitions(EntityType<?> entityType) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        var entityLimbs = LIMBS_BY_ENTITY_TYPE_ID.get(entityTypeId);
        return entityLimbs != null && !entityLimbs.isEmpty();
    }

    private LimbDefinitionRegistry() {}
}
