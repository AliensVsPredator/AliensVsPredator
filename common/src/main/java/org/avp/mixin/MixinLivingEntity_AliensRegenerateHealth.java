package org.avp.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.util.time.Tick;
import org.avp.common.data.tag.AVPEntityTypeTags;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_AliensRegenerateHealth {

    @Inject(at = @At("HEAD"), method = "tick")
    void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);
        var level = self.level();

        if (level.isClientSide)
            return;
        if (self.tickCount % Tick.PER_SECOND != 0)
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
