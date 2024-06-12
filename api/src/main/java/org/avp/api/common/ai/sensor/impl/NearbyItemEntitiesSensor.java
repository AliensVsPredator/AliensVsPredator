package org.avp.api.common.ai.sensor.impl;

import net.minecraft.world.entity.item.ItemEntity;

import org.avp.api.common.ai.GOAPBrainCache;
import org.avp.api.common.ai.sensor.Sensor;
import org.avp.api.common.ai.sensor.key.SensorKeys;

public class NearbyItemEntitiesSensor extends Sensor {

    public NearbyItemEntitiesSensor(GOAPBrainCache goapBrainCache) {
        super(goapBrainCache);
    }

    @Override
    public void sense() {
        var nearbyEntitiesOptional = goapBrainCache.get(SensorKeys.NEARBY_ENTITIES);

        nearbyEntitiesOptional.ifPresent(nearbyEntities -> {
            var itemEntities = nearbyEntities.stream()
                .filter(ItemEntity.class::isInstance)
                .map(ItemEntity.class::cast)
                .toList();
            goapBrainCache.cache(SensorKeys.NEARBY_ITEM_ENTITIES, itemEntities);
        });
    }
}
