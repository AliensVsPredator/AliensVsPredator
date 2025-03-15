package com.avp.common.util;

import com.avp.common.entity.projectile.BulletProjectile;
import com.avp.common.sound.AVPSoundEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;

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
            var targetY = entity.getTarget().getY(0.5);
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
}
