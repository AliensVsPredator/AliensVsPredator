package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.impl.DrinkablePotionFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.impl.EnchantedGoldenAppleFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.impl.LingeringPotionFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy.impl.SplashPotionFRIStrategy;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FRIStrategies {

    private static final FRIStrategy DRINKABLE_POTION = new DrinkablePotionFRIStrategy();

    private static final FRIStrategy ENCHANTED_GOLDEN_APPLE = new EnchantedGoldenAppleFRIStrategy();

    private static final FRIStrategy LINGERING_POTION = new LingeringPotionFRIStrategy();

    private static final FRIStrategy SPLASH_POTION = new SplashPotionFRIStrategy();

    public static final List<FRIStrategy> STRATEGIES = List.of(
        DRINKABLE_POTION,
        ENCHANTED_GOLDEN_APPLE,
        LINGERING_POTION,
        SPLASH_POTION
    );

    public static boolean isValid(ItemStack itemStack) {
        for (var strategy : STRATEGIES) {
            if (strategy.canUseItemStack(itemStack)) {
                return true;
            }
        }

        return false;
    }
}
