package com.avp.common.util;

import com.avp.AVP;
import com.avp.common.command.nuke.ExplosionProgressTracker;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.explosion.nuke.NuclearExplosionEffects;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.avp.common.explosion.Explosion;

public class ExplosionUtil {

    public static double getNormalizedHorizontalDistance(Explosion explosion, int x, int z) {
        var radiusX = explosion.config().largestRadius(Direction.Axis.X);
        var radiusZ = explosion.config().largestRadius(Direction.Axis.Z);

        return (x * x) / (double) (radiusX * radiusX) +
            (z * z) / (double) (radiusZ * radiusZ);
    }

    public static double getNormalizedVerticalDistance(Explosion explosion, int y) {
        var radiusYDown = explosion.config().radius(Direction.DOWN);
        var radiusYUp = explosion.config().radius(Direction.UP);

        return (y < 0
            ? (y * y) / (double) (radiusYDown * radiusYDown)
            : (y * y) / (double) (radiusYUp * radiusYUp));
    }

    public static double getNormalizedDistance(Explosion explosion, int x, int y, int z) {
        return getNormalizedHorizontalDistance(explosion, x, z) + getNormalizedVerticalDistance(explosion, y);
    }

    public static List<Entity> getEntitiesInRadius(ServerLevel level, Vec3 center, double radius) {
        var boundingBox = new AABB(
            center.x - radius,
            center.y - radius,
            center.z - radius,
            center.x + radius,
            center.y + radius,
            center.z + radius
        );

        return level.getEntities(null, boundingBox);
    }

    public static double computeDamage(double radius, double minimumDamage, double maximumDamage, double distance) {
        var damageFactor = 1.0 - (distance / radius);
        return Math.max(minimumDamage, maximumDamage * damageFactor);
    }

    public static void applyKnockback(Vec3 center, double radius, Entity entity, double maxKnockback, double distance) {
        var direction = entity.position().subtract(center).normalize();
        var knockbackStrength = maxKnockback * (1.0 - (distance / (radius * radius)));
        // Prevent excessive knockback (cap velocity)
        var knockbackVelocity = direction.scale(Math.max(maxKnockback, knockbackStrength));

        entity.setDeltaMovement(knockbackVelocity);
        entity.hurtMarked = true; // Ensure physics applies immediately
    }

    public static Explosion createNuclearExplosion(ServerLevel level, Vec3 center, int radius, int maxKnockback) {
        var progressTracker = new ExplosionProgressTracker();
        var nuclearExplosionEffects = new NuclearExplosionEffects();

        return Explosion.builder(level, center)
                .withRadius(Direction.Plane.HORIZONTAL, radius)
                .withRadius(Direction.UP, radius / 2)
                .withRadius(Direction.DOWN, 16 * 2)
                .onExplosionStart(() -> {
                    progressTracker.startTimer();

                    var entities = ExplosionUtil.getEntitiesInRadius(level, center, radius);

                    for (var entity : entities) {
                        var distance = entity.distanceToSqr(center);
                        var damage = ExplosionUtil.computeDamage(radius, 5, 1000, distance);
                        if (entity instanceof Alien alien) {
                            alien.setIrraiated(true);
                        }
                        entity.igniteForSeconds(15);
                        entity.hurt(level.damageSources().explosion(null), (float) damage);
                        ExplosionUtil.applyKnockback(center, radius, entity, maxKnockback, distance);
                    }
                })
                .onBlockSample(($, pos) -> {
                    nuclearExplosionEffects.apply($, pos);
                    progressTracker.incrementBlockDestroyCounter();
                })
                .onExplosionFinish(() -> {
                    progressTracker.stopTimer();

                    var timeTakenInMillis = progressTracker.timeTaken();
                    var timeTakenInTicks = timeTakenInMillis / 50;

                    AVP.LOGGER.info(
                            "Explosion @ {} completed in {}ms ({} ticks), destroying {} blocks!",
                            center,
                            timeTakenInMillis,
                            timeTakenInTicks,
                            progressTracker.blocksDestroyed()
                    );
                })
                .build();
    }
}
