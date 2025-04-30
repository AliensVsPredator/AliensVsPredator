package com.avp.fabric.common.entity.ai.sensor.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.fabric.common.entity.ai.GOAPConstants;
import com.avp.fabric.goap.GOAPSensor;
import com.avp.fabric.goap.state.GOAPMutableWorldState;

public class NearbyFoodItemEntitiesSensor implements GOAPSensor<LivingEntity> {

    public static final NearbyFoodItemEntitiesSensor INSTANCE = new NearbyFoodItemEntitiesSensor();

    private NearbyFoodItemEntitiesSensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        var nearbyItemEntities = worldState.getOrDefault(GOAPConstants.NEARBY_ITEM_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEARBY_FOOD_ITEM_ENTITIES,
            nearbyItemEntities.stream()
                .filter(entity -> entity.getItem().getComponents().has(DataComponents.FOOD))
                .toList()
        );
    }
}
