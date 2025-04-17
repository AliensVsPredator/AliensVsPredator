package com.avp.common.entity.ai.sensor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.util.AVPPredicates;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class NearbyEntitySensor<T extends LivingEntity> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearbyEntities = context.level()
            .getEntitiesOfClass(Entity.class, context.getBoundingBox().inflate(16), AVPPredicates.alwaysTrue());
        worldState.set(GOAPConstants.NEARBY_ENTITIES, nearbyEntities);
    }
}
