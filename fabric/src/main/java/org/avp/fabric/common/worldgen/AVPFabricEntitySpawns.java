package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;

import org.avp.common.config.AVPConfig;
import org.avp.common.registry.AVPEntityDataRegistry;
import org.avp.common.data.tag.AVPEntityTypeTags;

public class AVPFabricEntitySpawns {

    @SuppressWarnings("unchecked")
    public static void addEntitySpawns() {
        AVPEntityDataRegistry.INSTANCE.getMobEntries().forEach(entityData -> {
            var entityType = (EntityType<Mob>) entityData.getHolder().get();
            var spawnDataOptional = entityData.getSpawnData();
            spawnDataOptional.ifPresent(spawnData -> {
                var biomeTag = spawnData.biomeTagKey();
                var weight = spawnData.weight();
                var minGroupSize = spawnData.minGroupSize();
                var maxGroupSize = spawnData.maxGroupSize();

                if (entityType.is(AVPEntityTypeTags.ALIENS) && !AVPConfig.Aliens.ENABLE_XENOMORPH_OVERWORLD_SPAWNS) {
                    return;
                }

                if (entityType.is(AVPEntityTypeTags.PREDATORS) && !AVPConfig.Predators.ENABLE_YAUTJA_OVERWORLD_SPAWNS) {
                    return;
                }

                // Register biome spawns.
                BiomeModifications.addSpawn(
                    context -> context.hasTag(biomeTag),
                    MobCategory.MONSTER,
                    entityType,
                    weight,
                    minGroupSize,
                    maxGroupSize
                );

                // Register spawn placements.
                var placementType = spawnData.spawnPlacementType();
                var heightMapType = spawnData.heightMapType();
                var predicate = (SpawnPlacements.SpawnPredicate<Mob>) spawnData.spawnPredicate();
                SpawnPlacements.register(entityType, placementType, heightMapType, predicate);
            });
        });
    }

    private AVPFabricEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
