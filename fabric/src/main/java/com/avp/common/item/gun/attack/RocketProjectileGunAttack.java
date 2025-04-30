package com.avp.common.item.gun.attack;

import net.minecraft.server.level.ServerPlayer;

import com.avp.common.entity.projectile.Rocket;
import com.avp.common.network.ServerNetworking;
import com.avp.common.network.packet.S2CGunRecoilPayload;

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
            ServerNetworking.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
        }

        level.addFreshEntity(rocket);
    }
}
