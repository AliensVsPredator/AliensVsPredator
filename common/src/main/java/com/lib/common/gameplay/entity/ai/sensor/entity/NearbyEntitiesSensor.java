package com.lib.common.gameplay.entity.ai.sensor.entity;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.util.AVPPredicates;

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
