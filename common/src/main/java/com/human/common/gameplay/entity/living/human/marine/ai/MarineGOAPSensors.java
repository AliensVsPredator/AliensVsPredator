package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;

import com.avp.common.registry.tag.AVPBiomeTags;

public class MarineGOAPSensors {

    // TODO: There are cases where there may be multiple intents. Ex. if in a nuclear biome and underwater.
    public static final Sensor.Mono<Marine, ArmorIntent> ARMOR_INTENT = Sensor.map(
        StateKey.sensed("armor_intent"),
        marine -> {
            // TODO: Fix this check to check nearby blocks.
            if (marine.level().getBiome(marine.blockPosition()).is(AVPBiomeTags.IS_IRRADIATED)) {
                return ArmorIntent.RADIATION_PROTECTION;
            }

            if (marine.isOnFire()) {
                return ArmorIntent.FIRE_PROTECTION;
            }

            if (marine.isUnderWater()) {
                return ArmorIntent.DROWNING_PROTECTION;
            }

            return ArmorIntent.BEST_DEFENSE;
        }
    );

    public static final Sensor.Mono<Marine, Boolean> IS_CURRENT_BLOCK_POS_REPLACEABLE = Sensor.map(
        StateKey.sensed("is_current_block_pos_replaceable"),
        marine -> marine.level().getBlockState(marine.blockPosition()).canBeReplaced()
    );

    private MarineGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
