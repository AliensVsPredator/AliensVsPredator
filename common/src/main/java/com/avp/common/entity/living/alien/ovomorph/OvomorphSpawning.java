package com.avp.common.entity.living.alien.ovomorph;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.AVP;
import com.avp.common.entity.living.alien.AlienSpawning;

public class OvomorphSpawning {

    public static final SpawnPlacements.SpawnPredicate<Ovomorph> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.OVAMORPH_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Ovomorph> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN
    );
}
