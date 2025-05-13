package com.avp.common.entity.spawning;

import net.minecraft.tags.BiomeTags;

import com.avp.AVP;
import com.avp.common.entity.living.alien.AlienSpawning;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.avp.common.entity.living.human.marine.MarineSpawning;
import com.avp.common.entity.living.yautja.YautjaSpawning;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.worldgen.biome.AVPBiomeTags;
import com.avp.service.Services;

public class AVPEntitySpawns {

    public static void initialize() {
        registerAberrantAlienSpawns();
        registerNormalAlienSpawns();
        registerNetherAlienSpawns();

        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.YAUTJA)
                .withBiomeTagKey(BiomeTags.IS_JUNGLE)
                .withSpawnPredicate(YautjaSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.YAUTJA_SPAWN)
                .build()
        );

        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.MARINE)
                .withSpawnPredicate(MarineSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.MARINE_SPAWN)
                // Prevents marine biome spawn configurations from being generated.
                .disableConfig()
                .build()
        );
    }

    private static void registerNormalAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.QUEEN)
                .withBiomeTagKey(BiomeTags.IS_OVERWORLD)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.QUEEN_SPAWN)
                .build()
        );
    }

    private static void registerAberrantAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.ABERRANT_QUEEN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_QUEEN_SPAWN)
                .build()
        );
    }

    private static void registerNetherAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AVPEntityTypes.NETHER_QUEEN)
                .withBiomeTagKey(BiomeTags.IS_NETHER)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN)
                .build()
        );
    }
}
