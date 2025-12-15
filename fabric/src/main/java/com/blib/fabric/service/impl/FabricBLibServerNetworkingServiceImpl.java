package com.blib.fabric.service.impl;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import com.blib.internal.service.BLibServerNetworkingService;

public class FabricBLibServerNetworkingServiceImpl implements BLibServerNetworkingService {

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        ServerPlayNetworking.send(serverPlayer, payload);
    }

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        for (var player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, customPacketPayload);
        }
    }
}
