package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.just.goap.StateKey;

public class MarineGOAPStateKeys {

    public static final StateKey.Sensed<ArmorIntent> ARMOR_INTENT = StateKey.sensed("armor_intent");

    public static final StateKey.Sensed<Boolean> HAS_WATER_BUCKET = StateKey.sensed("has_water_bucket");

    public static final StateKey.Sensed<Boolean> IS_CURRENT_BLOCK_POS_REPLACEABLE = StateKey.sensed("is_current_block_pos_replaceable");

    private MarineGOAPStateKeys() {
        throw new UnsupportedOperationException();
    }
}
