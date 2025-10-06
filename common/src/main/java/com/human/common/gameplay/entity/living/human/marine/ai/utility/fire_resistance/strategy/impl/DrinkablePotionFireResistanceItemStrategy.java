package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.avp.common.model.inventory.AVPInventoryHolder;
import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DrinkablePotionFireResistanceItemStrategy<T extends LivingEntity & AVPInventoryHolder> implements FireResistanceItemStrategy<T> {

    @Override
    public boolean matches(ItemStack itemStack) {
        return itemStack.is(Items.POTION)
            && FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public double score(ItemStack itemStack, Context<T> context) {
        var livingEntity = context.getLivingEntity();
        var ticks = FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack);
        var weights = context.getWeights();

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(ticks);
        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(livingEntity, itemStack));
        var waste = FireResistanceItemStrategyUtil.overlapWasteTerm(context, ticks);

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste;
    }

    @Override
    public Action.Result execute(Context<T> context) {
        var blackboard = context.getBlackboard();
        var livingEntity = context.getLivingEntity();
        return ConsumeItemAction.perform(SoundEvents.GENERIC_DRINK, livingEntity, blackboard, () -> onConsume(livingEntity));
    }

    private static Action.@NotNull Result onConsume(LivingEntity livingEntity) {
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.POTION)) {
            return Action.Result.FAILED;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        itemStack.shrink(1);

        potionContents.getAllEffects().forEach(livingEntity::addEffect);
        return Action.Result.CONTINUE;
    }
}
