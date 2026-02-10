package com.blib.internal.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(Entity.class)
public abstract class MixinEntity_Events {

    @Inject(at = @At("HEAD"), method = "tick")
    private void blib$onTick(CallbackInfo ci) {
        var listeners = BLibGlobalEvents.ENTITY_TICK.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = (Entity) (Object) this;

        for (var listener : listeners) {
            listener.invoke(self);
        }
    }

    @Inject(at = @At("HEAD"), method = "remove")
    private void blib$onRemove(Entity.RemovalReason removalReason, CallbackInfo ci) {
        var listeners = BLibGlobalEvents.ENTITY_REMOVE.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = (Entity) (Object) this;

        for (var listener : listeners) {
            listener.invoke(self, removalReason);
        }
    }
}
