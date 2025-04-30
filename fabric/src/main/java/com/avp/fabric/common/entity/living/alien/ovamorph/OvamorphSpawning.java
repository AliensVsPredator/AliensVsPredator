package com.avp.fabric.common.entity.living.alien.ovamorph;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.entity.living.alien.AlienSpawning;

public class OvamorphSpawning {

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.OVAMORPH_SPAWN
    );

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> NETHER_PREDICATE = AlienSpawning.createPredicate(
        AVPFabric.config.spawnConfigs.NETHER_OVAMORPH_SPAWN
    );
}
