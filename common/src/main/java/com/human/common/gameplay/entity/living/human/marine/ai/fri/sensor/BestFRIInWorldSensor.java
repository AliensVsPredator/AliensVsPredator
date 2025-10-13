package com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class BestFRIInWorldSensor {

    public static final StateKey.Sensed<Option<ItemTarget.World>> KEY = StateKey.sensed("best_fri_in_world");

    public static @NotNull Map<StateKey<?>, Option<ItemTarget.World>> sense(Marine marine, ReadableWorldState worldState) {
        var bestScore = Double.MIN_VALUE;
        ItemEntity bestItemEntity = null;
        FRIStrategy bestStrategy = null;

        for (var strategy : FRIStrategies.STRATEGIES) {
            if (!strategy.isValid(marine, worldState)) {
                continue;
            }

            var itemEntities = worldState.getOrDefault(GOAPSensors.NEARBY_ITEM_ENTITIES.key(), List.of());

            for (var entry : itemEntities) {
                var itemStack = entry.getItem();

                if (!strategy.canUseItemStack(itemStack)) {
                    continue;
                }
                
                var newScore = strategy.score(marine, worldState, itemStack);

                if (newScore > bestScore) {
                    bestScore = newScore;
                    bestItemEntity = entry;
                    bestStrategy = strategy;
                }
            }
        }

        Option<ItemTarget.World> itemTarget = bestItemEntity == null
            ? Option.none()
            : Option.some(new ItemTarget.World(bestItemEntity, bestScore, bestStrategy));

        return Map.of(KEY, itemTarget);
    }
}
