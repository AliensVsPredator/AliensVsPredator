package com.alien.common.gameplay.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.just.goap.sensor.Sensor;
import com.just.goap.sensor.Sensors;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

import com.avp.common.util.AVPPredicates;

public class AlienGOAPSensors {

    public static final Sensor.Mono<Entity, List<LivingEntity>> NEARBY_HOSTS = Sensors.compose(
        GOAPSensors.NEARBY_LIVING_ENTITIES.key(),
        AlienGOAPKeys.NEARBY_HOSTS,
        (entity, nearbyEntities) -> nearbyEntities.stream()
            .filter(e -> AVPPredicates.isFreeHost(entity, e))
            .toList()
    );

    static final Sensor.Mono<Ovomorph, Boolean> HAS_NEARBY_HOST =
        Sensors.compose(AlienGOAPKeys.NEARBY_HOSTS, AlienGOAPKeys.HAS_NEARBY_HOST, (ovomorph, nearbyHosts) -> !nearbyHosts.isEmpty());

    private AlienGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
