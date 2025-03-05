package com.avp.common.entity.living.alien.xenomorph.praetorian;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.config.ConfigProperties;
import com.avp.common.entity.living.alien.AlienSpawning;

public class PraetorianSpawning {

    public static final SpawnPlacements.SpawnPredicate<Praetorian> PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.PRAETORIAN_SPAWNING
    );

    public static final SpawnPlacements.SpawnPredicate<Praetorian> NETHER_PREDICATE = AlienSpawning.createPredicate(
        ConfigProperties.NETHER_PRAETORIAN_SPAWNING
    );
}
