package org.avp.api.entity.ai.sensor.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.GOAPBrainCache;
import org.avp.api.entity.ai.sensor.Sensor;
import org.avp.api.entity.ai.sensor.key.SensorKeys;

import java.util.List;
import java.util.function.Predicate;

public class ClosestTargetSensor extends Sensor {

    private final Mob mob;

    private final Predicate<LivingEntity> targetPredicate;

    public ClosestTargetSensor(GOAPBrainCache goapBrainCache, Mob mob, Predicate<LivingEntity> targetPredicate) {
        this(goapBrainCache, mob, targetPredicate, true);
    }

    public ClosestTargetSensor(GOAPBrainCache goapBrainCache, Mob mob, Predicate<LivingEntity> targetPredicate, boolean checkLineOfSight) {
        super(goapBrainCache);
        this.mob = mob;
        this.targetPredicate = targetPredicate.and(livingEntity -> {
            if (checkLineOfSight) {
                return mob.getSensing().hasLineOfSight(livingEntity);
            }

            return true;
        });
    }

    @Override
    public void sense() {
        if (mob.getTarget() == null || mob.getTarget().isDeadOrDying() || !targetPredicate.test(mob.getTarget())) {
            goapBrainCache.remove(SensorKeys.TARGET);
        }

        var nearbyLivingEntitiesOptional = goapBrainCache.get(SensorKeys.NEARBY_LIVING_ENTITIES);
        var nearbyLivingEntities = nearbyLivingEntitiesOptional.orElse(List.of());
        var targetEntities = nearbyLivingEntities.stream().filter(targetPredicate).toList();

        var minimumDistance = -1.0;
        LivingEntity closestTarget = null;

        for (var target: targetEntities) {
            var distance = mob.distanceToSqr(target);

            if (minimumDistance == -1.0 || distance < minimumDistance) {
                minimumDistance = distance;
                closestTarget = target;
            }
        }

        goapBrainCache.cache(SensorKeys.TARGET, closestTarget);
        mob.setTarget(closestTarget);
    }
}
