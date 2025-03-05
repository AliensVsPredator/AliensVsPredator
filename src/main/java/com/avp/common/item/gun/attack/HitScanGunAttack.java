package com.avp.common.item.gun.attack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.config.ConfigProperties;
import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.network.ServerNetworking;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.AVPPredicates;
import com.avp.common.util.EnchantmentUtil;
import com.avp.server.BlockBreakProgressManager;

public class HitScanGunAttack extends AbstractGunAttack {

    public HitScanGunAttack(GunAttackConfig gunAttackConfig) {
        super(gunAttackConfig);
    }

    @Override
    public void shoot() {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();

        if (level.isClientSide) {
            return;
        }

        var piercingLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.PIERCING);
        var hitEntities = new ArrayList<>();

        pierceAttempts:
        for (int i = 0; i < piercingLevel + 1; i++) {
            var hitResult = ProjectileUtil.getHitResultOnViewVector(
                shooter,
                entity -> !hitEntities.contains(entity) && (entity.getType() == EntityType.END_CRYSTAL || AVPPredicates.isLiving(entity)),
                gunAttackConfig.fireModeConfig().range()
            );

            if (shooter instanceof ServerPlayer serverPlayer) {
                ServerNetworking.sendToClient(serverPlayer, new S2CGunRecoilPayload(gunAttackConfig.fireModeConfig().recoil()));
            }

            switch (hitResult.getType()) {
                case BLOCK -> {
                    var blockHitResult = (BlockHitResult) hitResult;
                    var blockPos = blockHitResult.getBlockPos();
                    var direction = blockHitResult.getDirection();
                    onBlockHit(blockPos, direction);
                    break pierceAttempts;
                }
                case ENTITY -> {
                    var hitEntity = ((EntityHitResult) hitResult).getEntity();
                    hitEntities.add(hitEntity);
                    onEntityHit(hitEntity);
                }
                case MISS -> { /* Do nothing */ }
            }
        }
    }

    @Override
    public void onBlockHit(BlockPos blockPos, Direction direction) {
        var level = gunAttackConfig.shooter().level();
        var blockState = level.getBlockState(blockPos);
        var soundType = blockState.getSoundType();

        var ricochetSoundEvent = getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSoundEvent, SoundSource.BLOCKS);

        damageBlock(level, blockPos);

        var payload = new S2CBulletHitBlockPayload(blockPos, direction);
        ServerNetworking.sendToAllClients(level.getServer(), payload);
    }

    @Override
    public void onEntityHit(Entity hitEntity) {
        var shooter = gunAttackConfig.shooter();
        var level = shooter.level();

        var powerLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.POWER);
        var damage = gunAttackConfig.fireModeConfig().damage() * (1 + (0.25F * powerLevel));
        var registry = shooter.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.BULLET), shooter);

        hitEntity.hurt(damageSource, damage);

        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            livingEntity.setLastHurtByMob(shooter);

            applyFlameEffects(livingEntity);
            applyKnockbackEffects(livingEntity, shooter);
        }
    }

    private void applyFlameEffects(LivingEntity livingEntity) {
        var flameLevel = EnchantmentUtil.getLevel(livingEntity.level(), gunAttackConfig.gunItemStack(), Enchantments.FLAME);

        if (flameLevel > 0) {
            livingEntity.igniteForTicks(20 * 5);
        }
    }

    private void applyKnockbackEffects(LivingEntity livingEntity, LivingEntity shooter) {
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

    private static SoundEvent getRicochetSoundForSoundType(SoundType soundType) {
        SoundEvent ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS;
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT;
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_METAL;
        } else {
            ricochetSfx = AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC;
        }
        return ricochetSfx;
    }

    private void damageBlock(@NotNull Level level, BlockPos blockPos) {
        if (!AVP.WEAPONS_CONFIG.properties().getOrThrow(ConfigProperties.BULLETS_DAMAGE_BLOCKS_ENABLED)) {
            return;
        }

        if (!level.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS)) {
            return;
        }

        var blockState = level.getBlockState(blockPos);

        // Only damage blocks if they should be destroyed.
        if (blockState.is(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)) {
            return;
        }

        var powerLevel = EnchantmentUtil.getLevel(level, gunAttackConfig.gunItemStack(), Enchantments.POWER);
        var damage = gunAttackConfig.fireModeConfig().damage() * (1 + (0.25F * powerLevel));
        BlockBreakProgressManager.damage(level, blockPos, damage);
    }
}
