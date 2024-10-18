package org.avp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.common.game.entity.AbstractFacehugger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_PreventMovementWhenHugged extends Entity {

    public MixinLivingEntity_PreventMovementWhenHugged(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Inject(method = {"isImmobile"}, at = {@At("RETURN")}, cancellable = true)
    protected void isImmobile(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (this.getPassengers().stream().anyMatch(AbstractFacehugger.class::isInstance)) callbackInfo.setReturnValue(true);
    }
}
