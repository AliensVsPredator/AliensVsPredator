package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.sensor;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.core.functional.option.Option;
import com.just.goap.StateKey;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BestFRIInHandsSensor {

    public static final StateKey.Sensed<Option<ItemTarget.Hands<FRIStrategy>>> KEY = StateKey.sensed("best_fri_in_hands");

    public static @NotNull Option<ItemTarget.Hands<FRIStrategy>> sense(
        LivingEntity livingEntity,
        ReadableWorldState worldState
    ) {
        var bestScore = Double.MIN_VALUE;
        InteractionHand bestHand = null;
        FRIStrategy bestStrategy = null;

        for (var interactionHand : InteractionHand.values()) {
            var itemStack = livingEntity.getItemInHand(interactionHand);

            for (var strategy : FRIStrategies.STRATEGIES) {
                if (!strategy.canUseItemStack(itemStack) || !strategy.isValid(livingEntity, worldState)) {
                    continue;
                }

                var newScore = strategy.score(livingEntity, worldState, itemStack);

                if (newScore > bestScore) {
                    bestScore = newScore;
                    bestHand = interactionHand;
                    bestStrategy = strategy;
                }
            }
        }

        return bestHand == null
            ? Option.none()
            : Option.some(new ItemTarget.Hands<>(bestHand, bestScore, bestStrategy));
    }
}
