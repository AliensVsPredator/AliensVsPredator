package com.avp.core.common.item.gun.attack;

import com.avp.core.platform.service.Services;
import net.minecraft.server.level.ServerPlayer;

import com.avp.core.common.entity.projectile.Rocket;
import com.avp.core.common.network.packet.S2CGunRecoilPayload;

public class RocketProjectileGunAttack extends ProjectileGunAttack {

    public RocketProjectileGunAttack(GunAttackConfig gunAttackConfig) {
        super(gunAttackConfig);
    }

    @Override
    public void shoot() {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();
        var rocket = new Rocket(level, shooter);
        rocket.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);

        if (shooter instanceof ServerPlayer serverPlayer) {
            Services.NETWORK.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
        }

        level.addFreshEntity(rocket);
    }
}
