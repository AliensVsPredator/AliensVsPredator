package com.human.common.gameplay.item.gun.pipeline.step.impl;

import com.human.common.gameplay.item.gun.pipeline.GunShootContext;
import com.human.common.gameplay.item.gun.pipeline.GunShootResult;
import com.human.common.gameplay.item.gun.pipeline.step.GunShootStep;
import net.minecraft.world.entity.player.Player;

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
