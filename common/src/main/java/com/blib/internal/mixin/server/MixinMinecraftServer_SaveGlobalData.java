package com.blib.internal.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.util.BLibSaveTiming;
import com.blib.mod.BLib;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer_SaveGlobalData {

    @Inject(at = @At("TAIL"), method = "saveEverything")
    private void blib$onSaveEverything(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        var listeners = BLibGlobalEvents.SERVER_SAVE.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = MinecraftServer.class.cast(this);

        BLibSaveTiming.time(
            BLib.LOGGER,
            "SERVER_SAVE event listeners=" + listeners.size() + " flush=" + flush + " forced=" + forced,
            () -> {
                for (var listener : listeners) {
                    listener.invoke(self);
                }
            }
        );
    }
}
