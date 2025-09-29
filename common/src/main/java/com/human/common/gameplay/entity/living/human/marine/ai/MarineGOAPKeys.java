package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.ai.model.PartialArmorSet;
import com.just.core.functional.option.Option;
import com.just.goap.GOAPKey;

public class MarineGOAPKeys {

    public static final GOAPKey<Option<PartialArmorSet>> BEST_ARMOR_SET = new GOAPKey<>("best_armor_set");

    public static final GOAPKey<Option<PartialArmorSet>> BEST_WATER_BREATHING_ARMOR_SET = new GOAPKey<>("best_water_breathing_armor_set");

    public static final GOAPKey<PartialArmorSet> CURRENT_ARMOR_SET = new GOAPKey<>("current_armor_set");

    private MarineGOAPKeys() {
        throw new UnsupportedOperationException();
    }
}
