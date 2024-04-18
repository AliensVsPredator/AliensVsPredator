package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.common.util.MixinUtils;
import org.avp.server.BlockBreakProgressManager;
import org.avp.server.ServerScheduler;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        tickScheduledRunnables();
        tickBlockBreakProgressManager();
    }

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

    private void tickBlockBreakProgressManager() {
        var serverLevel = MixinUtils.<ServerLevel>self(this);
        BlockBreakProgressManager.tick(serverLevel);
    }
}
