package com.avp.common.entity.spawn;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.config.ConfigProperties;
import com.avp.common.config.property.ConfigPropertyContainer;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.chestburster.ChestbursterSpawning;
import com.avp.common.entity.living.alien.ovamorph.OvamorphSpawning;
import com.avp.common.entity.living.alien.xenomorph.drone.DroneSpawning;
import com.avp.common.entity.living.alien.xenomorph.praetorian.PraetorianSpawning;
import com.avp.common.entity.living.alien.xenomorph.queen.QueenSpawning;
import com.avp.common.entity.living.alien.xenomorph.warrior.WarriorSpawning;
import com.avp.common.entity.living.yautja.YautjaSpawning;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.AVPPredicates;

public class SpawnPlacements {

    private static final Predicate<BiomeSelectionContext> IS_JUNGLE = (context) -> context.hasTag(BiomeTags.IS_JUNGLE);

    public static void initialize() {
        var properties = AVP.SPAWNING_CONFIG.properties();
        var naturalSpawningEnabled = properties.getOrThrow(ConfigProperties.NATURAL_SPAWNING_ENABLED);
        var adultSpawningEnabled = naturalSpawningEnabled && properties.getOrThrow(ConfigProperties.ADULT_SPAWNING_ENABLED);
        var youngSpawningEnabled = naturalSpawningEnabled && properties.getOrThrow(ConfigProperties.YOUNG_SPAWNING_ENABLED);
        var removableSpawnsEnabled = naturalSpawningEnabled && properties.getOrThrow(ConfigProperties.REMOVE_VANILLA_SPAWNS);

        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.weight());

            net.minecraft.world.entity.SpawnPlacements.register(AVPEntityTypes.YAUTJA, placement, heightMap, YautjaSpawning.PREDICATE);
            BiomeModifications.addSpawn(IS_JUNGLE, MobCategory.MONSTER, AVPEntityTypes.YAUTJA, weight, minGroupSize, maxGroupSize);
        }

        if (adultSpawningEnabled) {
            registerAdultXenomorphSpawns(properties);
        }

        if (youngSpawningEnabled) {
            registerYoungXenomorphSpawns(properties);
        }

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

    private static void registerAdultXenomorphSpawns(ConfigPropertyContainer properties) {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (properties.getOrThrow(ConfigProperties.DRONE_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.DRONE_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.DRONE_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.DRONE_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().weight());

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_DRONE,
                placement,
                heightMap,
                DroneSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_DRONE,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (properties.getOrThrow(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().weight());

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_PRAETORIAN,
                placement,
                heightMap,
                PraetorianSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_PRAETORIAN,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (properties.getOrThrow(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().weight());

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_WARRIOR,
                placement,
                heightMap,
                WarriorSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_WARRIOR,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }

    private static void registerYoungXenomorphSpawns(ConfigPropertyContainer properties) {
        var placement = SpawnPlacementTypes.ON_GROUND;
        var heightMap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

        if (properties.getOrThrow(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().weight());

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

        if (properties.getOrThrow(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().weight());

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_CHESTBURSTER,
                placement,
                heightMap,
                ChestbursterSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_CHESTBURSTER,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }

        if (properties.getOrThrow(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().enabled())) {
            var maxGroupSize = properties.getOrThrow(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().maxGroupSize());
            var minGroupSize = properties.getOrThrow(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().minGroupSize());
            var weight = properties.getOrThrow(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().weight());

            net.minecraft.world.entity.SpawnPlacements.register(
                AVPEntityTypes.NETHER_OVAMORPH,
                placement,
                heightMap,
                OvamorphSpawning.NETHER_PREDICATE
            );
            BiomeModifications.addSpawn(
                AVPPredicates.alwaysTrue(),
                AVPEntityTypes.ALIEN_CATEGORY,
                AVPEntityTypes.NETHER_OVAMORPH,
                weight,
                minGroupSize,
                maxGroupSize
            );
        }
    }
}
