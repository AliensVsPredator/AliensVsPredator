package com.lib.common.gameplay.entity.ai.sensor.stats;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

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
