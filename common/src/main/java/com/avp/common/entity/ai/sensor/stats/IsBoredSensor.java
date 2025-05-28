package com.avp.common.entity.ai.sensor.stats;

import com.xlib.goap.GOAPSensor;
import com.xlib.goap.state.GOAPMutableWorldState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;

public class IsBoredSensor implements GOAPSensor<LivingEntity> {

    private int boredom;

    public IsBoredSensor(RandomSource randomSource) {
        this.boredom = computeBoredom(randomSource);
    }

    @Override
    public void sense(LivingEntity context, GOAPMutableWorldState worldState) {
        this.boredom--;

        worldState.set(GOAPConstants.IS_BORED, boredom < 0);

        if (boredom < 0) {
            this.boredom = computeBoredom(context.getRandom());
        }
    }

    protected int computeBoredom(RandomSource randomSource) {
        return (5 * 20) + (randomSource.nextInt(5) * 20);
    }
}
