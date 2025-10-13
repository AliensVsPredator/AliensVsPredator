package com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy;

import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl.DrinkablePotionFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl.EnchantedGoldenAppleFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl.LingeringPotionFRIStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.impl.SplashPotionFRIStrategy;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FRIStrategies {

    private static final FRIStrategy DRINKABLE_POTION = new DrinkablePotionFRIStrategy();

    private static final FRIStrategy ENCHANTED_GOLDEN_APPLE = new EnchantedGoldenAppleFRIStrategy();

    private static final FRIStrategy LINGERING_POTION = new LingeringPotionFRIStrategy();

    private static final FRIStrategy SPLASH_POTION = new SplashPotionFRIStrategy();

    // private static final FRIStrategy WATER_BUCKET = new WaterBucketFireReducingItemStrategy<>();

    public static final List<FRIStrategy> STRATEGIES = List.of(
        DRINKABLE_POTION,
        ENCHANTED_GOLDEN_APPLE,
        LINGERING_POTION,
        SPLASH_POTION
        // WATER_BUCKET
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
