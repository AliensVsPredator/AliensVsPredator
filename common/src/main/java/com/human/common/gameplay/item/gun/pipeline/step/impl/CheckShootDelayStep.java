package com.human.common.gameplay.item.gun.pipeline.step.impl;

import com.human.common.gameplay.item.gun.pipeline.GunShootContext;
import com.human.common.gameplay.item.gun.pipeline.GunShootResult;
import com.human.common.gameplay.item.gun.pipeline.step.GunShootStep;
import net.minecraft.sounds.SoundSource;

public class CheckShootDelayStep implements GunShootStep {

    public static final CheckShootDelayStep INSTANCE = new CheckShootDelayStep();

    private CheckShootDelayStep() {}

    @Override
    public GunShootResult apply(GunShootContext context) {
        if (!context.shooter().level().isClientSide) {
            // Only run this side effect server-side.
            playShootStartSoundEffect(context);
        }

        return context.tickProgress() < context.fireModeConfig().shootDelayInTicks()
            ? GunShootResult.DELAYED
            : GunShootResult.CONTINUE;
    }

    private void playShootStartSoundEffect(GunShootContext context) {
        var shootStartSoundEvent = context.fireModeConfig().shootStartSoundEvent();

        if (shootStartSoundEvent != null && context.isFirstTick()) {
            var shooter = context.shooter();
            shooter.level().playSound(null, shooter.blockPosition(), shootStartSoundEvent.get(), SoundSource.PLAYERS);
        }
    }
}
