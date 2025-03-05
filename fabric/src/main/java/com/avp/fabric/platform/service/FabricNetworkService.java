package com.avp.fabric.platform.service;

import com.avp.core.platform.service.NetworkService;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkService implements NetworkService {

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        for (var player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, customPacketPayload);
        }
    }

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        ServerPlayNetworking.send(serverPlayer, customPacketPayload);
    }

    @Override
    public void sendToServer(CustomPacketPayload customPacketPayload) {
        ClientPlayNetworking.send(customPacketPayload);
    }
}
