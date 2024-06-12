package org.avp.common.game.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.util.BLPredicates;
import org.avp.api.common.weapon.WeaponItemStack;
import org.avp.api.common.weapon.attack.AbstractWeaponAttack;
import org.avp.common.config.AVPConfig;
import org.avp.common.game.damage.AVPDamageSources;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.api.service.Services;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.util.SoundUtils;
import org.avp.api.server.BlockBreakProgressManager;
import org.jetbrains.annotations.NotNull;

public class HitscanWeaponAttack extends AbstractWeaponAttack {

    public HitscanWeaponAttack(WeaponItemStack weaponItemStack, LivingEntity shooter) {
        super(weaponItemStack, shooter);
    }

    @Override
    public void shoot() {
        var hitResult = ProjectileUtil.getHitResultOnViewVector(
            shooter,
            entity -> entity.getType() == EntityType.END_CRYSTAL || BLPredicates.IS_LIVING.test(entity),
            fireModeData.range()
        );

        switch (hitResult.getType()) {
            case BLOCK -> {
                var blockHitResult = (BlockHitResult) hitResult;
                var blockPos = blockHitResult.getBlockPos();
                var direction = blockHitResult.getDirection();
                onBlockHit(blockPos, direction);
            }
            case ENTITY -> onEntityHit(((EntityHitResult) hitResult).getEntity());
            case MISS -> { /* Do nothing */ }
        }
    }

    @Override
    public void onBlockHit(BlockPos blockPos, Direction direction) {
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        var soundType = block.getSoundType(blockState);

        BLHolder<SoundEvent> ricochetSfx = SoundUtils.getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSfx.get(), SoundSource.BLOCKS);

        damageBlock(level, blockPos);

        var payload = new ClientboundBulletHitBlockPayload(blockPos, direction);
        Services.NETWORK_SERVICE.sendToAllClients(level.getServer(), payload);
    }

    @Override
    public void onEntityHit(Entity hitEntity) {
        var level = shooter.level();
        var damage = fireModeData.getTotalDamage();

        var damageSource = AVPDamageSources.INSTANCE.bullet.get().covertIndirectDamageSource(level, shooter);
        hitEntity.hurt(damageSource, damage);

        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            livingEntity.setLastHurtByMob(shooter);

            livingEntity.knockback(
                fireModeData.getTotalKnockback(),
                Mth.sin(shooter.getYRot() * Mth.DEG_TO_RAD),
                -Mth.cos(shooter.getYRot() * Mth.DEG_TO_RAD)
            );

            var bulletEffects = weaponItemStack.getBulletEffects();
            bulletEffects.forEach(bulletEffect -> bulletEffect.applyEffect(livingEntity));
        }
    }

    private void damageBlock(@NotNull Level level, BlockPos blockPos) {
        // Only damage blocks if both are true.
        if (!AVPConfig.General.GUNS_DO_BLOCK_DAMAGE)
            return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS))
            return;

        var blockState = level.getBlockState(blockPos);

        // Only damage blocks if they should be destroyed.
        if (blockState.is(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)) {
            return;
        }

        var damage = fireModeData.getTotalDamage();
        BlockBreakProgressManager.damage(level, blockPos, damage);
    }
}
