package org.avp.mixin.server;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.server.BlockBreakProgressManager;
import org.avp.server.HivemindManager;
import org.avp.server.QueenManager;
import org.avp.api.server.ServerScheduler;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        tickScheduledRunnables();

        var serverLevel = ServerLevel.class.cast(this);
        BlockBreakProgressManager.tick(serverLevel);
        HivemindManager.tick(serverLevel);
        QueenManager.tick(serverLevel);
    }

    @Unique
    private void tickScheduledRunnables() {
        ServerScheduler.getScheduledTasks().removeIf(tuple -> {
            var runTime = tuple.first();

            if (System.currentTimeMillis() >= runTime) {
                tuple.second().run();
                return true;
            }

            return false;
        });
    }
}
