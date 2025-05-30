package com.lib.common.gameplay.entity.ai.sensor.entity;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

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
