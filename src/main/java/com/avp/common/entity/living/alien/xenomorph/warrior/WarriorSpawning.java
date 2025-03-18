package com.avp.common.entity.living.alien.xenomorph.warrior;

import com.avp.AVP;
import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.entity.living.alien.AlienSpawning;

public class WarriorSpawning {

    public static final SpawnPlacements.SpawnPredicate<Warrior> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.WARRIOR_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Warrior> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN
    );
}
