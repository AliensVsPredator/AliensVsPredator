package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.DrinkablePotionFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.EnchantedGoldenAppleFireResistanceItemStrategy;
import com.just.core.functional.option.Option;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ConsumableFireResistanceItemStrategies {

    public static final FireResistanceItemStrategy DRINKABLE_POTION = new DrinkablePotionFireResistanceItemStrategy();

    public static final FireResistanceItemStrategy ENCHANTED_GOLDEN_APPLE = new EnchantedGoldenAppleFireResistanceItemStrategy();

    private static final List<FireResistanceItemStrategy> REGISTRY = List.of(
        ENCHANTED_GOLDEN_APPLE,
        DRINKABLE_POTION
    );

    public static Option<FireResistanceItemStrategy> strategyFor(ItemStack stack) {
        for (var strategy : REGISTRY) {
            if (strategy.matches(stack)) {
                return Option.some(strategy);
            }
        }

        return Option.none();
    }

    public static boolean isConsumableFireResistanceItem(ItemStack stack) {
        return strategyFor(stack).isSome();
    }

    public static List<FireResistanceItemStrategy> all() {
        return REGISTRY;
    }
}
