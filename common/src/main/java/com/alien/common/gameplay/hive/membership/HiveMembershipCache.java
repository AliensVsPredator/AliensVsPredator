package com.alien.common.gameplay.hive.membership;

import com.alien.common.model.hive.HiveMemberData;
import com.just.core.cache.Cache;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.avp.AVP;

public class HiveMembershipCache extends Cache<UUID, HiveMemberData> {

    private final Map<EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> hiveMemberDataByEntityTypeMap;

    public HiveMembershipCache() {
        this.hiveMemberDataByEntityTypeMap = new HashMap<>();
    }

    public Map<EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> getMembersByEntityTypeMap() {
        return Collections.unmodifiableMap(hiveMemberDataByEntityTypeMap);
    }

    public List<Map.Entry<UUID, HiveMemberData>> getMembersByEntityType(EntityType<?> entityType) {
        return hiveMemberDataByEntityTypeMap.getOrDefault(entityType, List.of());
    }

    @Override
    protected void onAddToCache(UUID uuid, @Nullable HiveMemberData oldValue, HiveMemberData newValue) {
        var entityType = newValue.getEntityType().unwrapOr(null);

        if (entityType == null) {
            AVP.LOGGER.warn("onAddToCache - Invalid entity type. Resource Location: {}", newValue.entityTypeResourceLocation());
            return;
        }

        var list = hiveMemberDataByEntityTypeMap.computeIfAbsent(entityType, $ -> new ArrayList<>());

        if (oldValue != null) {
            var oldEntry = Map.entry(uuid, oldValue);
            list.remove(oldEntry);
        }

        var newEntry = Map.entry(uuid, newValue);
        list.add(newEntry);
    }

    @Override
    protected void onRemoveFromCache(UUID uuid, HiveMemberData value) {
        var entityType = value.getEntityType().unwrapOr(null);

        if (entityType == null) {
            AVP.LOGGER.warn("onRemoveFromCache - Invalid entity type. Resource Location: {}", value.entityTypeResourceLocation());
            return;
        }

        var entry = Map.entry(uuid, value);

        hiveMemberDataByEntityTypeMap.computeIfAbsent(entityType, $ -> new ArrayList<>())
            .remove(entry);
    }

    @Override
    protected void onClearCache() {
        super.onClearCache();
        hiveMemberDataByEntityTypeMap.clear();
    }
}
