package com.avp.common.entity.living.alien.ovamorph;

import com.avp.AVP;
import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.entity.living.alien.AlienSpawning;

public class OvamorphSpawning {

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.OVAMORPH_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN
    );
}
