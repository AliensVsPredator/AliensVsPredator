package com.avp.common.item.gun.attack;

import net.minecraft.server.level.ServerPlayer;

import com.avp.common.entity.projectile.Rocket;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.service.Services;

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
            Services.SERVER_NETWORKING.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
        }

        level.addFreshEntity(rocket);
    }
}
