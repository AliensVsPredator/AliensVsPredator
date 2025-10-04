package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class LingeringPotionFireResistanceItemStrategy implements FireResistanceItemStrategy {

    @Override
    public boolean matches(ItemStack itemStack) {
        return itemStack.is(Items.LINGERING_POTION)
            && FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public double score(Marine marine, Context context, ItemStack itemStack, Weights weights) {
        // Lingering ~ 1/4 duration per full exposure if you stick in the cloud.
        var base = FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack);
        var ticks = (int) Math.round(base * 0.25);

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(ticks);
        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(marine, itemStack));
        var waste = FireResistanceItemStrategyUtil.overlapWasteTerm(context, ticks);

        // Cloud unreliability penalty.
        var unreliability = 0.12;

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste
            - unreliability;
    }

    @Override
    public Action.Result consume(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        var itemStack = marine.getMainHandItem();

        if (!itemStack.is(Items.LINGERING_POTION)) {
            return Action.Result.FAILED;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        itemStack.shrink(1);

        var level = marine.level();
        var thrownPotion = new ThrownPotion(level, marine);

        thrownPotion.setItem(itemStack);
        thrownPotion.shoot(0.0, -1.0, 0.0, 0.5F, 1.0F);
        level.addFreshEntity(thrownPotion);

        potionContents.getAllEffects().forEach(marine::addEffect);

        return Action.Result.CONTINUE;
    }
}
