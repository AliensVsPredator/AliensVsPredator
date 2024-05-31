package org.avp.api.entity.ai.sensor.impl;

import net.minecraft.world.entity.item.ItemEntity;
import org.avp.api.entity.ai.GOAPBrainCache;
import org.avp.api.entity.ai.sensor.Sensor;
import org.avp.api.entity.ai.sensor.key.SensorKeys;

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
