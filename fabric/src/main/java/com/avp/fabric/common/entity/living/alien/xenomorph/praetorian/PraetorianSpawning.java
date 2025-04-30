package com.avp.fabric.common.entity.living.alien.xenomorph.praetorian;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVP;
import com.avp.fabric.common.entity.living.alien.AlienSpawning;

public class PraetorianSpawning {

    public static final SpawnPlacements.SpawnPredicate<Praetorian> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.PRAETORIAN_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Praetorian> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN
    );
}
