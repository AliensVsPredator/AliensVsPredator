package com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategyUtil;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;

import com.avp.common.model.inventory.AVPInventory;

public class EnchantedGoldenAppleFRIStrategy implements FRIStrategy {

    private static final int TICK_DURATION = Foods.ENCHANTED_GOLDEN_APPLE
        .effects()
        .stream()
        .map(FoodProperties.PossibleEffect::effect)
        .filter(effect -> effect.is(MobEffects.FIRE_RESISTANCE))
        .mapToInt(MobEffectInstance::getDuration)
        .findFirst()
        .orElse(0);

    private static final List<MobEffectInstance> POSSIBLE_EFFECTS = Foods.ENCHANTED_GOLDEN_APPLE
        .effects()
        .stream()
        .map(FoodProperties.PossibleEffect::effect)
        .toList();

    @Override
    public boolean canUseItemStack(ItemStack itemStack) {
        return itemStack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public boolean isValid(LivingEntity livingEntity, ReadableWorldState worldState) {
        return true;
    }

    @Override
    public Collection<AVPInventory.Entry> selectEntriesFromInventory(AVPInventory inventory) {
        return inventory.selectEntries(Items.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public double score(LivingEntity livingEntity, ReadableWorldState worldState, ItemStack itemStack) {
        var healthRatio = worldState.getOrDefault(GOAPSensors.HEALTH_RATIO.key(), 1.0F);
        var isOnFire = worldState.getOrDefault(GOAPSensors.IS_ON_FIRE.key(), false);
        var fireResTicksRemaining = worldState.getOrDefault(GOAPSensors.FIRE_RESISTANCE_REMAINING_TICKS.key(), 0);
        var weights = FRIStrategy.Weights.DEFAULT;

        var U = FRIStrategyUtil.urgencyTerm(isOnFire, healthRatio);
        var D = FRIStrategyUtil.durationTermSeconds(TICK_DURATION);

        // Side-benefit is bigger if low HP.
        var side = FRIStrategyUtil.clamp01((1.0 - healthRatio) * 0.6);

        var use = FRIStrategyUtil.timePenalty(itemStack.getUseDuration(livingEntity));
        var waste = FRIStrategyUtil.overlapWasteTerm(fireResTicksRemaining, TICK_DURATION);

        // Apply rarity penalty unless highly urgent.
        return weights.urgency() * U
            + weights.duration() * D
            + weights.sideBenefit() * side
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste
            - weights.rarityPenalty() * (1.0 - U);
    }

    @Override
    public Action.Signal execute(LivingEntity livingEntity, ReadableWorldState worldState, Blackboard blackboard) {
        return ConsumeItemAction.perform(SoundEvents.GENERIC_EAT, livingEntity, blackboard, () -> onConsume(livingEntity));
    }

    private static Action.Signal onConsume(LivingEntity livingEntity) {
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return Action.Signal.ABORT;
        }

        POSSIBLE_EFFECTS.forEach(livingEntity::addEffect);

        itemStack.shrink(1);

        return Action.Signal.CONTINUE;
    }
}
