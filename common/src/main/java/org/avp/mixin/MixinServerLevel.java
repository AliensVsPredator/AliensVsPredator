package org.avp.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.common.AVPConstants;
import org.avp.common.util.MixinUtilities;
import org.avp.common.util.TimeUtilities;
import org.avp.server.BlockBreakProgressManager;
import org.avp.server.ServerScheduler;

/**
 * @author Boston Vanseghi
 */
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
        var serverLevel = MixinUtilities.<ServerLevel>self(this);
        var gameTime = serverLevel.getGameTime();

        if (gameTime % TimeUtilities.ONE_MINUTE_IN_TICKS == 0) {
            AVPConstants.LOGGER.debug(
                "Cleaning block break progress map ({} entries)",
                BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
            );
            BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.entrySet().removeIf(entry -> {
                var lastUpdateTimeMillis = entry.getValue().first();
                return System.currentTimeMillis() > lastUpdateTimeMillis;
            });
            AVPConstants.LOGGER.debug(
                "Finished cleaning block break progress map ({} entries)",
                BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.size()
            );
        }
    }
}
