package com.human.common.gameplay.item.gun.attack.projectile;

import com.human.common.gameplay.entity.projectile.Rocket;
import com.human.common.gameplay.item.gun.attack.GunAttackAction;
import com.human.common.gameplay.item.gun.attack.GunAttackConfig;
import com.human.common.gameplay.item.gun.pipeline.GunShootResult;
import net.minecraft.server.level.ServerPlayer;

import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.service.Services;

public class RocketProjectileGunAttackAction implements GunAttackAction {

    public static final RocketProjectileGunAttackAction INSTANCE = new RocketProjectileGunAttackAction();

    private RocketProjectileGunAttackAction() {}

    @Override
    public GunShootResult shoot(GunAttackConfig gunAttackConfig) {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();

        if (level.isClientSide) {
            // Rocket projectile shots do nothing on the client-side.
            return GunShootResult.FAILURE;
        }

        var rocket = new Rocket(level, shooter);
        rocket.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);

        if (shooter instanceof ServerPlayer serverPlayer) {
            Services.SERVER_NETWORKING.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
        }

        level.addFreshEntity(rocket);

        return GunShootResult.SHOT;
    }
}
