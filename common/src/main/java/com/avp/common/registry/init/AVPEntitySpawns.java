package com.avp.common.registry.init;

import com.alien.common.gameplay.entity.living.alien.AlienSpawning;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.alien.common.registry.init.AlienEntityTypes;
import com.human.common.gameplay.entity.living.human.marine.MarineSpawning;
import com.human.common.registry.init.entity_type.HumanEntityTypes;
import com.predator.common.gameplay.entity.living.yautja.YautjaSpawning;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.minecraft.tags.BiomeTags;

import com.avp.AVP;
import com.avp.common.model.spawning.AVPEntitySpawnData;
import com.avp.common.registry.tag.AVPBiomeTags;
import com.avp.service.Services;

public class AVPEntitySpawns {

    public static void initialize() {
        registerAberrantAlienSpawns();
        registerIrradiatedAlienSpawns();
        registerNetherAlienSpawns();
        registerNormalAlienSpawns();

        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(PredatorEntityTypes.YAUTJA)
                .withBiomeTagKey(BiomeTags.IS_JUNGLE)
                .withSpawnPredicate(YautjaSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.YAUTJA_SPAWN)
                .build()
        );

        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(HumanEntityTypes.MARINE)
                .withSpawnPredicate(MarineSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.MARINE_SPAWN)
                // Prevents marine biome spawn configurations from being generated.
                .disableConfig()
                .build()
        );
    }

    private static void registerNormalAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.QUEEN)
                .withBiomeTagKey(BiomeTags.IS_OVERWORLD)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.QUEEN_SPAWN)
                .build()
        );
    }

    private static void registerAberrantAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.ABERRANT_QUEEN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.ABERRANT_QUEEN_SPAWN)
                // Prevents aberrant queen biome spawn configurations from being generated.
                .disableConfig()
                .build()
        );
    }

    private static void registerIrradiatedAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.IRRADIATED_DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.IRRADIATED_DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.IRRADIATED_WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.IRRADIATED_WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.IRRADIATED_PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.IRRADIATED_PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.IRRADIATED_QUEEN)
                .withBiomeTagKey(AVPBiomeTags.IS_IRRADIATED)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.IRRADIATED_QUEEN_SPAWN)
                .build()
        );
    }

    private static void registerNetherAlienSpawns() {
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_OVOMORPH)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_CHESTBURSTER)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_DRONE)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_DRONE_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_WARRIOR)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_PRAETORIAN)
                .withBiomeTagKey(AVPBiomeTags.HAS_XENOMORPHS)
                .withSpawnPredicate(AlienSpawning.getTypedPredicate())
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN)
                .build()
        );
        Services.REGISTRY.registerEntitySpawnData(
            AVPEntitySpawnData.builder(AlienEntityTypes.NETHER_QUEEN)
                .withBiomeTagKey(BiomeTags.IS_NETHER)
                .withSpawnPredicate(QueenSpawning.PREDICATE)
                .withSpawnSettings(AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN)
                .build()
        );
    }
}
