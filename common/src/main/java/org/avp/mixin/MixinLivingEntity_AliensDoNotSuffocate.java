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

import org.avp.common.tag.AVPEntityTypeTags;
import org.avp.common.util.MixinUtils;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_AliensDoNotSuffocate extends Entity {

    protected MixinLivingEntity_AliensDoNotSuffocate(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "hurt")
    void ignoreSuffocationDamage(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = MixinUtils.<LivingEntity>self(this);
        if (self.getType().is(AVPEntityTypeTags.ALIENS) && damageSource.is(DamageTypes.IN_WALL)) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
