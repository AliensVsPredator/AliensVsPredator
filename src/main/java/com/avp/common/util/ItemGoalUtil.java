package com.avp.common.util;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.projectile.BulletProjectile;
import com.avp.common.entity.projectile.ShurikenItemEntity;
import com.avp.common.entity.projectile.SmartDiscItemEntity;
import com.avp.common.sound.AVPSoundEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class ItemGoalUtil {

    public static void shootBullet(PathfinderMob entity) {
        // TODO: Change sound effect here.
        entity.level()
                .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        AVPSoundEvents.WEAPON_GENERIC_SHOOT,
                        SoundSource.PLAYERS,
                        0.5F,
                        0.4F / (entity.level().getRandom().nextFloat() * 0.4F + 0.8F)
                );
        if (!entity.level().isClientSide && entity.getTarget() != null) {
            var targetX = entity.getTarget().getX();
            var targetY = entity.getTarget().getY(1.0);
            var targetZ = entity.getTarget().getZ();
            var sourceX = entity.getX();
            var sourceY = entity.getY(0.5);
            var sourceZ = entity.getZ();
            var directionX = targetX - sourceX;
            var directionY = targetY - sourceY;
            var directionZ = targetZ - sourceZ;
            var length = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
            directionX /= length;
            directionY /= length;
            directionZ /= length;

            var bulletProjectile = new BulletProjectile(entity.level());
            bulletProjectile.setOwner(entity);
            bulletProjectile.setPos(sourceX, sourceY, sourceZ);

            var velocity = 1.5F;
            bulletProjectile.setDeltaMovement(directionX * velocity, directionY * velocity, directionZ * velocity);
            bulletProjectile.setRemainingFireTicks(0);
            bulletProjectile.extinguishFire();

            entity.level().addFreshEntity(bulletProjectile);
        }
    }

    public static void shootShuriken(LivingEntity entity) {
        // TODO: Change sound effect here.
        entity.level()
                .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.TRIDENT_THROW,
                        SoundSource.PLAYERS,
                        0.5F,
                        0.4F / (entity.level().getRandom().nextFloat() * 0.4F + 0.8F)
                );
        if (!entity.level().isClientSide) {
            var shurikenItemEntity = new ShurikenItemEntity(entity.level(), entity);
            shurikenItemEntity.setItem(entity.getUseItem());
            shurikenItemEntity.setOwner(entity);
            shurikenItemEntity.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.5F, 1.0F);
            entity.level().addFreshEntity(shurikenItemEntity);
        }
    }

    public static void shootSmartDisc(LivingEntity entity) {
        // TODO: Change sound effect here.
        entity.level()
                .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.TRIDENT_THROW,
                        SoundSource.PLAYERS,
                        0.5F,
                        0.4F / (entity.level().getRandom().nextFloat() * 0.4F + 0.8F)
                );

        if (!entity.level().isClientSide) {
            var smartDiscItemEntity = new SmartDiscItemEntity(entity.level(), entity);
            smartDiscItemEntity.setItem(entity.getUseItem());
            smartDiscItemEntity.setOwner(entity);
            smartDiscItemEntity.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 3.5F, 1.0F);
            entity.level().addFreshEntity(smartDiscItemEntity);
        }
    }

    public static void trackToLivingEntity(Projectile projectile, Double bulletSpeed, Boolean highLightMob) {
        var livingEntities = projectile.level()
                .getEntitiesOfClass(
                        LivingEntity.class,
                        projectile.getBoundingBox().inflate(5),
                        livingEntity -> !livingEntity.getType()
                                .is(
                                        AVPEntityTypeTags.PREDATORS
                                ) && !AVPPredicates.IS_IMMORTAL.test(livingEntity) && livingEntity != projectile.getOwner()
                );
        if (!livingEntities.isEmpty()) {
            var first = livingEntities.getFirst();
            if (Boolean.TRUE.equals(highLightMob))
                first.setGlowingTag(true);
            var entityPos = new Vec3(first.getX(), first.getY() + first.getEyeHeight(), first.getZ());
            var directionToTarget = entityPos.subtract(projectile.position()).normalize();
            var newVelocity = directionToTarget.scale(bulletSpeed);

            projectile.setDeltaMovement(newVelocity);
        }
    }
}
