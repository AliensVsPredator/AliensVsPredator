package com.avp.neoforge;

import com.avp.data.worldgen.AVPVillageInjection;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import com.avp.AVP;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(AVP.MOD_ID)
public class AVPNeoForge {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    public AVPNeoForge(IEventBus modBus) {
        AVP.initialize();

        REGISTRY.initialize(modBus);

        // Mod bus events
        modBus.addListener(AVPNeoForge::registerEntityAttributes);
        modBus.addListener(AVPNeoForge::registerMiscellaneous);

        // Game bus events
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::registerCommands);
        NeoForge.EVENT_BUS.addListener(AVPNeoForge::addNewVillageBuilding);
    }

    public static void registerMiscellaneous(final FMLCommonSetupEvent event) {
        // Register alien lifecycles.
        REGISTRY.getAlienLifecycleSuppliers()
            .forEach(alienLifecycleSupplier -> AlienLifecycleRegistry.register(alienLifecycleSupplier.get()));
    }

    // Game event
    public static void registerCommands(final RegisterCommandsEvent event) {
        REGISTRY.getLiteralArgumentBuilders()
            .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder));
    }

    // Mod event
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        REGISTRY.getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.first().get(), pair.second().get().build()));
    }

    public static void addNewVillageBuilding(final ServerAboutToStartEvent event) {
        var templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        var processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        AVPVillageInjection.addBuildingToPool(
                templatePoolRegistry,
                processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/plains/houses"),
                "avp:village/plains/houses/plains_commissary",
                5
        );

        AVPVillageInjection.addBuildingToPool(
                templatePoolRegistry,
                processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/snowy/houses"),
                "avp:village/snowy/houses/snowy_commissary",
                5
        );

        AVPVillageInjection.addBuildingToPool(
                templatePoolRegistry,
                processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/savanna/houses"),
                "avp:village/savanna/houses/savanna_commissary",
                5
        );

        AVPVillageInjection.addBuildingToPool(
                templatePoolRegistry,
                processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/taiga/houses"),
                "avp:village/taiga/houses/taiga_commissary",
                5
        );

        AVPVillageInjection.addBuildingToPool(
                templatePoolRegistry,
                processorListRegistry,
                ResourceLocation.withDefaultNamespace("village/desert/houses"),
                "avp:village/desert/houses/desert_commissary",
                5
        );
    }
}
