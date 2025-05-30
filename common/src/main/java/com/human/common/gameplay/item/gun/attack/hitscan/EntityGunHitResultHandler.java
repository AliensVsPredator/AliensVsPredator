package com.human.common.gameplay.item.gun.attack.hitscan;

import com.human.common.gameplay.item.gun.attack.GunAttackConfig;
import com.lib.common.gameplay.util.EnchantmentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;

import com.avp.common.registry.key.AVPDamageTypeKeys;

public class EntityGunHitResultHandler {

    public static void handle(GunAttackConfig gunAttackConfig, Entity hitEntity) {
        var shooter = gunAttackConfig.shooter();
        var level = (ServerLevel) shooter.level();

        if (shooter instanceof Player && hitEntity instanceof Player && !level.getServer().isPvpAllowed()) {
            // Do not hurt entities if shooter was a player, target was a player and if PVP is not allowed.
            return;
        }

        // Apply pre-effects.
        if (hitEntity instanceof LivingEntity livingEntity) {
            applyFlameEffects(gunAttackConfig, livingEntity);
        }

        var powerLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.POWER);
        var damage = gunAttackConfig.fireModeConfig().damage() * (1 + (0.25F * powerLevel));
        var registry = shooter.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypeKeys.BULLET), shooter);

        var wasHurt = hitEntity.hurt(damageSource, damage);

        // Apply post-effects.
        if (wasHurt && hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            livingEntity.setLastHurtByMob(shooter);

            applyKnockbackEffects(gunAttackConfig, livingEntity, shooter);
        }
    }

    private static void applyFlameEffects(GunAttackConfig gunAttackConfig, LivingEntity livingEntity) {
        var flameLevel = EnchantmentUtil.getLevel(livingEntity.level(), gunAttackConfig.gunItemStack(), Enchantments.FLAME);

        if (flameLevel > 0) {
            livingEntity.igniteForTicks(20 * 5);
        }
    }

    private static void applyKnockbackEffects(GunAttackConfig gunAttackConfig, LivingEntity livingEntity, LivingEntity shooter) {
        var punchLevel = EnchantmentUtil.getLevel(livingEntity.level(), gunAttackConfig.gunItemStack(), Enchantments.PUNCH);
        var baseKnockback = gunAttackConfig.fireModeConfig().knockback();

        if (punchLevel > 0) {
            if (baseKnockback == 0) {
                baseKnockback = 0.2F;
            }

            baseKnockback *= punchLevel;
        }

        livingEntity.knockback(
            baseKnockback,
            Mth.sin(shooter.getYRot() * Mth.DEG_TO_RAD),
            -Mth.cos(shooter.getYRot() * Mth.DEG_TO_RAD)
        );
    }
}
