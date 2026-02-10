package com.blib.internal.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer_SaveGlobalData {

    @Inject(at = @At("TAIL"), method = "saveEverything")
    private void blib$onSaveEverything(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        var listeners = BLibGlobalEvents.SERVER_SAVE.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = MinecraftServer.class.cast(this);

        for (var listener : listeners) {
            listener.invoke(self);
        }
    }
}
