package com.avp.neoforge.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;

import com.avp.AVP;
import com.avp.common.config.AVPConfig;
import com.avp.data.AVPSpawnData;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDatagen {

    private static final AVPConfig.SpawnConfigs config = AVP.config.spawnConfigs;

    // FIXME: UPDATE TO PROPER ENTITY TYPES AND IF POSSIBLE FIGURE OUT A BRIDGE SERVICE FOR THE ResourceKey, SINCE ResourceKey<BiomeModifier> IS NEO ONLY
    private static final List<AVPSpawnData> spawnDataList = List.of(
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_OVAMORPH, BiomeTags.IS_OVERWORLD, config.OVAMORPH_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_CHESTBURSTER, BiomeTags.IS_OVERWORLD, config.CHESTBURSTER_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_DRONE, BiomeTags.IS_OVERWORLD, config.DRONE_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_WARRIOR, BiomeTags.IS_OVERWORLD, config.WARRIOR_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_PRAETORIAN, BiomeTags.IS_OVERWORLD, config.PRAETORIAN_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_QUEEN, BiomeTags.IS_OVERWORLD, config.QUEEN_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_OVAMORPH, BiomeTags.IS_NETHER, config.NETHER_OVAMORPH_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_CHESTBURSTER, BiomeTags.IS_NETHER, config.NETHER_CHESTBURSTER_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_DRONE, BiomeTags.IS_NETHER, config.NETHER_DRONE_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_WARRIOR, BiomeTags.IS_NETHER, config.NETHER_WARRIOR_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_PRAETORIAN, BiomeTags.IS_NETHER, config.NETHER_PRAETORIAN_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_NETHER_QUEEN, BiomeTags.IS_NETHER, config.NETHER_QUEEN_SPAWN),
            new AVPSpawnData(EntityType.GHAST, AVPEntitySpawnKeys.ADD_SPAWNS_YAUTJA, BiomeTags.IS_JUNGLE, config.YAUTJA_SPAWN)
    );

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
            event.includeServer(),
            (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider(),
                new RegistrySetBuilder()
                    .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, bootstrap -> {
                        for (AVPSpawnData spawnData : spawnDataList) {
                            bootstrap.register((ResourceKey<BiomeModifier>) spawnData.spawnKey(),
                                new BiomeModifiers.AddSpawnsBiomeModifier(
                                    bootstrap.lookup(Registries.BIOME).getOrThrow(spawnData.biomeTag()),
                                    List.of(new MobSpawnSettings.SpawnerData(
                                        spawnData.entityType(),
                                        spawnData.config().weight,
                                        spawnData.config().minGroupSize,
                                        spawnData.config().maxGroupSize
                                    ))
                                ));
                            }
                        }
                ),
                Set.of(AVP.MOD_ID)
            )
        );
    }

    private AVPNeoForgeDatagen() {}
}
