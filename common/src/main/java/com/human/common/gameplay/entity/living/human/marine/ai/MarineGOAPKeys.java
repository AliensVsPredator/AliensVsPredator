package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.ai.model.PartialArmorSet;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;

public class MarineGOAPKeys {

    public static final StateKey.Sensed<Option<PartialArmorSet>> BEST_ARMOR_SET = StateKey.sensed("best_armor_set");

    public static final StateKey.Sensed<Option<PartialArmorSet>> BEST_WATER_BREATHING_ARMOR_SET = StateKey.sensed(
        "best_water_breathing_armor_set"
    );

    public static final StateKey.Sensed<PartialArmorSet> CURRENT_ARMOR_SET = StateKey.sensed("current_armor_set");

    private MarineGOAPKeys() {
        throw new UnsupportedOperationException();
    }
}
