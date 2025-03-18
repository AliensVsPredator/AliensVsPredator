package com.avp.common.entity.living.alien.chestburster;

import com.avp.AVP;
import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.entity.living.alien.AlienSpawning;

public class ChestbursterSpawning {

    public static final SpawnPlacements.SpawnPredicate<Chestburster> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.CHESTBURSTER_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Chestburster> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN
    );
}
