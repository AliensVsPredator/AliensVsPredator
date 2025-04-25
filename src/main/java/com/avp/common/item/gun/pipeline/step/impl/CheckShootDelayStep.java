package com.avp.common.item.gun.pipeline.step.impl;

import net.minecraft.sounds.SoundSource;

import com.avp.common.item.gun.pipeline.GunShootContext;
import com.avp.common.item.gun.pipeline.GunShootResult;
import com.avp.common.item.gun.pipeline.step.GunShootStep;

public class CheckShootDelayStep implements GunShootStep {

    public static final CheckShootDelayStep INSTANCE = new CheckShootDelayStep();

    private CheckShootDelayStep() {}

    @Override
    public GunShootResult apply(GunShootContext context) {
        playShootStartSoundEffect(context);

        return context.tickProgress() < context.fireModeConfig().shootDelayInTicks()
            ? GunShootResult.DELAYED
            : GunShootResult.CONTINUE;
    }

    private void playShootStartSoundEffect(GunShootContext context) {
        var shootStartSoundEvent = context.fireModeConfig().shootStartSoundEvent();

        if (shootStartSoundEvent != null && context.isFirstTick()) {
            var shooter = context.shooter();
            shooter.level().playSound(null, shooter.blockPosition(), shootStartSoundEvent, SoundSource.PLAYERS);
        }
    }
}
