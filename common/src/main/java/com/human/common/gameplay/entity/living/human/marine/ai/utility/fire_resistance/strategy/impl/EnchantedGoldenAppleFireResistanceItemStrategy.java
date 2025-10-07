package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.avp.common.model.inventory.AVPInventoryHolder;

public class EnchantedGoldenAppleFireResistanceItemStrategy<T extends LivingEntity & AVPInventoryHolder> implements FireResistanceItemStrategy<T> {

    private static final int TICK_DURATION = Foods.ENCHANTED_GOLDEN_APPLE
        .effects()
        .stream()
        .map(FoodProperties.PossibleEffect::effect)
        .filter(effect -> effect.is(MobEffects.FIRE_RESISTANCE))
        .mapToInt(MobEffectInstance::getDuration)
        .findFirst()
        .orElse(0);

    @Override
    public boolean matches(ItemStack itemStack) {
        return itemStack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    public double score(ItemStack itemStack, Context<T> context) {
        var livingEntity = context.getLivingEntity();
        var weights = context.getWeights();

        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(TICK_DURATION);

        // Side-benefit is bigger if low HP.
        var side = FireResistanceItemStrategyUtil.clamp01((1.0 - context.healthRatio()) * 0.6);

        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(livingEntity, itemStack));
        var waste = FireResistanceItemStrategyUtil.overlapWasteTerm(context, TICK_DURATION);

        // Apply rarity penalty unless highly urgent.
        return weights.urgency() * U
            + weights.duration() * D
            + weights.sideBenefit() * side
            - weights.useTimePenalty() * use
            - weights.overlapWastePenalty() * waste
            - weights.rarityPenalty() * (1.0 - U);
    }

    @Override
    public Action.Signal execute(Context<T> context) {
        var blackboard = context.getBlackboard();
        var livingEntity = context.getLivingEntity();
        return ConsumeItemAction.perform(SoundEvents.GENERIC_EAT, livingEntity, blackboard, () -> onConsume(livingEntity));
    }

    private static Action.Signal onConsume(LivingEntity livingEntity) {
        var itemStack = livingEntity.getMainHandItem();

        if (!itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return Action.Signal.ABORT;
        }

        itemStack.shrink(1);

        Foods.ENCHANTED_GOLDEN_APPLE
            .effects()
            .stream()
            .map(FoodProperties.PossibleEffect::effect)
            .forEach(livingEntity::addEffect);

        return Action.Signal.CONTINUE;
    }
}
