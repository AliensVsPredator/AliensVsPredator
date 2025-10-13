package com.human.common.gameplay.entity.living.human.marine.ai.fri.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BestFRIInHandsSensor {

    public static final StateKey.Sensed<Option<ItemTarget.Hands>> KEY = StateKey.sensed("best_fri_in_hands");

    public static @NotNull Map<StateKey<?>, Option<ItemTarget.Hands>> sense(Marine marine, ReadableWorldState worldState) {
        var bestScore = Double.MIN_VALUE;
        InteractionHand bestHand = null;
        FRIStrategy bestStrategy = null;

        for (var interactionHand : InteractionHand.values()) {
            var itemStack = marine.getItemInHand(interactionHand);

            for (var strategy : FRIStrategies.STRATEGIES) {
                if (!strategy.canUseItemStack(itemStack) || !strategy.isValid(marine, worldState)) {
                    continue;
                }

                var newScore = strategy.score(marine, worldState, itemStack);

                if (newScore > bestScore) {
                    bestScore = newScore;
                    bestHand = interactionHand;
                    bestStrategy = strategy;
                }
            }
        }

        Option<ItemTarget.Hands> itemTarget = bestHand == null
            ? Option.none()
            : Option.some(new ItemTarget.Hands(bestHand, bestScore, bestStrategy));

        return Map.of(KEY, itemTarget);
    }
}
