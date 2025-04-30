package com.avp.fabric.common.entity.spawn;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

import com.avp.AVPResources;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.entity.living.alien.chestburster.ChestbursterSpawning;
import com.avp.fabric.common.entity.living.alien.ovamorph.OvamorphSpawning;
import com.avp.fabric.common.entity.living.alien.xenomorph.drone.DroneSpawning;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.PraetorianSpawning;
import com.avp.fabric.common.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.avp.fabric.common.entity.living.alien.xenomorph.warrior.WarriorSpawning;
import com.avp.fabric.common.entity.living.human.marine.MarineSpawning;
import com.avp.fabric.common.entity.living.yautja.YautjaSpawning;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.util.AVPPredicates;

public class SpawnPlacements {

    private static final Predicate<BiomeSelectionContext> IS_JUNGLE = context -> context.hasTag(BiomeTags.IS_JUNGLE);

    private SpawnPlacements() {}

    public static void initialize() {
        var naturalSpawningEnabled = AVPFabric.config.spawnConfigs.NATURAL_SPAWNING_ENABLED;
        var adultSpawningEnabled = AVPFabric.config.spawnConfigs.ADULT_SPAWNING_ENABLED;
        var youngSpawningEnabled = AVPFabric.config.spawnConfigs.YOUNG_SPAWNING_ENABLED;
        var removableSpawnsEnabled = naturalSpawningEnabled && AVPFabric.config.spawnConfigs.REMOVE_VANILLA_SPAWNS;

        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (AVPFabric.config.spawnConfigs.YAUTJA_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.YAUTJA_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.YAUTJA_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.YAUTJA_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.YAUTJA, placement, heightMap, YautjaSpawning.PREDICATE);
            BiomeModifications.addSpawn(
                IS_JUNGLE,
                AVPEntityTypes.PREDATOR_CATEGORY,
                AVPEntityTypes.YAUTJA,
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

        net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.MARINE, placement, heightMap, MarineSpawning.PREDICATE);

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

        if (AVPFabric.config.spawnConfigs.DRONE_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.DRONE_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.DRONE_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.DRONE_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.DRONE, placement, heightMap, DroneSpawning.PREDICATE);
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.DRONE,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.PRAETORIAN_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.PRAETORIAN_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.PRAETORIAN_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.PRAETORIAN_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.PRAETORIAN,
                placement,
                heightMap,
                PraetorianSpawning.PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.PRAETORIAN,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.QUEEN_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.QUEEN_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.QUEEN_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.QUEEN_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.QUEEN, placement, heightMap, QueenSpawning.PREDICATE);
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.QUEEN,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.WARRIOR_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.WARRIOR_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.WARRIOR_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.WARRIOR_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.WARRIOR, placement, heightMap, WarriorSpawning.PREDICATE);
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.WARRIOR,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_DRONE_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_DRONE_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_DRONE_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_DRONE_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_DRONE,
                placement,
                heightMap,
                DroneSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_DRONE,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_PRAETORIAN_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_PRAETORIAN,
                placement,
                heightMap,
                PraetorianSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_PRAETORIAN,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_WARRIOR_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_WARRIOR_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_WARRIOR_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_WARRIOR_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_WARRIOR,
                placement,
                heightMap,
                WarriorSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_WARRIOR,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_QUEEN_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_QUEEN_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_QUEEN_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_QUEEN_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_QUEEN,
                placement,
                heightMap,
                QueenSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_QUEEN,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }

    private static void registerYoungXenomorphSpawns() {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (AVPFabric.config.spawnConfigs.CHESTBURSTER_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.CHESTBURSTER_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.CHESTBURSTER_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.CHESTBURSTER_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.CHESTBURSTER,
                placement,
                heightMap,
                ChestbursterSpawning.PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.CHESTBURSTER,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.OVAMORPH_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.OVAMORPH_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.OVAMORPH_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.OVAMORPH_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.OVAMORPH, placement, heightMap, OvamorphSpawning.PREDICATE);
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.OVAMORPH,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_CHESTBURSTER_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_CHESTBURSTER,
                placement,
                heightMap,
                ChestbursterSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_CHESTBURSTER,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (AVPFabric.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.enabled) {
            var maxGroupSize = AVPFabric.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.maxGroupSize;
            var minGroupSize = AVPFabric.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.minGroupSize;
            var weight = AVPFabric.config.spawnConfigs.NETHER_OVAMORPH_SPAWN.weight;

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_OVAMORPH,
                placement,
                heightMap,
                OvamorphSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                biomeSelectionContext -> biomeSelectionContext.hasTag(BiomeTags.IS_NETHER),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_OVAMORPH,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }
}
