package com.lib.common.gameplay.goap;

import com.just.goap.Sensor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.common.util.AVPPredicates;

public class GOAPSensors {

    public static final Sensor.Direct<Entity, List<Entity>> NEARBY_ENTITIES = Sensor.direct(
        GOAPKeys.NEARBY_ENTITIES,
        entity -> entity.level()
            .getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(16), AVPPredicates.alwaysTrue())
    );

    public static final Sensor.Derived<Entity, List<LivingEntity>, List<Entity>> NEARBY_LIVING_ENTITIES = Sensor.derived(
        GOAPKeys.NEARBY_LIVING_ENTITIES,
        GOAPKeys.NEARBY_ENTITIES,
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> e instanceof LivingEntity)
            .map(e -> (LivingEntity) e)
            .toList()
    );

    private GOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
