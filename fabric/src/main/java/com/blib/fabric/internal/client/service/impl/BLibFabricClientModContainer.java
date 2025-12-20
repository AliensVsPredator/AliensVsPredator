package com.blib.fabric.internal.client.service.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.client.event.impl.BLibClientSetupEvents;
import com.blib.common.event.impl.BLibEventListenerContainer;
import com.blib.common.network.model.NetworkHandler;
import com.blib.fabric.internal.service.impl.BLibFabricRegistryServiceImpl;
import com.blib.internal.service.BLibInternalServices;

@ApiStatus.Internal
public class BLibFabricClientModContainer {

    private final BLibClientMod mod;

    private final BLibEventListenerContainer<BLibClientSetupEvent> onClientSetup;

    public BLibFabricClientModContainer(BLibClientMod mod) {
        this.mod = mod;
        this.onClientSetup = BLibClientSetupEvents.CONTAINER_FACTORY.apply(mod);
    }

    public BLibEventListenerContainer<BLibClientSetupEvent> onClientSetup() {
        return onClientSetup;
    }

    public void initialize() {
        var registry = (BLibFabricRegistryServiceImpl) BLibInternalServices.REGISTRY;

        onClientSetup.getListeners()
            .forEach(BLibClientSetupEvent::invoke);

        registry.getModContainer(mod.common())
            .getClientBoundPacketHandlers()
            .forEach(networkHandler -> {
                @SuppressWarnings("unchecked")
                var typedNetworkHandler = (NetworkHandler<CustomPacketPayload>) networkHandler;

                BiConsumer<CustomPacketPayload, Player> biConsumer = switch (typedNetworkHandler) {
                    case NetworkHandler.FromClient<CustomPacketPayload> handler -> handler.payloadConsumer();
                    case NetworkHandler.FromEither<CustomPacketPayload> handler -> handler.fromServerPayloadConsumer();
                    case NetworkHandler.FromServer<CustomPacketPayload> handler -> handler.payloadConsumer();
                };

                ClientPlayNetworking.registerGlobalReceiver(
                    networkHandler.type(),
                    (payload, context) -> context.client().execute(() -> biConsumer.accept(payload, context.player()))
                );
            });
    }
}
