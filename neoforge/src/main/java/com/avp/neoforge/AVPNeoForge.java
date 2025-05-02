package com.avp.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import com.avp.AVP;
import com.avp.neoforge.service.NeoForgeRegistryService;
import com.avp.service.Services;

@Mod(AVP.MOD_ID)
public class AVPNeoForge {

    private static final NeoForgeRegistryService REGISTRY = (NeoForgeRegistryService) Services.REGISTRY;

    public AVPNeoForge(IEventBus modBus) {
        var registryService = (NeoForgeRegistryService) Services.REGISTRY;
        registryService.initialize(modBus);

        AVP.initialize();
    }

    @EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class Events {

        @SubscribeEvent
        public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
            REGISTRY.getEntityAttributeSupplierPairs()
                .forEach(pair -> event.put(pair.first().get(), pair.second().get().build()));
        }
    }

}
