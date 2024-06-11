package org.avp.api.ai.sensor.impl;

import net.minecraft.world.entity.Entity;
import org.avp.api.ai.GOAPBrainCache;
import org.avp.api.ai.sensor.Sensor;
import org.avp.api.ai.sensor.key.SensorKeys;

public class NearbyEntitiesSensor extends Sensor {

    private final Entity entity;

    public NearbyEntitiesSensor(GOAPBrainCache goapBrainCache, Entity entity) {
        super(goapBrainCache);
        this.entity = entity;
    }

    @Override
    public void sense() {
        if (isDisabled()) {
            return;
        }

        var nearbyEntities = entity.level().getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(16));
        goapBrainCache.cache(SensorKeys.NEARBY_ENTITIES, nearbyEntities);
    }
}
