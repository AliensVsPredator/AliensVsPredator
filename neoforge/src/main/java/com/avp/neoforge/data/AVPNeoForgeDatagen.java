package com.avp.neoforge.data;

import com.avp.AVP;
import com.avp.AVPResources;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDatagen {

    public static final ResourceKey<BiomeModifier> ADD_SPAWNS_EXAMPLE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            AVPResources.location("add_spawns_example")
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
                        HolderGetter<Biome> biomes = bootstrap.lookup(Registries.BIOME);
                        bootstrap.register(ADD_SPAWNS_EXAMPLE, new BiomeModifiers.AddSpawnsBiomeModifier(
                            HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                            List.of(
                                new MobSpawnSettings.SpawnerData(EntityType.GHAST, 100, 1, 4),
                                new MobSpawnSettings.SpawnerData(EntityType.GHAST, 1, 5, 10)
                            )
                        ));
                    }),
                Set.of(AVP.MOD_ID)
            )
        );
    }
}
