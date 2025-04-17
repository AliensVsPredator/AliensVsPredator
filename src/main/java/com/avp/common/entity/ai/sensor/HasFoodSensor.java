package com.avp.common.entity.ai.sensor;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class HasFoodSensor<T extends LivingEntity> implements GOAPSensor<T> {

    private final Predicate<T> hasFood;

    public HasFoodSensor(Predicate<T> hasFood) {
        this.hasFood = hasFood;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(GOAPConstants.HAS_FOOD, hasFood.test(context));
    }
}
