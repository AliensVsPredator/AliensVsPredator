package com.avp.common.effect;

import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPPredicates;
import mod.azure.azurelib.core.object.Color;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

public class RadiationStatusEffect extends MobEffect {

    private static final Map<LivingEntity, Integer> effectTracker = new WeakHashMap<>();

    protected RadiationStatusEffect() {
        super(MobEffectCategory.HARMFUL, Color.GREEN.getColor());
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        var armorCheck = livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.FEET).is(AVPItemTags.RADIATION_RESISTANT_ARMOR);
        var currentDuration = effectTracker.getOrDefault(livingEntity, 0);

        if (AVPPredicates.IS_IMMORTAL.test(livingEntity) || livingEntity.getType().is(AVPEntityTypeTags.RADIATION_RESISTANT)) {
            livingEntity.removeEffect(AVPEffects.RADIATION_EFFECT);
            return false;
        }

        if (!armorCheck && !AVPPredicates.IS_IMMORTAL.test(livingEntity)) {
            var registry = livingEntity.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
            var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.RADIATION));
            switch (amplifier) {
                case 0:
                    this.handleStatusEffects(livingEntity, 100, amplifier, MobEffects.WEAKNESS, MobEffects.HUNGER);
                    if (livingEntity.tickCount % 80 == 0)
                        livingEntity.hurt(damageSource, 0.1F);
                    break;

                case 1:
                    this.handleStatusEffects(livingEntity, 100, amplifier, MobEffects.WEAKNESS, MobEffects.HUNGER,
                            MobEffects.MOVEMENT_SLOWDOWN);
                    if (livingEntity.tickCount % 40 == 0)
                        livingEntity.hurt(damageSource, 2.1F);
                    break;

                default:
                    this.handleStatusEffects(livingEntity, 100, amplifier, MobEffects.WEAKNESS, MobEffects.HUNGER,
                            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.BLINDNESS);
                    if (livingEntity.tickCount % 20 == 0)
                        livingEntity.hurt(damageSource, 5.0F);
                    break;
            }

            var threshold = switch (amplifier) {
                case 0 -> 20 * 60 * 8; // 8 minutes in ticks
                case 1 -> 20 * 60 * 16; // 16 minutes in ticks
                default -> Integer.MAX_VALUE;
            };

            if (currentDuration >= threshold && amplifier < 2) {
                effectTracker.put(livingEntity, 0);
                livingEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, amplifier + 1));
            } else {
                effectTracker.put(livingEntity, currentDuration + 1);
            }
        } else {
            effectTracker.remove(livingEntity);
        }

        return !armorCheck;
    }

    @SafeVarargs
    private void handleStatusEffects(@NotNull LivingEntity livingEntity, int ticks, int amplifier, Holder<MobEffect>... statusEffects) {
        for (Holder<MobEffect> effect : statusEffects)
            if (!livingEntity.hasEffect(effect))
                livingEntity.addEffect(new MobEffectInstance(effect, ticks, amplifier, true, true));
    }
}
