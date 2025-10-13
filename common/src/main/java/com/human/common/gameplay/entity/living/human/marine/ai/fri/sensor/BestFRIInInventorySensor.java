package com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import com.avp.common.model.inventory.AVPInventory;

public class BestFRIInInventorySensor {

    public static final StateKey.Sensed<Option<ItemTarget.Inventory>> KEY = StateKey.sensed("best_fri_in_inventory");

    public static @NotNull Map<StateKey<?>, Option<ItemTarget.Inventory>> sense(Marine marine, ReadableWorldState worldState) {
        var bestScore = Double.MIN_VALUE;
        AVPInventory.Entry bestEntry = null;
        FRIStrategy bestStrategy = null;

        for (var strategy : FRIStrategies.STRATEGIES) {
            if (!strategy.isValid(marine, worldState)) {
                continue;
            }

            var entries = strategy.selectEntriesFromInventory(marine.getInventory());

            for (var entry : entries) {
                var itemStack = entry.copyItemStack();

                if (!strategy.canUseItemStack(itemStack)) {
                    continue;
                }

                var newScore = strategy.score(marine, worldState, itemStack);

                if (newScore > bestScore) {
                    bestScore = newScore;
                    bestEntry = entry;
                    bestStrategy = strategy;
                }
            }
        }

        Option<ItemTarget.Inventory> itemTarget = bestEntry == null
            ? Option.none()
            : Option.some(new ItemTarget.Inventory(bestEntry, bestScore, bestStrategy));

        return Map.of(KEY, itemTarget);
    }
}
