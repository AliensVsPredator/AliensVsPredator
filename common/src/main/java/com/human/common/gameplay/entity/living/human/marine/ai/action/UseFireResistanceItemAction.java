package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

import com.avp.common.model.inventory.AVPInventory;

public class UseFireResistanceItemAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(UseFireResistanceItemAction.class);

    public static Action.@NotNull Result perform(
        Function<ItemStack, Option<FireResistanceItemStrategy>> strategySelector,
        Marine marine,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var mainHandItem = marine.getMainHandItem();
        var strategyOption = strategySelector.apply(mainHandItem);

        if (strategyOption.isNone()) {
            return Action.Result.FAILED;
        }

        return strategyOption.unwrap().consume(marine, worldState, blackboard);
    }

    public static @Nullable Score getBestScore(
        StateKey<List<AVPInventory.Entry>> fireResistanceItemEntriesKey,
        Function<ItemStack, Option<FireResistanceItemStrategy>> strategySelector,
        Marine marine,
        ReadableWorldState worldState
    ) {
        var entries = worldState.getOrDefault(fireResistanceItemEntriesKey, List.of());

        if (entries.isEmpty()) {
            return null;
        }

        // Build context for scoring.
        var isOnFire = worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false);
        var healthRatio = worldState.getOrDefault(GOAPSensors.HEALTH_RATIO.key(), 1.0F);
        var fireResTicksRemaining = worldState.getOrDefault(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS.key(), 0);

        var context = new FireResistanceItemStrategy.Context(isOnFire, healthRatio, fireResTicksRemaining);

        var weights = FireResistanceItemStrategy.Weights.DEFAULT;

        Score best = null;

        for (var entry : entries) {
            var itemStack = entry.copyItemStack();
            var fireResistanceStrategyOption = strategySelector.apply(itemStack);

            // This should always be present because the sensor uses the same registry.
            if (fireResistanceStrategyOption.isNone()) {
                LOGGER.error("No fire resistance item strategy for stack: {}", itemStack.getDisplayName().getString());
                continue;
            }

            var strat = fireResistanceStrategyOption.unwrap();
            var score = strat.score(marine, context, itemStack, weights);

            if (Double.isFinite(score)) {
                if (best == null || score > best.score) {
                    best = new Score(strat, entry, score);
                }
            }
        }

        return best;
    }

    public record Score(
        FireResistanceItemStrategy strategy,
        AVPInventory.Entry entry,
        double score
    ) {}

    private UseFireResistanceItemAction() {
        throw new UnsupportedOperationException();
    }
}
