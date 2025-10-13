package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.strategy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.avp.common.model.inventory.AVPInventory;

public class FRIStrategyUtil {

    public static int fireResistanceTicksFromStack(AVPInventory.Entry entry) {
        var mobEffectInstance = getMobEffectInstanceOrNull(entry, MobEffects.FIRE_RESISTANCE);
        return mobEffectInstance == null
            ? 0
            : mobEffectInstance.getDuration();
    }

    public static int fireResistanceTicksFromStack(ItemStack itemStack) {
        var mobEffectInstance = getMobEffectInstanceOrNull(itemStack, MobEffects.FIRE_RESISTANCE);
        return mobEffectInstance == null
            ? 0
            : mobEffectInstance.getDuration();
    }

    public static double urgencyTerm(boolean isOnFire, double healthRatio) {
        var onFire = isOnFire ? 1.0 : 0.0;
        var lowHp = 1.0 - clamp01(healthRatio);

        return 0.5 * onFire + 0.35 * lowHp;
    }

    public static double durationTermSeconds(int ticks) {
        // Reward up to ~3 minutes; saturate beyond.
        var secs = ticks / 20.0;

        return clamp01(secs / 180.0);
    }

    public static double overlapWasteTerm(int fireResTicksRemaining, int newTicks) {
        // If entity already has long remaining, discourage stacking.
        var remain = fireResTicksRemaining;

        if (remain <= 0) {
            return 0.0;
        }

        // Waste if remaining already covers a minute+.
        var coverage = durationTermSeconds(remain);
        var redundancy = durationTermSeconds(Math.max(0, remain - newTicks));

        return Math.max(0.0, coverage - redundancy);
    }

    public static double timePenalty(int useTicks) {
        // Normalize against 32-tick baseline.
        return clamp01((useTicks - 16) / 32.0);
    }

    public static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static @Nullable MobEffectInstance getMobEffectInstanceOrNull(AVPInventory.Entry entry, Holder<MobEffect> mobEffectHolder) {
        for (var mobEffectInstance : getMobEffects(entry)) {
            if (mobEffectInstance.getEffect() == mobEffectHolder) {
                return mobEffectInstance;
            }
        }

        return null;
    }

    private static Iterable<MobEffectInstance> getMobEffects(AVPInventory.Entry entry) {
        var potionContents = entry.get(DataComponents.POTION_CONTENTS);

        if (potionContents == null) {
            return List.of();
        }

        return potionContents.getAllEffects();
    }

    private static @Nullable MobEffectInstance getMobEffectInstanceOrNull(ItemStack itemStack, Holder<MobEffect> mobEffectHolder) {
        for (var mobEffectInstance : getMobEffects(itemStack)) {
            if (mobEffectInstance.getEffect() == mobEffectHolder) {
                return mobEffectInstance;
            }
        }

        return null;
    }

    private static Iterable<MobEffectInstance> getMobEffects(ItemStack itemStack) {
        var potionContents = itemStack.get(DataComponents.POTION_CONTENTS);

        if (potionContents == null) {
            return List.of();
        }

        return potionContents.getAllEffects();
    }
}
