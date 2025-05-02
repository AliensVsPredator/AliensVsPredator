package com.avp.neoforge.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;

import com.avp.AVP;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDatagen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator()
            .addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                    event.getGenerator().getPackOutput(),
                    event.getLookupProvider(),
                    new RegistrySetBuilder()
                        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_OVAMORPH,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_CHESTBURSTER,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_DRONE,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_WARRIOR,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_PRAETORIAN,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_QUEEN,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_OVAMORPH,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER OVAMORPH
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_CHESTBURSTER,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER CHESTBURSTER
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_DRONE,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER DRONE
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_WARRIOR,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER WARRIOR
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_PRAETORIAN,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER PRAETORIAN
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_QUEEN,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_NETHER),
                                    List.of(
                                        // FIXME: MOVE TO NETHER QUEEN
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                            bootstrap.register(
                                AVPEntitySpawnKeys.ADD_SPAWNS_YAUTJA,
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_JUNGLE),
                                    List.of(
                                        // FIXME: MOVE TO YAUTJA
                                        new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4)
                                    )
                                )
                            );
                        }),
                    Set.of(AVP.MOD_ID)
                )
            );
    }

    private AVPNeoForgeDatagen() {}
}
