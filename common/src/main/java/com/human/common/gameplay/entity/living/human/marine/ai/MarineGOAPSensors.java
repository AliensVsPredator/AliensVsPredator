package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.goap.Sensor;
import net.minecraft.world.item.Items;

public class MarineGOAPSensors {

    public static final Sensor<Marine, Boolean> HAS_WATER_BUCKET = Sensor.direct(
        MarineGOAPStateKeys.HAS_WATER_BUCKET,
        marine -> marine.getMainHandItem().is(Items.WATER_BUCKET) || marine.getInventory().hasItem(Items.WATER_BUCKET)
    );

    public static final Sensor<Marine, Boolean> IS_CURRENT_BLOCK_POS_REPLACEABLE = Sensor.direct(
        MarineGOAPStateKeys.IS_CURRENT_BLOCK_POS_REPLACEABLE,
        marine -> marine.level().getBlockState(marine.blockPosition()).canBeReplaced()
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
