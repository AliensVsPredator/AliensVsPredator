package com.avp.core.common.entity.living.alien.xenomorph.warrior;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.AlienSpawning;

public class WarriorSpawning {

    public static final SpawnPlacements.SpawnPredicate<Warrior> PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.WARRIOR_SPAWNING
    );

    public static final SpawnPlacements.SpawnPredicate<Warrior> NETHER_PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.NETHER_WARRIOR_SPAWNING
    );
}
