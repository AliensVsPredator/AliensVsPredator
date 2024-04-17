package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;

import org.avp.common.config.AVPConfig;
import org.avp.common.entity.spawn.AVPEntitySpawns;
import org.avp.common.tag.AVPEntityTags;

public class AVPFabricEntitySpawns {

    @SuppressWarnings("unchecked")
    public static void addEntitySpawns() {
        AVPEntitySpawns.INSTANCE.getEntries().forEach(entitySpawnDataHolder -> {
            var entitySpawnData = entitySpawnDataHolder.get();
            var weight = entitySpawnData.weight();
            var minGroupSize = entitySpawnData.minGroupSize();
            var maxGroupSize = entitySpawnData.maxGroupSize();
            var entityType = (EntityType<Mob>) entitySpawnData.entityTypeGameObject().get();

            if (entityType.is(AVPEntityTags.ALIENS) && !AVPConfig.Aliens.ENABLE_XENOMORPH_OVERWORLD_SPAWNS) {
                return;
            }

            if (entityType.is(AVPEntityTags.PREDATORS) && !AVPConfig.Predators.ENABLE_YAUTJA_OVERWORLD_SPAWNS) {
                return;
            }

            // Register biome spawns.
            BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(), // TODO: Make this generic.
                MobCategory.MONSTER,
                entityType,
                weight,
                minGroupSize,
                maxGroupSize
            );

            // Register spawn placements.
            var placementType = entitySpawnData.spawnPlacementType();
            var heightMapType = entitySpawnData.heightMapType();
            var predicate = (SpawnPlacements.SpawnPredicate<Mob>) entitySpawnData.spawnPredicate();
            SpawnPlacements.register(entityType, placementType, heightMapType, predicate);
        });
    }

    private AVPFabricEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
