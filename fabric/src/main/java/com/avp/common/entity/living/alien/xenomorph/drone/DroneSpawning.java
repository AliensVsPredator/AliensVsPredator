package com.avp.common.entity.living.alien.xenomorph.drone;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.AVP;
import com.avp.common.entity.living.alien.AlienSpawning;

public class DroneSpawning {

    public static final SpawnPlacements.SpawnPredicate<Drone> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.DRONE_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Drone> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_DRONE_SPAWN
    );
}
