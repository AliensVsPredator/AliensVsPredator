package com.avp.fabric.common.entity.living.alien.chestburster;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.entity.living.alien.AlienSpawning;

public class ChestbursterSpawning {

    public static final SpawnPlacements.SpawnPredicate<Chestburster> PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.CHESTBURSTER_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Chestburster> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN
    );
}
