package com.avp.fabric.common.entity.spawn;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.AlienSpawning;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.avp.common.entity.living.human.marine.MarineSpawning;
import com.avp.common.entity.living.yautja.YautjaSpawning;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.AVPPredicates;

public class AVPSpawnPlacements {

    private static final Predicate<BiomeSelectionContext> IS_JUNGLE = context -> context.hasTag(BiomeTags.IS_JUNGLE);

    private AVPSpawnPlacements() {}

    public static void initialize() {
        var naturalSpawningEnabled = AVP.config.spawnConfigs.NATURAL_SPAWNING_ENABLED;
        var adultSpawningEnabled = AVP.config.spawnConfigs.ADULT_SPAWNING_ENABLED;
        var youngSpawningEnabled = AVP.config.spawnConfigs.YOUNG_SPAWNING_ENABLED;
        var removableSpawnsEnabled = naturalSpawningEnabled && AVP.config.spawnConfigs.REMOVE_VANILLA_SPAWNS;

        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (AVP.config.spawnConfigs.YAUTJA_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.YAUTJA_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.YAUTJA_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.YAUTJA_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.YAUTJA.get(),
                placement,
                heightMap,
                YautjaSpawning.PREDICATE
            );
            BiomeModifications.addSpawn(
                IS_JUNGLE,
                AVPEntityTypes.PREDATOR_CATEGORY,
                AVPEntityTypes.YAUTJA.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (adultSpawningEnabled) {
            registerAdultXenomorphSpawns();
        }

        if (youngSpawningEnabled) {
            registerYoungXenomorphSpawns();
        }

        SpawnPlacements.register(
            AVPEntityTypes.MARINE.get(),
            placement,
            heightMap,
            MarineSpawning.PREDICATE
        );

        if (removableSpawnsEnabled) {
            BiomeModifications.create(AVPResources.location("remove_entity_spawns"))
                .add(
                    ModificationPhase.REMOVALS,
                    selectionContext -> true,
                    (biomeSelectionContext, modificationContext) -> modificationContext.getSpawnSettings()
                        .removeSpawns((spawnGroup, spawnEntry) -> spawnEntry.type.is(AVPEntityTypeTags.REMOVE_VANILLA_SPAWNS))
                );
        }
    }

    private static void registerAdultXenomorphSpawns() {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (AVP.config.spawnConfigs.DRONE_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.DRONE_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.DRONE_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.DRONE_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.DRONE.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.DRONE.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.PRAETORIAN_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.PRAETORIAN_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.PRAETORIAN_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.PRAETORIAN_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.PRAETORIAN.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.PRAETORIAN.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.QUEEN_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.QUEEN_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.QUEEN_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.QUEEN_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.QUEEN.get(),
                placement,
                heightMap,
                QueenSpawning.PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.QUEEN.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.WARRIOR_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.WARRIOR_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.WARRIOR_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.WARRIOR_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.WARRIOR.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.WARRIOR.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_DRONE_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_DRONE_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_DRONE_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_DRONE_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_DRONE.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_DRONE.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_WARRIOR_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_WARRIOR.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_WARRIOR.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_QUEEN.get(),
                placement,
                heightMap,
                QueenSpawning.PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_QUEEN.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }

    private static void registerYoungXenomorphSpawns() {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (AVP.config.spawnConfigs.CHESTBURSTER_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.CHESTBURSTER_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.CHESTBURSTER_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.CHESTBURSTER_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.CHESTBURSTER.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.CHESTBURSTER.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.OVAMORPH_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.OVAMORPH_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.OVAMORPH_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.OVAMORPH_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.OVOMORPH.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.OVOMORPH.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_CHESTBURSTER.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_CHESTBURSTER.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.enabled) {
            var maxGroupSize = AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.maxGroupSize;
            var minGroupSize = AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.minGroupSize;
            var weight = AVP.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.weight;

            SpawnPlacements.register(
                AVPEntityTypes.NETHER_OVOMORPH.get(),
                placement,
                heightMap,
                AlienSpawning.getTypedPredicate()
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_OVOMORPH.get(),
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }
}
