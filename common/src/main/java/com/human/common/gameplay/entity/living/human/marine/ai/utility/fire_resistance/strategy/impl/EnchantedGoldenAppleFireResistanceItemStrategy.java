package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.ConsumeItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.FireResistanceItemStrategyUtil;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class EnchantedGoldenAppleFireResistanceItemStrategy implements FireResistanceItemStrategy {

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
    public double score(Marine marine, Context context, ItemStack itemStack, Weights weights) {
        var U = FireResistanceItemStrategyUtil.urgencyTerm(context);
        var D = FireResistanceItemStrategyUtil.durationTermSeconds(TICK_DURATION);

        // Side-benefit is bigger if low HP.
        var side = FireResistanceItemStrategyUtil.clamp01((1.0 - context.healthRatio()) * 0.6);

        var use = FireResistanceItemStrategyUtil.timePenalty(FireResistanceItemStrategyUtil.useDurationTicks(marine, itemStack));
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
    public Action.Result consume(Marine marine, ReadableWorldState worldState, Blackboard blackboard) {
        return ConsumeItemAction.perform(SoundEvents.GENERIC_EAT, marine, blackboard, () -> onConsume(marine));
    }

    private static Action.@NotNull Result onConsume(Marine marine) {
        var itemStack = marine.getMainHandItem();

        if (!itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return Action.Result.FAILED;
        }

        itemStack.shrink(1);

        Foods.ENCHANTED_GOLDEN_APPLE
            .effects()
            .stream()
            .map(FoodProperties.PossibleEffect::effect)
            .forEach(marine::addEffect);

        return Action.Result.CONTINUE;
    }
}
