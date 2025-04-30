package com.avp.fabric.common.effect;

import mod.azure.azurelib.core.object.Color;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import com.avp.fabric.common.damage.AVPDamageTypes;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.fabric.common.util.AVPPredicates;

public class RadiationStatusEffect extends MobEffect {

    public static final int EFFECT_DURATION_IN_TICKS = (int) TimeUnit.MINUTES.toSeconds(16) * 20;

    private static final Map<LivingEntity, Integer> EFFECT_TRACKER = new WeakHashMap<>();

    protected RadiationStatusEffect() {
        super(MobEffectCategory.HARMFUL, Color.GREEN.getColor());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        var currentDuration = EFFECT_TRACKER.getOrDefault(livingEntity, 0);

        if (
            AVPPredicates.IS_IMMORTAL.test(livingEntity) || livingEntity.getType()
                .is(
                    AVPEntityTypeTags.RADIATION_RESISTANT
                )
        ) {
            livingEntity.removeEffect(AVPEffects.RADIATION_EFFECT);
            return false;
        }

        switch (amplifier) {
            case 0:
                applyLevel1RadiationSideEffects(livingEntity, amplifier);
                break;
            case 1:
                applyLevel2RadiationSideEffects(livingEntity, amplifier);
                break;
            default:
                applyDefaultRadiationSideEffects(livingEntity, amplifier);
                break;
        }

        var threshold = switch (amplifier) {
            case 0 -> 4 * 60 * 20; // 4 minutes in ticks
            case 1 -> 8 * 60 * 20; // 8 minutes in ticks
            default -> RadiationStatusEffect.EFFECT_DURATION_IN_TICKS;
        };

        if (currentDuration >= threshold && amplifier < 2) {
            EFFECT_TRACKER.put(livingEntity, 0);
            livingEntity.addEffect(
                new MobEffectInstance(AVPEffects.RADIATION_EFFECT, RadiationStatusEffect.EFFECT_DURATION_IN_TICKS, amplifier + 1)
            );
        } else {
            EFFECT_TRACKER.put(livingEntity, currentDuration + 1);
        }

        return livingEntity.isAlive();
    }

    private void applyLevel1RadiationSideEffects(LivingEntity livingEntity, int amplifier) {
        handleStatusEffects(livingEntity, 5 * 20, amplifier, MobEffects.WEAKNESS, MobEffects.HUNGER);

        if (livingEntity.tickCount % (4 * 20) == 0) {
            livingEntity.hurt(createRadiationDamageSource(livingEntity), 0.5F);
        }
    }

    private void applyLevel2RadiationSideEffects(LivingEntity livingEntity, int amplifier) {
        handleStatusEffects(
            livingEntity,
            5 * 20,
            amplifier,
            MobEffects.WEAKNESS,
            MobEffects.HUNGER,
            MobEffects.MOVEMENT_SLOWDOWN
        );

        if (livingEntity.tickCount % (2 * 20) == 0) {
            livingEntity.hurt(createRadiationDamageSource(livingEntity), 1.0F);
        }
    }

    private void applyDefaultRadiationSideEffects(LivingEntity livingEntity, int amplifier) {
        handleStatusEffects(
            livingEntity,
            5 * 20,
            amplifier,
            MobEffects.WEAKNESS,
            MobEffects.HUNGER,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.BLINDNESS
        );

        if (livingEntity.tickCount % 20 == 0) {
            livingEntity.hurt(createRadiationDamageSource(livingEntity), 2.0F);
        }
    }

    @SafeVarargs
    private void handleStatusEffects(@NotNull LivingEntity livingEntity, int ticks, int amplifier, Holder<MobEffect>... statusEffects) {
        for (var effect : statusEffects) {
            if (!livingEntity.hasEffect(effect)) {
                livingEntity.addEffect(new MobEffectInstance(effect, ticks, amplifier, true, true));
            }
        }
    }

    private static DamageSource createRadiationDamageSource(LivingEntity livingEntity) {
        return new DamageSource(
            livingEntity.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(
                    AVPDamageTypes.RADIATION
                )
        );
    }
}
