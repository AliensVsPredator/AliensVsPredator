package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

import net.minecraft.world.entity.SpawnPlacements;
import org.avp.common.config.AVPConfig;
import org.avp.common.entity.AVPBaseAlienEntityTypes;
import org.avp.common.entity.spawn.AVPEntitySpawns;

/**
 * @author Boston Vanseghi
 */
public class AVPFabricEntitySpawns {

    @SuppressWarnings("unchecked")
    public static void addEntitySpawns() {
        AVPEntitySpawns.getEntries().forEach(entitySpawnData -> {
            var weight = entitySpawnData.weight();
            var minGroupSize = entitySpawnData.minGroupSize();
            var maxGroupSize = entitySpawnData.maxGroupSize();
            var entityType = (EntityType<Mob>) entitySpawnData.entityTypeGameObject().get();

            BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(), // TODO: Make this generic.
                MobCategory.MONSTER,
                entityType,
                weight,
                minGroupSize,
                maxGroupSize
            );


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
