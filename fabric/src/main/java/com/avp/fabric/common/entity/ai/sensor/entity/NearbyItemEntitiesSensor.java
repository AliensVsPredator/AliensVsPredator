package com.avp.fabric.common.entity.ai.sensor.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;

import com.avp.common.goap.GOAPSensor;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.fabric.common.entity.ai.GOAPConstants;

public class NearbyItemEntitiesSensor implements GOAPSensor<LivingEntity> {

    public static final NearbyItemEntitiesSensor INSTANCE = new NearbyItemEntitiesSensor();

    private NearbyItemEntitiesSensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        var nearbyEntities = worldState.getOrDefault(GOAPConstants.NEARBY_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEARBY_ITEM_ENTITIES,
            nearbyEntities.stream()
                .filter(entity -> entity instanceof ItemEntity)
                .map(entity -> (ItemEntity) entity)
                .toList()
        );
    }
}
