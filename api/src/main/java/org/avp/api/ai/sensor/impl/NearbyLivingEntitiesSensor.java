package org.avp.api.ai.sensor.impl;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.ai.GOAPBrainCache;
import org.avp.api.ai.sensor.key.SensorKeys;
import org.avp.api.ai.sensor.Sensor;

public class NearbyLivingEntitiesSensor extends Sensor {

    public NearbyLivingEntitiesSensor(GOAPBrainCache goapBrainCache) {
        super(goapBrainCache);
    }

    @Override
    public void sense() {
        var nearbyEntitiesOptional = goapBrainCache.get(SensorKeys.NEARBY_ENTITIES);

        nearbyEntitiesOptional.ifPresent(nearbyEntities -> {
            var livingEntities = nearbyEntities.stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .toList();
            goapBrainCache.cache(SensorKeys.NEARBY_LIVING_ENTITIES, livingEntities);
        });
    }
}
