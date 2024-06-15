package org.avp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.data.tag.AVPEntityTypeTags;

@Mixin(Entity.class)
public abstract class MixinEntity_PreventBoatAndCartRiding {

    // If a boat or cart attempts to force the alien/predator to start riding it, tell the boat no.
    @Inject(at = @At("HEAD"), method = "startRiding", cancellable = true)
    void startRiding(Entity entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = Entity.class.cast(this);

        // All aliens will not ride boats.
        if (
            !self.getType().is(AVPEntityTypeTags.ALIENS) &&
                !self.getType().is(AVPEntityTypeTags.PREDATORS)
        )
            return;

        if (entity instanceof Boat || entity instanceof Minecart) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    // If the alien/predator is already riding a boat somehow, stop riding the boat.
    @Inject(at = @At("HEAD"), method = "tick")
    void tick(CallbackInfo callbackInfo) {
        var self = Entity.class.cast(this);
        var level = self.level();

        if (level.isClientSide)
            return;
        if (
            !self.getType().is(AVPEntityTypeTags.ALIENS) &&
                !self.getType().is(AVPEntityTypeTags.PREDATORS)
        )
            return;

        if (self.getVehicle() instanceof Boat || self.getVehicle() instanceof Minecart) {
            self.stopRiding();
        }
    }
}
