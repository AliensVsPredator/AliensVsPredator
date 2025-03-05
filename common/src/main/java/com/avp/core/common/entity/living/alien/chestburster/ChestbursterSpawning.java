package com.avp.core.common.entity.living.alien.chestburster;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.AlienSpawning;

public class ChestbursterSpawning {

    public static final SpawnPlacements.SpawnPredicate<Chestburster> PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.CHESTBURSTER_SPAWNING
    );

    public static final SpawnPlacements.SpawnPredicate<Chestburster> NETHER_PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.NETHER_CHESTBURSTER_SPAWNING
    );
}
