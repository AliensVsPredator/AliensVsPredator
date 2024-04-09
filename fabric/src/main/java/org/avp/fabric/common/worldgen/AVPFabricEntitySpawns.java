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

    public static void addEntitySpawns() {
        if (AVPConfig.Aliens.ENABLE_XENOMORPH_OVERWORLD_SPAWNS) {
            // TODO: Expand this to support all types of xenos spawning.
            BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER,
                AVPBaseAlienEntityTypes.DRONE.get(),
                100,
                1,
                3
            );
        }

        addEntitySpawnChecks();
    }

    @SuppressWarnings("unchecked")
    private static void addEntitySpawnChecks() {
        AVPEntitySpawns.getEntries().forEach(entitySpawnData -> {
            var entityType = (EntityType<Mob>) entitySpawnData.entityTypeGameObject().get();
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
