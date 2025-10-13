package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;

import java.util.Map;

public class BestFRISensor {

    public static final StateKey.Sensed<Option<? extends ItemTarget<FRIStrategy>>> KEY = StateKey.sensed("best_fri");

    public static Map<StateKey<?>, ?> sense(Marine marine, ReadableWorldState worldState) {
        var handsOption = worldState.getOrDefault(BestFRIInHandsSensor.KEY, Option.none());
        var inventoryOption = worldState.getOrDefault(BestFRIInInventorySensor.KEY, Option.none());
        var worldOption = worldState.getOrDefault(BestFRIInWorldSensor.KEY, Option.none());

        Option<? extends ItemTarget<FRIStrategy>> bestOption = Option.none();

        // Find the best scoring target among hands, inventory, and world
        for (var itemTargetOption : new Option[] { handsOption, inventoryOption, worldOption }) {
            if (itemTargetOption.isSome()) {
                @SuppressWarnings("unchecked")
                var candidate = (ItemTarget<FRIStrategy>) itemTargetOption.unwrap();

                if (bestOption.isNone() || candidate.score() > bestOption.unwrap().score()) {
                    bestOption = Option.some(candidate);
                }
            }
        }

        return Map.of(KEY, bestOption);
    }
}
