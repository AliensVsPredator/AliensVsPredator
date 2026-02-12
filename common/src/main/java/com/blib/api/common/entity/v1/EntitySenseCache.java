package com.blib.api.common.entity.v1;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySenseCache {

    private final Entity entity;

    private final Map<Class<? extends Entity>, List<Entity>> entitiesByClassMap;

    private final Map<EntityType<?>, List<Entity>> entitiesByTypeMap;

    private final Map<Item, List<ItemEntity>> itemEntitiesByItemMap;

    private final int tickFrequency;

    private int lastSenseTick;

    public EntitySenseCache(Entity entity, int tickFrequency) {
        this.entity = entity;
        this.entitiesByClassMap = new HashMap<>();
        this.entitiesByTypeMap = new HashMap<>();
        this.itemEntitiesByItemMap = new HashMap<>();
        this.tickFrequency = tickFrequency;
        this.lastSenseTick = 0;
    }

    /**
     * Returns all nearby entities that are instances of the given class. Supports querying by superclass (e.g.,
     * {@code LivingEntity.class} will return all living entities). Results are cached for O(1) subsequent lookups.
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getByClass(Class<T> entityClass) {
        tryPopulateCache();

        // Check if we already have a cached result for this class.
        var cached = entitiesByClassMap.get(entityClass);

        if (cached != null) {
            return (List<T>) cached;
        }

        // Collect all entities that are assignable to the queried class.
        List<Entity> collectedEntities = null;

        for (var entry : entitiesByClassMap.entrySet()) {
            if (!entityClass.isAssignableFrom(entry.getKey())) {
                continue;
            }

            if (collectedEntities == null) {
                collectedEntities = new ArrayList<>();
            }

            collectedEntities.addAll(entry.getValue());
        }

        // Cache the result for future lookups.
        if (collectedEntities == null) {
            collectedEntities = List.of();
        }

        entitiesByClassMap.put(entityClass, collectedEntities);

        return (List<T>) collectedEntities;
    }

    public List<ItemEntity> getByItem(Item item) {
        return itemEntitiesByItemMap.getOrDefault(item, List.of());
    }

    public List<Entity> getByTag(TagKey<EntityType<?>> tagKey) {
        tryPopulateCache();

        List<Entity> collectedEntities = null;

        for (var entityType : entitiesByTypeMap.keySet()) {
            if (!entityType.is(tagKey)) {
                continue;
            }

            var entities = entitiesByTypeMap.getOrDefault(entityType, List.of());

            if (collectedEntities == null) {
                collectedEntities = new ArrayList<>();
            }

            collectedEntities.addAll(entities);
        }

        return collectedEntities == null
            ? List.of()
            : collectedEntities;
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getByType(EntityType<T> entityType) {
        tryPopulateCache();

        return (List<T>) entitiesByTypeMap.getOrDefault(entityType, List.of());
    }

    private void tryPopulateCache() {
        if (entity.tickCount <= lastSenseTick + tickFrequency) {
            return;
        }

        entitiesByClassMap.clear();
        entitiesByTypeMap.clear();
        itemEntitiesByItemMap.clear();

        var scanArea = AABB.ofSize(entity.getEyePosition(), 16, 16, 16);

        var entities = entity.level().getEntitiesOfClass(Entity.class, scanArea);

        for (var entity : entities) {
            entitiesByClassMap.computeIfAbsent(entity.getClass(), $ -> new ArrayList<>())
                .add(entity);
            entitiesByTypeMap.computeIfAbsent(entity.getType(), $ -> new ArrayList<>())
                .add(entity);

            if (entity instanceof ItemEntity itemEntity) {
                itemEntitiesByItemMap.computeIfAbsent(itemEntity.getItem().getItem(), $ -> new ArrayList<>())
                    .add(itemEntity);
            }
        }

        this.lastSenseTick = entity.tickCount;
    }
}
