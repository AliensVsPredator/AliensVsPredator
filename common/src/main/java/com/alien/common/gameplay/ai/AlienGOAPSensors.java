package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.just.goap.Sensor;
import com.lib.common.gameplay.goap.GOAPKeys;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.common.util.AVPPredicates;

public class AlienGOAPSensors {

    public static final Sensor.Derived<Entity, List<LivingEntity>, List<LivingEntity>> NEARBY_HOSTS = Sensor.derived(
        AlienGOAPKeys.NEARBY_HOSTS,
        GOAPKeys.NEARBY_LIVING_ENTITIES,
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> AVPPredicates.isFreeHost(entity, e))
            .toList()
    );

    static final Sensor<Ovomorph, Boolean> HAS_NEARBY_HOST =
        Sensor.derived(AlienGOAPKeys.HAS_NEARBY_HOST, AlienGOAPKeys.NEARBY_HOSTS, (ovomorph, nearbyHosts) -> !nearbyHosts.isEmpty());

    private AlienGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
