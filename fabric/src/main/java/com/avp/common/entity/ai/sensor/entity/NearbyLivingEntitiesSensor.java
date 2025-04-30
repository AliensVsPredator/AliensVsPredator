package com.avp.common.entity.ai.sensor.entity;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class NearbyLivingEntitiesSensor implements GOAPSensor<LivingEntity> {

    public static final NearbyLivingEntitiesSensor INSTANCE = new NearbyLivingEntitiesSensor();

    private NearbyLivingEntitiesSensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        var nearbyEntities = worldState.getOrDefault(GOAPConstants.NEARBY_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEARBY_LIVING_ENTITIES,
            nearbyEntities.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .toList()
        );
    }
}
