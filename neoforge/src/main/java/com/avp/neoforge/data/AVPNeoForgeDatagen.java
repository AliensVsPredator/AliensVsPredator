package com.avp.neoforge.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;

import java.util.List;
import java.util.Set;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.data.AVPCaveKey;
import com.avp.data.worldgen.AVPOres;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDatagen {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new AVPNeoForgeDataMaps(packOutput, lookupProvider));
        generator.addProvider(
            event.includeServer(),
            (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                        var biomes = bootstrap.lookup(Registries.BIOME);
                        var placedFeatures = bootstrap.lookup(Registries.PLACED_FEATURE);
                        var biomes0 = new BiomeFilterRegistryLookup(biomes);
                        var excludedBiomes = HolderSet.direct(biomes.getOrThrow(Biomes.DRIPSTONE_CAVES));
                        var underGround = GenerationStep.Decoration.UNDERGROUND_ORES;

                        for (var spawnData : REGISTRY.getEntitySpawnDataEntries()) {
                            if (spawnData.isConfigDisabled()) {
                                continue;
                            }

                            var entityType = spawnData.getEntityType();
                            var entityTypePath = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
                            var spawnKey = ResourceKey.create(
                                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                                AVPResources.location("add_spawns_" + entityTypePath)
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
                                            spawnSettings.weight,
                                            spawnSettings.minGroupSize,
                                            spawnSettings.maxGroupSize
                                        )
                                    )
                                )
                            );
                        }
                        bootstrap.register(
                            AVPFeatureKeys.ADD_AUTUNITE_GEODE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPCaveKey.AUTUNITE_GEODE)),
                                GenerationStep.Decoration.LOCAL_MODIFICATIONS
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_BAUXITE_MIDDLE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.BAUXITE_MIDDLE.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_BAUXITE_UPPER,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.BAUXITE_UPPER.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_GALENA,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.GALENA.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_LITHIUM,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LITHIUM.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_MONAZITE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.MONAZITE.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_SILICON_GRAVEL,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.SILICON_GRAVEL.placedFeatureKey())),
                                underGround
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_TITANIUM_LOWER,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.TITANIUM_LOWER.placedFeatureKey())),
                                underGround
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_LEAD_SWAMP,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(
                                    List.of(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.MANGROVE_SWAMP))
                                ),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LEAD_SWAMP.placedFeatureKey())),
                                underGround
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_LITHIUM_DESERT,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(
                                    List.of(biomes.getOrThrow(Biomes.DESERT), biomes.getOrThrow(Biomes.BADLANDS))
                                ),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LITHIUM_DESERT.placedFeatureKey())),
                                underGround
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_MONAZITE_JUNGLE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.MONAZITE_JUNGLE.placedFeatureKey())),
                                underGround
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_ZINC,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                new AndHolderSet<>(
                                    biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                    new NotHolderSet<>(biomes0, excludedBiomes)
                                ),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.ZINC.placedFeatureKey())),
                                underGround
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_ZINC_DRIPSTONE_CAVES,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(biomes.getOrThrow(Biomes.DRIPSTONE_CAVES)),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.ZINC_DRIPSTONE_CAVES.placedFeatureKey())),
                                underGround
                            )
                        );
                    }),
                Set.of(AVP.MOD_ID)
            )
        );
    }

    private AVPNeoForgeDatagen() {}
}
