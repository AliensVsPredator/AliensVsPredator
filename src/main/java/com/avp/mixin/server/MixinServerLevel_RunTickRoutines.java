package com.avp.mixin.server;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.level.saveddata.HiveLevelData;
import com.avp.server.BlockBreakProgressManager;
import com.avp.server.ServerScheduler;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel_RunTickRoutines {

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        tickScheduledRunnables();

        var serverLevel = ServerLevel.class.cast(this);
        var hiveLevelDataOptional = HiveLevelData.getOrCreate(serverLevel);

        hiveLevelDataOptional.ifPresent(HiveLevelData::tick);

        BlockBreakProgressManager.tick(serverLevel);
    }

    @Unique
    private void tickScheduledRunnables() {
        ServerScheduler.getScheduledTasks().removeIf(entry -> {
            var runTime = entry.getKey();

            if (System.currentTimeMillis() >= runTime) {
                entry.getValue().run();
                return true;
            }

            return false;
        });
    }
}
