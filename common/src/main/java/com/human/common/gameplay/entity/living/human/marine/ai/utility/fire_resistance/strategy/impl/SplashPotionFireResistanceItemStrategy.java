package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SplashPotionItem;

import java.util.Objects;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class SplashPotionFireResistanceItemStrategy<T extends LivingEntity & AVPInventoryHolder> implements FireResistanceItemStrategy<T> {

    @Override
    public boolean matches(ItemStack itemStack) {
        return itemStack.getItem() instanceof SplashPotionItem
            && FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public double score(ItemStack itemStack, Context<T> context) {
        var livingEntity = context.getLivingEntity();
        var weights = context.getWeights();
        var base = FireResistanceItemStrategyUtil.fireResistanceTicksFromStack(itemStack);
        var ticks = (int) Math.round(base * 0.75);

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(ticks);
        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(livingEntity, itemStack));
        var waste = FireResistanceItemStrategyUtil.overlapWasteTerm(context, ticks);

        // Slight handling penalty (aiming, spread).
        var handlingPenalty = 0.05;

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste
            - handlingPenalty;
    }

    @Override
    public Action.Signal execute(Context<T> context) {
        var livingEntity = context.getLivingEntity();
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.SPLASH_POTION)) {
            return Action.Signal.ABORT;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        itemStack.shrink(1);

        var level = livingEntity.level();
        var thrownPotion = new ThrownPotion(level, livingEntity);

        thrownPotion.setItem(itemStack);
        thrownPotion.shoot(0.0, -1.0, 0.0, 0.5F, 1.0F);
        level.addFreshEntity(thrownPotion);

        potionContents.getAllEffects().forEach(livingEntity::addEffect);

        return Action.Signal.CONTINUE;
    }
}
