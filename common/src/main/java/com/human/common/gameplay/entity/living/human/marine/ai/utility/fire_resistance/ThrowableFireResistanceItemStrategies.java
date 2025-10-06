package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance;

import com.avp.common.model.inventory.AVPInventory;
import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.LingeringPotionFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.SplashPotionFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.general.UtilityAI;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ThrowableFireResistanceItemStrategies {

    public static final FireResistanceItemStrategy<Marine> SPLASH_POTION = new SplashPotionFireResistanceItemStrategy<>();

    public static final FireResistanceItemStrategy<Marine> LINGERING_POTION = new LingeringPotionFireResistanceItemStrategy<>();

    private static final List<FireResistanceItemStrategy<Marine>> STRATEGIES = List.of(
        SPLASH_POTION,
        LINGERING_POTION
    );

    private static final UtilityAI<ItemStack, FireResistanceItemStrategy.Context<Marine>, Action.Result, FireResistanceItemStrategy<Marine>> UTILITY_AI = new UtilityAI<>(STRATEGIES);

    public static Option<FireResistanceItemStrategy<Marine>> strategyFor(ItemStack stack) {
        return UTILITY_AI.getFirstStrategy(stack);
    }

    public static boolean isThrowableFireResistanceItem(ItemStack stack) {
        return strategyFor(stack).isSome();
    }

    public static List<FireResistanceItemStrategy<Marine>> all() {
        return STRATEGIES;
    }

    public static @Nullable UtilityAI.Pick<ItemStack, FireResistanceItemStrategy<Marine>> getBestStrategyAndMatchableOrNull(List<AVPInventory.Entry> entries, FireResistanceItemStrategy.Context<Marine> context) {
        return UTILITY_AI.getBestStrategyAndMatchableOrNull(entries, AVPInventory.Entry::copyItemStack, context);
    }
}
