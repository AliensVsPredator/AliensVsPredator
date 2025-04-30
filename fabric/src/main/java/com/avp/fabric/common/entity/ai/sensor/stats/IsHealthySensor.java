package com.avp.fabric.common.entity.ai.sensor.stats;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.goap.GOAPSensor;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.fabric.common.entity.ai.GOAPConstants;

public class IsHealthySensor<T extends LivingEntity> implements GOAPSensor<T> {

    private final float normalizedPercentThreshold;

    public IsHealthySensor(float normalizedPercentThreshold) {
        this.normalizedPercentThreshold = normalizedPercentThreshold;
    }

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(GOAPConstants.IS_HEALTHY, context.getHealth() > context.getMaxHealth() * normalizedPercentThreshold);
    }
}
