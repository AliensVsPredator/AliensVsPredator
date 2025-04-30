package com.avp.fabric.common.entity.ai.sensor.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.goap.GOAPSensor;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.fabric.common.entity.ai.GOAPConstants;
import com.avp.fabric.common.util.AVPPredicates;

public class NearbyEntitiesSensor implements GOAPSensor<LivingEntity> {

    public static final NearbyEntitiesSensor INSTANCE = new NearbyEntitiesSensor();

    private NearbyEntitiesSensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        worldState.set(
            GOAPConstants.NEARBY_ENTITIES,
            context.level()
                .getEntitiesOfClass(Entity.class, context.getBoundingBox().inflate(16), AVPPredicates.alwaysTrue())
        );
    }
}
