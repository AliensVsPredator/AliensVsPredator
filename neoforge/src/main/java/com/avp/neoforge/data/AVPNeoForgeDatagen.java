package com.avp.neoforge.data;

import com.avp.common.entity.type.AVPEntityTypes;
import com.bvanseg.just.functional.function.Lazy;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;

import java.util.List;
import java.util.Set;

import com.avp.AVP;
import com.avp.common.config.AVPConfig;
import com.avp.data.AVPCaveKey;
import com.avp.data.AVPSpawnData;
import com.avp.data.worldgen.AVPOres;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDatagen {

    private static final AVPConfig.SpawnConfigs config = AVP.config.spawnConfigs;

    // FIXME: UPDATE TO PROPER ENTITY TYPES AND IF POSSIBLE FIGURE OUT A BRIDGE SERVICE FOR THE ResourceKey, SINCE
    // ResourceKey<BiomeModifier> IS NEO ONLY
    private static final Lazy<List<AVPSpawnData>> spawnDataList = Lazy.of(() -> List.of(
            new AVPSpawnData(AVPEntityTypes.OVAMORPH.get(), AVPEntitySpawnKeys.ADD_SPAWNS_OVAMORPH, BiomeTags.IS_OVERWORLD, config.OVAMORPH_SPAWN),
            new AVPSpawnData(AVPEntityTypes.CHESTBURSTER.get(), AVPEntitySpawnKeys.ADD_SPAWNS_CHESTBURSTER, BiomeTags.IS_OVERWORLD, config.CHESTBURSTER_SPAWN),
            new AVPSpawnData(AVPEntityTypes.DRONE.get(), AVPEntitySpawnKeys.ADD_SPAWNS_DRONE, BiomeTags.IS_OVERWORLD, config.DRONE_SPAWN),
            new AVPSpawnData(AVPEntityTypes.WARRIOR.get(), AVPEntitySpawnKeys.ADD_SPAWNS_WARRIOR, BiomeTags.IS_OVERWORLD, config.WARRIOR_SPAWN),
            new AVPSpawnData(AVPEntityTypes.PRAETORIAN.get(), AVPEntitySpawnKeys.ADD_SPAWNS_PRAETORIAN, BiomeTags.IS_OVERWORLD, config.PRAETORIAN_SPAWN),
            new AVPSpawnData(AVPEntityTypes.QUEEN.get(), AVPEntitySpawnKeys.ADD_SPAWNS_QUEEN, BiomeTags.IS_OVERWORLD, config.QUEEN_SPAWN),
            new AVPSpawnData(AVPEntityTypes.NETHER_OVAMORPH.get(), AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_OVAMORPH, BiomeTags.IS_NETHER, config.NETHER_OVAMORPH_SPAWN),
            new AVPSpawnData(
                    AVPEntityTypes.NETHER_CHESTBURSTER.get(),
                    AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_CHESTBURSTER,
                    BiomeTags.IS_NETHER,
                    config.NETHER_CHESTBURSTER_SPAWN
            ),
            new AVPSpawnData(AVPEntityTypes.NETHER_DRONE.get(), AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_DRONE, BiomeTags.IS_NETHER, config.NETHER_DRONE_SPAWN),
            new AVPSpawnData(AVPEntityTypes.NETHER_WARRIOR.get(), AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_WARRIOR, BiomeTags.IS_NETHER, config.NETHER_WARRIOR_SPAWN),
            new AVPSpawnData(
                    AVPEntityTypes.NETHER_PRAETORIAN.get(),
                    AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_PRAETORIAN,
                    BiomeTags.IS_NETHER,
                    config.NETHER_PRAETORIAN_SPAWN
            ),
            new AVPSpawnData(AVPEntityTypes.NETHER_QUEEN.get(), AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_QUEEN, BiomeTags.IS_NETHER, config.NETHER_QUEEN_SPAWN),
            new AVPSpawnData(AVPEntityTypes.YAUTJA.get(), AVPEntitySpawnKeys.ADD_SPAWNS_YAUTJA, BiomeTags.IS_JUNGLE, config.YAUTJA_SPAWN)
    ));

    @SuppressWarnings("unchecked")
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

                        for (AVPSpawnData spawnData : spawnDataList.get()) {
                            bootstrap.register(
                                (ResourceKey<BiomeModifier>) spawnData.spawnKey(),
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    biomes.getOrThrow(spawnData.biomeTag()),
                                    List.of(
                                        new MobSpawnSettings.SpawnerData(
                                            spawnData.entityType(),
                                            spawnData.config().weight,
                                            spawnData.config().minGroupSize,
                                            spawnData.config().maxGroupSize
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
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_BAUXITE_UPPER,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.BAUXITE_UPPER.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_GALENA,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.GALENA.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_LITHIUM,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LITHIUM.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_MONAZITE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.MONAZITE.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_SILICON_GRAVEL,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.SILICON_GRAVEL.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                        bootstrap.register(
                            AVPFeatureKeys.ADD_TITANIUM_LOWER,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.TITANIUM_LOWER.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_LEAD_SWAMP,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(
                                    List.of(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.MANGROVE_SWAMP))
                                ),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LEAD_SWAMP.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_LITHIUM_DESERT,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(
                                    List.of(biomes.getOrThrow(Biomes.DESERT), biomes.getOrThrow(Biomes.BADLANDS))
                                ),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.LITHIUM_DESERT.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_MONAZITE_JUNGLE,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.MONAZITE_JUNGLE.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
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
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );

                        bootstrap.register(
                            AVPFeatureKeys.ADD_ZINC_DRIPSTONE_CAVES,
                            new BiomeModifiers.AddFeaturesBiomeModifier(
                                HolderSet.direct(biomes.getOrThrow(Biomes.DRIPSTONE_CAVES)),
                                HolderSet.direct(placedFeatures.getOrThrow(AVPOres.ZINC_DRIPSTONE_CAVES.placedFeatureKey())),
                                GenerationStep.Decoration.UNDERGROUND_ORES
                            )
                        );
                    }),
                Set.of(AVP.MOD_ID)
            )
        );
    }

    private AVPNeoForgeDatagen() {}
}
