package com.blib.internal.mixin;

import com.blib.api.common.tag.v1.BLibDamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mirrors vanilla's {@code bypasses_armor} behavior — short-circuits {@code getDamageAfterArmorAbsorb} to
 * return the incoming damage unmodified — but gates on
 * {@link BLibDamageTypeTags#BYPASSES_ARMOR_ONLY}, a BLib-owned tag that vanilla's {@code bypasses_shield}
 * does NOT transitively include. Lets a damage type say "tears through armor" without also gaining "passes
 * through a raised shield."
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_BypassesArmorOnly {

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void blib$bypassArmorOnly(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        if (source.is(BLibDamageTypeTags.BYPASSES_ARMOR_ONLY)) {
            cir.setReturnValue(damage);
        }
    }
}
