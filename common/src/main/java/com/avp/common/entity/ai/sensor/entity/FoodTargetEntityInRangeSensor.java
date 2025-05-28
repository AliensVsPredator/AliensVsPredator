package com.avp.common.entity.ai.sensor.entity;

import com.bvanseg.just.functional.option.None;
import com.bvanseg.just.functional.option.Option;
import com.bvanseg.just.functional.option.Some;
import com.xlib.goap.GOAPSensor;
import com.xlib.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.function.BiPredicate;

import com.avp.common.entity.ai.GOAPConstants;

public class FoodTargetEntityInRangeSensor<T extends LivingEntity> implements GOAPSensor<T> {

    private final BiPredicate<T, Double> distanceSqrToTargetPredicate;

    public FoodTargetEntityInRangeSensor(BiPredicate<T, Double> distanceSqrToTargetPredicate) {
        this.distanceSqrToTargetPredicate = distanceSqrToTargetPredicate;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var nearestTargetOption = worldState.getOrDefault(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none());

        switch (nearestTargetOption) {
            case None<? extends ItemEntity> target -> { /* NO-OP */ }
            case Some<? extends ItemEntity> some -> worldState.set(
                GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE,
                distanceSqrToTargetPredicate.test(context, context.distanceToSqr(some.unwrap()))
            );
        }
    }
}
