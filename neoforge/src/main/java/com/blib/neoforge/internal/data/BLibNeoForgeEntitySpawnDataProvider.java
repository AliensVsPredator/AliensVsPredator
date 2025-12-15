package com.blib.neoforge.internal.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.neoforge.internal.service.impl.NeoForgeBLibRegistryServiceImpl;

@ApiStatus.Internal
public final class BLibNeoForgeEntitySpawnDataProvider implements DataProvider.Factory<DatapackBuiltinEntriesProvider> {

    private final BLibMod mod;

    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public BLibNeoForgeEntitySpawnDataProvider(
        BLibMod mod,
        CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        this.mod = mod;
        this.lookupProvider = lookupProvider;
    }

    @Override
    public @NotNull DatapackBuiltinEntriesProvider create(@NotNull PackOutput packOutput) {
        var registry = (NeoForgeBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;

        var registrySetBuilder = new RegistrySetBuilder()
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                var biomes = bootstrap.lookup(Registries.BIOME);

                for (var spawnData : registry.getModContainer(mod).getEntitySpawnDataEntries()) {
                    if (spawnData.isConfigDisabled()) {
                        continue;
                    }

                    var holder = spawnData.getEntityTypeHolder();
                    var entityType = holder.get();
                    var entityTypePath = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
                    var spawnKey = ResourceKey.create(
                        NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                        mod.resources().createLocation("add_spawns_" + entityTypePath)
                    );
                    var config = spawnData.getConfigData();
                    var spawnSettings = config.spawnSettings();

                    bootstrap.register(
                        spawnKey,
                        new BiomeModifiers.AddSpawnsBiomeModifier(
                            biomes.getOrThrow(config.biomeTagKey()),
                            List.of(
                                new MobSpawnSettings.SpawnerData(
                                    entityType,
                                    spawnSettings.weight(),
                                    spawnSettings.minGroupSize(),
                                    spawnSettings.maxGroupSize()
                                )
                            )
                        )
                    );
                }
            });

        return new Provider(mod, packOutput, lookupProvider, registrySetBuilder);
    }

    static class Provider extends DatapackBuiltinEntriesProvider {

        private final BLibMod mod;

        public Provider(
            BLibMod mod,
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            RegistrySetBuilder datapackEntriesBuilder
        ) {
            super(output, registries, datapackEntriesBuilder, Set.of(mod.id()));
            this.mod = mod;
        }

        @Override
        public @NotNull String getName() {
            return mod.id() + " Entity Spawn Data Registries";
        }
    }
}
