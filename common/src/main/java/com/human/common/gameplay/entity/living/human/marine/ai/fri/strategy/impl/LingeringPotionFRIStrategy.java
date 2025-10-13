package com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategyUtil;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.Objects;

import com.avp.common.model.inventory.AVPInventory;

public class LingeringPotionFRIStrategy implements FRIStrategy {

    @Override
    public boolean canUseItemStack(ItemStack itemStack) {
        return itemStack.is(Items.LINGERING_POTION) && FRIStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public boolean isValid(LivingEntity livingEntity, ReadableWorldState worldState) {
        return worldState.getOrDefault(GOAPSensors.IS_ON_GROUND.key(), false);
    }

    @Override
    public Collection<AVPInventory.Entry> selectEntriesFromInventory(AVPInventory inventory) {
        return inventory.selectEntries(Items.LINGERING_POTION)
            .stream()
            .filter(entry -> FRIStrategyUtil.fireResistanceTicksFromStack(entry) > 0)
            .toList();
    }

    @Override
    public double score(LivingEntity livingEntity, ReadableWorldState worldState, ItemStack itemStack) {
        var healthRatio = worldState.getOrDefault(GOAPSensors.HEALTH_RATIO.key(), 1.0F);
        var isOnFire = worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false);
        var fireResTicksRemaining = worldState.getOrDefault(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS.key(), 0);
        var weights = FRIStrategy.Weights.DEFAULT;
        // Lingering ~ 1/4 duration per full exposure if you stick in the cloud.
        var base = FRIStrategyUtil.fireResistanceTicksFromStack(itemStack);
        var ticks = (int) Math.round(base * 0.25);

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FRIStrategyUtil.urgencyTerm(isOnFire, healthRatio);
        var D = FRIStrategyUtil.durationTermSeconds(ticks);
        var use = FRIStrategyUtil.timePenalty(itemStack.getUseDuration(livingEntity));
        var waste = FRIStrategyUtil.overlapWasteTerm(fireResTicksRemaining, ticks);

        // Cloud unreliability penalty.
        var unreliability = 0.12;

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste
            - unreliability;
    }

    @Override
    public Action.Signal execute(LivingEntity livingEntity, ReadableWorldState worldState, Blackboard blackboard) {
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.LINGERING_POTION)) {
            return Action.Signal.ABORT;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        var level = livingEntity.level();
        var thrownPotion = new ThrownPotion(level, livingEntity);

        thrownPotion.setItem(itemStack);
        thrownPotion.shoot(0.0, -1.0, 0.0, 0.5F, 1.0F);
        level.addFreshEntity(thrownPotion);

        potionContents.getAllEffects().forEach(livingEntity::addEffect);

        itemStack.shrink(1);

        return Action.Signal.CONTINUE;
    }
}
