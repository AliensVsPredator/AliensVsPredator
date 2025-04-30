package com.avp.common.entity.living.alien.xenomorph.warrior;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.AVP;
import com.avp.common.entity.living.alien.AlienSpawning;

public class WarriorSpawning {

    public static final SpawnPlacements.SpawnPredicate<Warrior> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.WARRIOR_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Warrior> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN
    );
}
