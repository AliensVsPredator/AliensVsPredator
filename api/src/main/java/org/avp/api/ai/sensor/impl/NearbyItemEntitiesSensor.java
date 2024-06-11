package org.avp.api.ai.sensor.impl;

import net.minecraft.world.entity.item.ItemEntity;
import org.avp.api.ai.GOAPBrainCache;
import org.avp.api.ai.sensor.key.SensorKeys;
import org.avp.api.ai.sensor.Sensor;

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
