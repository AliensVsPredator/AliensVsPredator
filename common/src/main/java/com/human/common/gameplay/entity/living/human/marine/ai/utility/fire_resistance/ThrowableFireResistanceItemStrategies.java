package com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance;

import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.LingeringPotionFireResistanceItemStrategy;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.impl.SplashPotionFireResistanceItemStrategy;
import com.just.core.functional.option.Option;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ThrowableFireResistanceItemStrategies {

    public static final FireResistanceItemStrategy SPLASH_POTION = new SplashPotionFireResistanceItemStrategy();

    public static final FireResistanceItemStrategy LINGERING_POTION = new LingeringPotionFireResistanceItemStrategy();

    private static final List<FireResistanceItemStrategy> REGISTRY = List.of(
        SPLASH_POTION,
        LINGERING_POTION
    );

    public static Option<FireResistanceItemStrategy> strategyFor(ItemStack stack) {
        for (var strategy : REGISTRY) {
            if (strategy.matches(stack)) {
                return Option.some(strategy);
            }
        }

        return Option.none();
    }

    public static boolean isThrowableFireResistanceItem(ItemStack stack) {
        return strategyFor(stack).isSome();
    }

    public static List<FireResistanceItemStrategy> all() {
        return REGISTRY;
    }
}
