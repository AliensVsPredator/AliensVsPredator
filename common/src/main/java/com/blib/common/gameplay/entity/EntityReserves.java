package com.blib.common.gameplay.entity;

import com.blib.common.gameplay.util.BLibEntityPredicates;
import com.blib.common.util.codec.impl.MojangCodecs;
import com.just.codec.Codec;
import com.just.codec.impl.Codecs;
import net.minecraft.world.entity.EntityType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class EntityReserves {

    public static final Codec<EntityReserves> CODEC = Codec.unboundedMap(
        MojangCodecs.ENTITY_TYPE,
        Codecs.INT
    )
        .xmap(
            map -> {
                var reserves = new EntityReserves();
                map.forEach(reserves::add);
                return reserves;
            },
            EntityReserves::getBackingMap
        );

    private final Map<EntityType<?>, Integer> entityTypesToCountMap;

    public EntityReserves() {
        this.entityTypesToCountMap = new HashMap<>();
    }

    public void add(EntityType<?> entityType, int count) {
        entityTypesToCountMap.compute(entityType, ($1, currentCount) -> {
            if (currentCount == null) {
                return Math.max(count, 0);
            }

            return Math.max(count + currentCount, 0);
        });
    }

    public void putAll(Map<EntityType<?>, Integer> map) {
        entityTypesToCountMap.putAll(map);
    }

    public int getCount(EntityType<?> entityType) {
        return entityTypesToCountMap.getOrDefault(entityType, 0);
    }

    public int getCount() {
        return getCountMatching(BLibEntityPredicates.alwaysTrue());
    }

    public int getCountMatching(Predicate<EntityType<?>> predicate) {
        return entityTypesToCountMap.entrySet()
            .stream()
            .filter(entry -> predicate.test(entry.getKey()))
            .mapToInt(Map.Entry::getValue)
            .sum();
    }

    public List<EntityType<?>> getAvailableEntityTypes() {
        return entityTypesToCountMap.keySet()
            .stream()
            .filter(key -> entityTypesToCountMap.getOrDefault(key, 0) > 0)
            .toList();
    }

    public Map<EntityType<?>, Integer> getBackingMap() {
        return Collections.unmodifiableMap(entityTypesToCountMap);
    }
}
