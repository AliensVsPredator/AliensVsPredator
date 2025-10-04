package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DrinkablePotionFireResistanceItemStrategy implements FireResistanceItemStrategy {

    @Override
    public boolean matches(ItemStack itemStack) {
        return itemStack.is(Items.POTION)
            && FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public double score(Marine marine, Context context, ItemStack itemStack, Weights weights) {
        var ticks = FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack);

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(ticks);
        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(marine, itemStack));
        var waste = FireResistanceItemStrategyUtil.overlapWasteTerm(context, ticks);

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste;
    }

    @Override
    public Action.Result consume(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        return ConsumeItemAction.perform(SoundEvents.GENERIC_DRINK, marine, blackboard, () -> onConsume(marine));
    }

    private static Action.@NotNull Result onConsume(Marine marine) {
        var itemStack = marine.getMainHandItem();

        if (!itemStack.is(Items.POTION)) {
            return Action.Result.FAILED;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        itemStack.shrink(1);

        potionContents.getAllEffects().forEach(marine::addEffect);
        return Action.Result.CONTINUE;
    }
}
