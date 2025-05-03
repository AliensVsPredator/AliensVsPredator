package com.avp.neoforge;

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
    }

    public static void registerMiscellaneous(FMLCommonSetupEvent event) {
        // Register alien lifecycles.
        REGISTRY.getAlienLifecycleSuppliers()
            .forEach(alienLifecycleSupplier -> AlienLifecycleRegistry.register(alienLifecycleSupplier.get()));
    }

    // Game event
    public static void registerCommands(RegisterCommandsEvent event) {
        REGISTRY.getLiteralArgumentBuilders()
            .forEach(literalArgumentBuilder -> event.getDispatcher().register(literalArgumentBuilder));
    }

    // Mod event
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        REGISTRY.getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.first().get(), pair.second().get().build()));
    }
}
