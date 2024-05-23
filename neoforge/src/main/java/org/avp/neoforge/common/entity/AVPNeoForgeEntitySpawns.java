package org.avp.neoforge.common.entity;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import org.avp.common.entity.data.AVPEntityDataRegistry;

public class AVPNeoForgeEntitySpawns {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AVPNeoForgeEntitySpawns::bootstrap);

    private static void bootstrap(BootstapContext<BiomeModifier> context) {
        AVPEntityDataRegistry.INSTANCE.getMobEntries().forEach(entityData -> {
            var entityTypeHolder = entityData.getHolder();
            var entityType = entityTypeHolder.get();

            entityData.getSpawnData().ifPresent(spawnData -> {
                var biomeTag = spawnData.biomeTagKey();
                var key = createThrowawayRegistryKey(entityTypeHolder.getResourceLocation());

                var biomes = context.lookup(Registries.BIOME);
                var biomeNamed = biomes.get(biomeTag).orElseThrow();

                var weight = spawnData.weight();
                var minGroupSize = spawnData.minGroupSize();
                var maxGroupSize = spawnData.maxGroupSize();

                var spawnsBiomeModifier = BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                    biomeNamed,
                    new MobSpawnSettings.SpawnerData(
                        entityType,
                        weight,
                        minGroupSize,
                        maxGroupSize
                    )
                );

                context.register(key, spawnsBiomeModifier);
            });
        });
    }

    private static ResourceKey<BiomeModifier> createThrowawayRegistryKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, resourceLocation);
    }

    @SuppressWarnings("unchecked")
    public static void handleSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
        AVPEntityDataRegistry.INSTANCE.getMobEntries().forEach(entityData -> {
            var entityType = (EntityType<Mob>) entityData.getHolder().get();

            entityData.getSpawnData().ifPresent(spawnData -> {
                var placementType = spawnData.spawnPlacementType();
                var heightMapType = spawnData.heightMapType();
                var predicate = (SpawnPlacements.SpawnPredicate<Mob>) spawnData.spawnPredicate();
                event.register(entityType, placementType, heightMapType, predicate, SpawnPlacementRegisterEvent.Operation.AND);
            });
        });
    }

    private AVPNeoForgeEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
