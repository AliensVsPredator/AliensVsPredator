package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.FRIStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.Objects;

import com.avp.common.model.inventory.AVPInventory;

public class DrinkablePotionFRIStrategy implements FRIStrategy {

    @Override
    public boolean canUseItemStack(ItemStack itemStack) {
        return itemStack.is(Items.POTION) && FRIStrategyUtil.fireResistanceTicksFromStack(itemStack) > 0;
    }

    @Override
    public boolean isValid(LivingEntity livingEntity, ReadableWorldState worldState) {
        return true;
    }

    @Override
    public Collection<AVPInventory.Entry> selectEntriesFromInventory(AVPInventory inventory) {
        return inventory.selectEntries(Items.POTION)
            .stream()
            .filter(entry -> FRIStrategyUtil.fireResistanceTicksFromStack(entry) > 0)
            .toList();
    }

    @Override
    public double score(LivingEntity livingEntity, ReadableWorldState worldState, ItemStack itemStack) {
        var healthRatio = worldState.getOrDefault(GOAPSensors.HEALTH_RATIO.key(), 1.0F);
        var isOnFire = worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false);
        var fireResTicksRemaining = worldState.getOrDefault(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS.key(), 0);
        var ticks = FRIStrategyUtil.fireResistanceTicksFromStack(itemStack);
        var weights = FRIStrategy.Weights.DEFAULT;

        if (ticks <= 0) {
            return Double.NEGATIVE_INFINITY;
        }

        var U = FRIStrategyUtil.urgencyTerm(isOnFire, healthRatio);
        var D = FRIStrategyUtil.durationTermSeconds(ticks);
        var use = FRIStrategyUtil.timePenalty(itemStack.getUseDuration(livingEntity));
        var waste = FRIStrategyUtil.overlapWasteTerm(fireResTicksRemaining, ticks);

        return weights.urgency() * U
            + weights.duration() * D
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste;
    }

    @Override
    public Action.Signal execute(LivingEntity livingEntity, ReadableWorldState worldState, Blackboard blackboard) {
        return ConsumeItemAction.perform(SoundEvents.GENERIC_DRINK, livingEntity, blackboard, () -> onConsume(livingEntity));
    }

    private static Action.Signal onConsume(LivingEntity livingEntity) {
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.POTION)) {
            return Action.Signal.ABORT;
        }

        var potionContents = Objects.requireNonNull(itemStack.get(DataComponents.POTION_CONTENTS));

        potionContents.getAllEffects().forEach(livingEntity::addEffect);

        itemStack.shrink(1);

        return Action.Signal.CONTINUE;
    }
}
