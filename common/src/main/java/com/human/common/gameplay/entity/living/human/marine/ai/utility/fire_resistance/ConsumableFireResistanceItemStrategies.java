package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.DrinkablePotionFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.EnchantedGoldenAppleFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.general.UtilityAI;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.avp.common.model.inventory.AVPInventory;

public final class ConsumableFireResistanceItemStrategies {

    private static final FireResistanceItemStrategy<Marine> DRINKABLE_POTION = new DrinkablePotionFireResistanceItemStrategy<>();

    private static final FireResistanceItemStrategy<Marine> ENCHANTED_GOLDEN_APPLE = new EnchantedGoldenAppleFireResistanceItemStrategy<>();

    private static final List<FireResistanceItemStrategy<Marine>> STRATEGIES = List.of(
        ENCHANTED_GOLDEN_APPLE,
        DRINKABLE_POTION
    );

    private static final UtilityAI<ItemStack, FireResistanceItemStrategy.Context<Marine>, Action.Signal, FireResistanceItemStrategy<Marine>> UTILITY_AI =
        new UtilityAI<>(STRATEGIES);

    public static Option<FireResistanceItemStrategy<Marine>> strategyFor(ItemStack stack) {
        return UTILITY_AI.getFirstStrategy(stack);
    }

    public static boolean isConsumableFireResistanceItem(ItemStack stack) {
        return strategyFor(stack).isSome();
    }

    public static List<FireResistanceItemStrategy<Marine>> all() {
        return STRATEGIES;
    }

    public static @Nullable UtilityAI.Pick<ItemStack, FireResistanceItemStrategy<Marine>> getBestStrategyAndMatchableOrNull(
        List<AVPInventory.Entry> entries,
        FireResistanceItemStrategy.Context<Marine> context
    ) {
        return UTILITY_AI.getBestStrategyAndMatchableOrNull(entries, AVPInventory.Entry::copyItemStack, context);
    }
}
