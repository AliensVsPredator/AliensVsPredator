package com.blib.api.common.entity.v1;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

public class EntitySenseCache {

    private final Entity entity;

    private final Map<Class<? extends Entity>, List<Entity>> entitiesByClassMap;

    private final Map<TagKey<EntityType<?>>, List<Entity>> entitiesByTagMap;

    private final Map<EntityType<?>, List<Entity>> entitiesByTypeMap;

    private final Map<Item, List<ItemEntity>> itemEntitiesByItemMap;

    private final RefreshPolicy<EntitySenseCache> refreshPolicy;

    private final ToIntFunction<EntitySenseCache> scanRadiusFunction;

    private final Set<TagKey<EntityType<?>>> trackedTags;

    private int lastSenseTick;

    private EntitySenseCache(
        Entity entity,
        RefreshPolicy<EntitySenseCache> refreshPolicy,
        ToIntFunction<EntitySenseCache> scanRadiusFunction,
        Set<TagKey<EntityType<?>>> trackedTags
    ) {
        this.entity = entity;
        this.entitiesByClassMap = new HashMap<>();
        this.entitiesByTagMap = new HashMap<>();
        this.entitiesByTypeMap = new HashMap<>();
        this.itemEntitiesByItemMap = new HashMap<>();
        this.refreshPolicy = refreshPolicy;
        this.scanRadiusFunction = scanRadiusFunction;
        this.trackedTags = trackedTags;
        this.lastSenseTick = 0;
    }

    public static Builder builder(Entity entity) {
        return new Builder(entity);
    }

    public void clear() {
        entitiesByClassMap.clear();
        entitiesByTypeMap.clear();
        itemEntitiesByItemMap.clear();
        entitiesByTagMap.clear();
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

        if (trackedTags.contains(tagKey)) {
            return entitiesByTagMap.getOrDefault(tagKey, List.of());
        }

        return getByTagMatch(tagKey);
    }

    private @NotNull List<Entity> getByTagMatch(TagKey<EntityType<?>> tagKey) {
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

    public Entity getEntity() {
        return entity;
    }

    public int getLastSenseTick() {
        return lastSenseTick;
    }

    private void tryPopulateCache() {
        if (!refreshPolicy.shouldRefresh(this)) {
            return;
        }

        clear();

        var scanRadius = scanRadiusFunction.applyAsInt(this);
        var diameter = scanRadius * 2;
        var scanArea = AABB.ofSize(entity.getEyePosition(), diameter, diameter, diameter);

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

            for (var tagKey : trackedTags) {
                if (entity.getType().is(tagKey)) {
                    entitiesByTagMap.computeIfAbsent(tagKey, $ -> new ArrayList<>())
                        .add(entity);
                }
            }
        }

        this.lastSenseTick = entity.tickCount;
    }

    public static class Builder {

        private final Entity entity;

        private final Set<TagKey<EntityType<?>>> trackedTags;

        private RefreshPolicy<EntitySenseCache> refreshPolicy;

        private ToIntFunction<EntitySenseCache> scanRadiusFunction;

        private Builder(Entity entity) {
            this.entity = entity;
            this.trackedTags = new HashSet<>();

            this.refreshPolicy = context -> context.getEntity().tickCount > context.getLastSenseTick() + 20;
            this.scanRadiusFunction = $ -> 16;
        }

        public Builder withRefreshPolicy(RefreshPolicy<EntitySenseCache> refreshPolicy) {
            this.refreshPolicy = refreshPolicy;
            return this;
        }

        public Builder withScanRadius(int scanRadius) {
            this.scanRadiusFunction = $ -> scanRadius;
            return this;
        }

        public Builder withScanRadius(ToIntFunction<EntitySenseCache> scanRadiusFunction) {
            this.scanRadiusFunction = scanRadiusFunction;
            return this;
        }

        public Builder addTrackedTag(TagKey<EntityType<?>> tagKey) {
            this.trackedTags.add(tagKey);
            return this;
        }

        public EntitySenseCache build() {
            return new EntitySenseCache(entity, refreshPolicy, scanRadiusFunction, trackedTags);
        }
    }
}
