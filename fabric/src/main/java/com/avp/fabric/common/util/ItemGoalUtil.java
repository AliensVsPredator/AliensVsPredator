package com.avp.fabric.common.util;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.TempAVPPredicates;
import com.avp.fabric.common.entity.projectile.BulletProjectile;
import com.avp.fabric.common.entity.projectile.ShurikenItemEntity;
import com.avp.fabric.common.entity.projectile.SmartDiscItemEntity;
import com.avp.fabric.common.item.AVPItems;

@Deprecated(forRemoval = true)
public class ItemGoalUtil {

    public static void shootBullet(PathfinderMob entity) {
        // TODO: Change sound effect here.
        entity.level()
            .playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                AVPSoundEvents.WEAPON_GENERIC_SHOOT.get(),
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

    public static void shootShuriken(PathfinderMob entity) {
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

            var shurikenItemEntity = new ShurikenItemEntity(entity.level(), entity);
            shurikenItemEntity.setOwner(entity);
            shurikenItemEntity.setPos(sourceX, sourceY, sourceZ);
            var velocity = 1.5F;
            shurikenItemEntity.setDeltaMovement(directionX * velocity, directionY * velocity, directionZ * velocity);

            entity.level().addFreshEntity(shurikenItemEntity);
        }
    }

    public static void shootSmartDisc(PathfinderMob entity) {
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

            var smartDiscItemEntity = new SmartDiscItemEntity(entity.level(), entity);
            smartDiscItemEntity.setOwner(entity);
            smartDiscItemEntity.setPos(sourceX, sourceY, sourceZ);
            var velocity = 3.5F;
            smartDiscItemEntity.setDeltaMovement(directionX * velocity, directionY * velocity, directionZ * velocity);
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
                    ) && !TempAVPPredicates.IS_IMMORTAL.test(livingEntity) && livingEntity != projectile.getOwner()
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

    public static void trackToOwnerEntity(Projectile projectile) {
        if (projectile.getOwner() == null) {
            return;
        }

        var vec3 = projectile.getOwner().getEyePosition().subtract(projectile.position());

        projectile.setPosRaw(projectile.getX(), projectile.getY() + vec3.y * 0.015, projectile.getZ());

        if (projectile.level().isClientSide) {
            projectile.yOld = projectile.getY();
        }

        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(0.5)));

        if (
            projectile.getOwner() instanceof Player player && projectile.getBoundingBox().intersects(projectile.getOwner().getBoundingBox())
        ) {
            if (!TempAVPPredicates.IS_IMMORTAL.test(player)) {
                player.getInventory().add(AVPItems.SMART_DISC.getDefaultInstance());
            }
            projectile.kill();
        } else if (projectile.getBoundingBox().intersects(projectile.getOwner().getBoundingBox())) {
            projectile.kill();
        }
    }
}
