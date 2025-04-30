package com.avp.fabric.common.entity.living.alien.xenomorph.warrior;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.entity.living.alien.AlienSpawning;

public class WarriorSpawning {

    public static final SpawnPlacements.SpawnPredicate<Warrior> PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.WARRIOR_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Warrior> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.NETHER_WARRIOR_SPAWN
    );
}
