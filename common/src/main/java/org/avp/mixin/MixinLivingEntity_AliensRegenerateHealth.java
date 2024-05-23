package org.avp.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.avp.common.tag.AVPEntityTypeTags;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_AliensRegenerateHealth {

    @Inject(at = @At("HEAD"), method = "tick")
    void tick(CallbackInfo callbackInfo) {
        var self = MixinUtils.<LivingEntity>self(this);
        var level = self.level();

        if (level.isClientSide)
            return;
        if (self.tickCount % 20 != 0)
            return;
        if (!self.getType().is(AVPEntityTypeTags.ALIENS))
            return;
        if (self.getHealth() >= self.getMaxHealth())
            return;
        if (!self.isAlive())
            return;

        self.heal(1F);
    }
}
