package org.avp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.data.tag.AVPEntityTypeTags;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_AliensAreImmuneToPoison extends Entity {

    protected MixinLivingEntity_AliensAreImmuneToPoison(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "canBeAffected")
    void ignorePoisonDamage(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = LivingEntity.class.cast(this);
        if (self.getType().is(AVPEntityTypeTags.ALIENS) && mobEffectInstance.getEffect() == MobEffects.POISON) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
