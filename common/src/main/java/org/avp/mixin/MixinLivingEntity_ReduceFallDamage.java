package org.avp.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.api.util.TypeUtil;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_ReduceFallDamage extends Entity {

    protected MixinLivingEntity_ReduceFallDamage(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "calculateFallDamage")
    void calculateReducedFallDamage(float height, float damage, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        var self = TypeUtil.<LivingEntity>self(this);
        if (self.getType().is(AVPEntityTypeTags.ALIENS)) {
            callbackInfoReturnable.setReturnValue(Mth.ceil((height - 16.0F) * damage));
        }
    }
}
