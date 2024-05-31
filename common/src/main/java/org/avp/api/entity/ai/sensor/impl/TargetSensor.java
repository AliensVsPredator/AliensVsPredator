package org.avp.api.entity.ai.sensor.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.GOAPBrainCache;
import org.avp.api.entity.ai.sensor.Sensor;
import org.avp.api.entity.ai.sensor.key.SensorKeys;

import java.util.Comparator;
import java.util.function.Predicate;

public class TargetSensor extends Sensor {

    private final Mob mob;

    private final Predicate<LivingEntity> targetPredicate;

    public TargetSensor(GOAPBrainCache goapBrainCache, Mob mob, Predicate<LivingEntity> targetPredicate) {
        super(goapBrainCache);
        this.mob = mob;
        this.targetPredicate = targetPredicate;
    }

    @Override
    public void sense() {
        if (mob.getTarget() == null || mob.getTarget().isDeadOrDying() || !targetPredicate.test(mob.getTarget())) {
            goapBrainCache.remove(SensorKeys.TARGET);
        }

        var nearbyLivingEntitiesOptional = goapBrainCache.get(SensorKeys.NEARBY_LIVING_ENTITIES);

        var closestTargetOptional = nearbyLivingEntitiesOptional.flatMap(nearbyLivingEntities -> nearbyLivingEntities.stream()
            .filter(targetPredicate)
            .min(Comparator.comparingDouble(mob::distanceToSqr)));

        closestTargetOptional.ifPresent(closestTarget -> {
            goapBrainCache.cache(SensorKeys.TARGET, closestTarget);
            mob.setTarget(closestTarget);
        });
    }
}
