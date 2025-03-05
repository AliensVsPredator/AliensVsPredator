package com.avp.neoforge;

import com.avp.core.AVP;
import com.avp.core.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.core.platform.service.Services;
import com.avp.neoforge.platform.service.NeoForgeRegistryService;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

@Mod(AVP.MOD_ID)
public final class AVPNeoForge {

    public AVPNeoForge(IEventBus eventBus) {
        var registry = (NeoForgeRegistryService) Services.REGISTRY;

        registry.initialize(eventBus);

        AVP.initialize();
    }

    // TODO: Verify that we can use @SubscribeEvent here.
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var registry = (NeoForgeRegistryService) Services.REGISTRY;

        registry.getCommands().forEach(event.getDispatcher()::register);
    }

    // TODO: Verify that we can use @SubscribeEvent here.
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        var registry = (NeoForgeRegistryService) Services.REGISTRY;
        var pairs = registry.getEntityTypeAttributePairs();
        pairs.forEach(pair -> {
            var entityType = pair.getKey().get();
            var attributeSupplier = pair.getValue().get().build();
            event.put(entityType, attributeSupplier);
        });
    }

    // TODO: Verify that we can use @SubscribeEvent here.
    @SubscribeEvent
    public static void registerNetworkingHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        // TODO:
        registrar.playBidirectional(
            S2CBulletHitBlockPayload.TYPE,
            S2CBulletHitBlockPayload.CODEC,
            new DirectionalPayloadHandler<>(
                ((payload, context) -> {}),
                (payload, context) -> {}
            )
        );
    }
}
