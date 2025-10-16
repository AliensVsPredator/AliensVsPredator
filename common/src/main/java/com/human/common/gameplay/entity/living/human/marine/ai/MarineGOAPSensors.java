package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import net.minecraft.world.entity.Entity;

public class MarineGOAPSensors {

    public static final Sensor.Mono<Entity, Boolean> IS_CURRENT_BLOCK_POS_REPLACEABLE = Sensors.map(
        StateKey.sensed("is_current_block_pos_replaceable"),
        entity -> entity.level().getBlockState(entity.blockPosition()).canBeReplaced()
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
