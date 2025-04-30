package com.avp.fabric.common.entity.living.alien.xenomorph.drone;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.entity.living.alien.AlienSpawning;

public class DroneSpawning {

    public static final SpawnPlacements.SpawnPredicate<Drone> PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.DRONE_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Drone> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.NETHER_DRONE_SPAWN
    );
}
