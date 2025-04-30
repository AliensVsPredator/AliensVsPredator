package com.avp.fabric.common.entity.ai.sensor.stats;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.goap.GOAPSensor;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.fabric.common.entity.ai.GOAPConstants;

public class IsBoredSensor implements GOAPSensor<LivingEntity> {

    private int boredom;

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        this.boredom--;

        worldState.set(GOAPConstants.IS_BORED, boredom < 0);

        if (boredom < 0) {
            this.boredom = (5 * 20) + (context.getRandom().nextInt(5) * 20);
        }
    }
}
