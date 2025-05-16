package com.avp.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

import com.avp.client.AVPClient;
import com.avp.common.network.NetworkHandler;
import com.avp.fabric.service.FabricRegistryService;
import com.avp.service.Services;

public class AVPFabricClient implements ClientModInitializer {

    private final FabricRegistryService REGISTRY = (FabricRegistryService) Services.REGISTRY;

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        registerClientBoundNetworkHandlers();
    }

    private void registerClientBoundNetworkHandlers() {
        REGISTRY.getClientBoundPacketHandlers().forEach(networkHandler -> {
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
