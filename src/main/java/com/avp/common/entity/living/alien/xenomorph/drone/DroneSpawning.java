package com.avp.common.entity.living.alien.xenomorph.drone;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.AlienSpawning;

public class DroneSpawning {

    public static final SpawnPlacements.SpawnPredicate<Drone> PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.DRONE_SPAWNING
    );

    public static final SpawnPlacements.SpawnPredicate<Drone> NETHER_PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.NETHER_DRONE_SPAWNING
    );
}
