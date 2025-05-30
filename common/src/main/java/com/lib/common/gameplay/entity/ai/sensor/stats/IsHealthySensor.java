package com.lib.common.gameplay.entity.ai.sensor.stats;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.world.entity.LivingEntity;

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
