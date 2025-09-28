package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.sensor.BestArmorSetSensor;
import com.human.common.gameplay.entity.living.human.marine.ai.sensor.CurrentArmorSetSensor;
import com.just.core.functional.option.Option;
import com.just.goap.Sensor;

public class MarineGOAPSensors {

    public static final Sensor<Marine, ArmorSet> CURRENT_ARMOR_SET = Sensor.direct(
        MarineGOAPKeys.CURRENT_ARMOR_SET,
        CurrentArmorSetSensor::sense
    );

    public static final Sensor<Marine, Option<ArmorSet>> BEST_ARMOR_SET = Sensor.derived(
        MarineGOAPKeys.BEST_ARMOR_SET,
        MarineGOAPKeys.CURRENT_ARMOR_SET,
        BestArmorSetSensor::sense
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
