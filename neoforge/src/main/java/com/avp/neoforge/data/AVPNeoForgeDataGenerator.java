package com.avp.neoforge.data;

import com.avp.core.AVP;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AVPNeoForgeDataGenerator {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder();
    // FIXME:
//        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AVPNeoForgeEntitySpawns::bootstrap);

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        var datapackBuiltinEntriesProvider = new DatapackBuiltinEntriesProvider(
            output,
            lookupProvider,
            REGISTRY_SET_BUILDER,
            Set.of(AVP.MOD_ID)
        );

        generator.addProvider(true, datapackBuiltinEntriesProvider);
        generator.addProvider(true, new AVPDataMapProvider(output, lookupProvider));
    }
}