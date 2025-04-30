package com.avp.fabric.common.item.gun.attack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantments;

import com.avp.common.util.EnchantmentUtil;
import com.avp.fabric.common.entity.projectile.Flamethrow;
import com.avp.fabric.common.network.ServerNetworking;
import com.avp.fabric.common.network.packet.S2CGunRecoilPayload;

public class FlamethrowProjectileGunAttack extends ProjectileGunAttack {

    public FlamethrowProjectileGunAttack(GunAttackConfig gunAttackConfig) {
        super(gunAttackConfig);
    }

    @Override
    public void shoot() {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();
        var flamethrow = new Flamethrow(level, shooter);
        flamethrow.setEnhanced(EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.FLAME) > 0);
        flamethrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 1.5F, 1.0F);

        if (shooter instanceof ServerPlayer serverPlayer) {
            ServerNetworking.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
        }

        level.addFreshEntity(flamethrow);
    }
}
