package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.just.goap.StateKey;
import com.just.goap.sensor.Sensor;

import com.avp.common.registry.tag.AVPBiomeTags;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class MarineGOAPSensors {

    public static final Sensor.Mono<Marine, Collection<ArmorIntent>> ARMOR_INTENT = Sensor.map(
        StateKey.sensed("armor_intent"),
        marine -> {
            var armorIntentSet = EnumSet.noneOf(ArmorIntent.class);

            // TODO: Fix this check to check nearby blocks.
            if (marine.level().getBiome(marine.blockPosition()).is(AVPBiomeTags.IS_IRRADIATED)) {
                armorIntentSet.add(ArmorIntent.RADIATION_PROTECTION);
            }

            if (marine.isOnFire()) {
                armorIntentSet.add(ArmorIntent.FIRE_PROTECTION);
            }

            if (marine.isUnderWater()) {
                armorIntentSet.add(ArmorIntent.DROWNING_PROTECTION);
            }

            if (armorIntentSet.isEmpty()) {
                armorIntentSet.add(ArmorIntent.BEST_DEFENSE);
            }

            return armorIntentSet;
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
