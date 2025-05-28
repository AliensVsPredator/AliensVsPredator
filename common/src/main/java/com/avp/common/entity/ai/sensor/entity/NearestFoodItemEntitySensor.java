package com.avp.common.entity.ai.sensor.entity;

import com.bvanseg.just.functional.option.Option;
import com.xlib.goap.GOAPSensor;
import com.xlib.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Comparator;
import java.util.List;

import com.avp.common.entity.ai.GOAPConstants;

public class NearestFoodItemEntitySensor implements GOAPSensor<LivingEntity> {

    public static final NearestFoodItemEntitySensor INSTANCE = new NearestFoodItemEntitySensor();

    private NearestFoodItemEntitySensor() {}

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        var nearbyFoodItemEntities = worldState.getOrDefault(GOAPConstants.NEARBY_FOOD_ITEM_ENTITIES, List.of());

        worldState.set(
            GOAPConstants.NEAREST_FOOD_ITEM_ENTITY,
            Option.ofNullable(
                nearbyFoodItemEntities.stream()
                    .min(Comparator.comparingDouble((ItemEntity target) -> target.distanceTo(context)))
                    .orElse(null)
            )
        );
    }
}
