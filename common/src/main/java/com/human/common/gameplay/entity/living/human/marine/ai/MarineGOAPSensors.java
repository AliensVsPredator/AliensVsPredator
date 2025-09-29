package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.PartialArmorSet;
import com.human.common.gameplay.entity.living.human.marine.ai.sensor.BestArmorSetSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.sensor.BestWaterbreathingArmorSetSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.sensor.CurrentArmorSetSensor;
import com.just.core.functional.option.Option;
import com.just.goap.Sensor;

public class MarineGOAPSensors {

    public static final Sensor<Marine, Option<PartialArmorSet>> BEST_ARMOR_SET = Sensor.derived(
        MarineGOAPKeys.BEST_ARMOR_SET,
        MarineGOAPKeys.CURRENT_ARMOR_SET,
        BestArmorSetSensor::sense
    );

    public static final Sensor<Marine, Option<PartialArmorSet>> BEST_WATER_BREATHING_ARMOR_SET = Sensor.derived(
        MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET,
        MarineGOAPKeys.CURRENT_ARMOR_SET,
        BestWaterbreathingArmorSetSensor::sense
    );

    public static final Sensor<Marine, PartialArmorSet> CURRENT_ARMOR_SET = Sensor.direct(
        MarineGOAPKeys.CURRENT_ARMOR_SET,
        CurrentArmorSetSensor::sense
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
