package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategyResult;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;

public class BestFRISensor {

    public static final StateKey.Sensed<Option<FRIStrategyResult<? extends ItemTarget>>> KEY = StateKey.sensed("best_fri");

    public static Option<FRIStrategyResult<? extends ItemTarget>> sense(LivingEntity ignored, ReadableWorldState worldState) {
        var handsOption = worldState.getOrDefault(BestFRIInHandsSensor.KEY, Option.none());
        var inventoryOption = worldState.getOrDefault(BestFRIInInventorySensor.KEY, Option.none());
        var worldOption = worldState.getOrDefault(BestFRIInWorldSensor.KEY, Option.none());

        Option<FRIStrategyResult<? extends ItemTarget>> bestOption = Option.none();

        // Find the best scoring target among hands, inventory, and world
        for (var itemTargetOption : new Option[] { handsOption, inventoryOption, worldOption }) {
            if (itemTargetOption.isSome()) {
                var candidate = (FRIStrategyResult<? extends ItemTarget>) itemTargetOption.unwrap();

                if (bestOption.isNone() || candidate.score() > bestOption.unwrap().score()) {
                    bestOption = Option.some(candidate);
                }
            }
        }

        return bestOption;
    }
}
