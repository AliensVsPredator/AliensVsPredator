package org.avp.neoforge.common.entity;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.avp.common.entity.spawn.AVPEntitySpawns;

public class AVPNeoForgeEntitySpawns {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AVPNeoForgeEntitySpawns::bootstrap);

    private static void bootstrap(BootstapContext<BiomeModifier> context) {
        AVPEntitySpawns.INSTANCE.getEntries().forEach(entitySpawnDataHolder -> {
            var entitySpawnData = entitySpawnDataHolder.get();
            var key = createThrowawayRegistryKey(entitySpawnDataHolder.getResourceLocation());

            var biomes = context.lookup(Registries.BIOME);
            // TODO: Make this different per entity spawn data.
            var biomeNamed = biomes.get(BiomeTags.IS_JUNGLE).orElseThrow();

            var entityType = entitySpawnData.entityTypeHolder().get();
            var weight = entitySpawnData.weight();
            var minGroupSize = entitySpawnData.minGroupSize();
            var maxGroupSize = entitySpawnData.maxGroupSize();

            var spawnsBiomeModifier = BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                biomeNamed,
                new MobSpawnSettings.SpawnerData(
                    entityType, weight, minGroupSize, maxGroupSize
                )
            );

            context.register(key, spawnsBiomeModifier);
        });
    }

    private static ResourceKey<BiomeModifier> createThrowawayRegistryKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, resourceLocation);
    }

    @SuppressWarnings("unchecked")
    public static void handleSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
        AVPEntitySpawns.INSTANCE.getEntries().forEach(entitySpawnDataHolder -> {
            var entitySpawnData = entitySpawnDataHolder.get();
            var entityType = (EntityType<Mob>) entitySpawnData.entityTypeHolder().get();
            var placementType = entitySpawnData.spawnPlacementType();
            var heightMapType = entitySpawnData.heightMapType();
            var predicate = (SpawnPlacements.SpawnPredicate<Mob>) entitySpawnData.spawnPredicate();
            event.register(entityType, placementType, heightMapType, predicate, SpawnPlacementRegisterEvent.Operation.AND);
        });
    }

    private AVPNeoForgeEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
