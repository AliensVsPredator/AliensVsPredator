package com.avp.neoforge.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import com.avp.service.ServerNetworkingService;

public class NeoForgeServerNetworkingService implements ServerNetworkingService {

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToAllPlayers(customPacketPayload);
    }
}
