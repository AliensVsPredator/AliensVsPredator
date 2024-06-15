package org.avp.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
public abstract class MixinLivingEntity_AliensDoNotBurn extends Entity {

    protected MixinLivingEntity_AliensDoNotBurn(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "hurt")
    void ignoresFireDamage(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = LivingEntity.class.cast(this);

        if (!self.getType().is(AVPEntityTypeTags.ALIENS))
            return;

        if (damageSource.is(DamageTypes.ON_FIRE)) {
            self.setRemainingFireTicks(10);
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
