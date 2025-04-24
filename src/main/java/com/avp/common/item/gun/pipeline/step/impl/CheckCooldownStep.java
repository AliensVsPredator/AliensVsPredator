package com.avp.common.item.gun.pipeline.step.impl;

import net.minecraft.world.entity.player.Player;

import com.avp.common.item.gun.pipeline.GunShootContext;
import com.avp.common.item.gun.pipeline.GunShootResult;
import com.avp.common.item.gun.pipeline.step.GunShootStep;

public class CheckCooldownStep implements GunShootStep {

    public static final CheckCooldownStep INSTANCE = new CheckCooldownStep();

    private CheckCooldownStep() {}

    @Override
    public GunShootResult apply(GunShootContext context) {
        if (
            context.shooter() instanceof Player player &&
                player.getCooldowns().isOnCooldown(context.gunItem())
        ) {
            return GunShootResult.COOLDOWN;
        }

        return GunShootResult.CONTINUE;
    }
}
