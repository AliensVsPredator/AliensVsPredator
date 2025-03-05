package com.avp.common.entity.living.alien.ovamorph;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.AlienSpawning;

public class OvamorphSpawning {

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.OVAMORPH_SPAWNING
    );

    public static final SpawnPlacements.SpawnPredicate<Ovamorph> NETHER_PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.NETHER_OVAMORPH_SPAWNING
    );
}
