package com.alien.common.gameplay.hive.membership;

import com.alien.common.model.hive.HiveMemberData;
import com.lib.common.gameplay.util.Cache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        var entityType = getEntityType(newValue);

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
        var entityType = getEntityType(value);
        var entry = Map.entry(uuid, value);

        hiveMemberDataByEntityTypeMap.computeIfAbsent(entityType, $ -> new ArrayList<>())
            .remove(entry);
    }

    @Override
    protected void onClearCache() {
        super.onClearCache();
        hiveMemberDataByEntityTypeMap.clear();
    }

    private @NotNull EntityType<?> getEntityType(HiveMemberData newValue) {
        return BuiltInRegistries.ENTITY_TYPE.get(newValue.entityType());
    }
}
