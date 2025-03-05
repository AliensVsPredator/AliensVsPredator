package com.avp.neoforge.platform.service;

import com.avp.core.platform.service.NetworkService;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeNetworkService implements NetworkService {

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToPlayer(serverPlayer, customPacketPayload);
    }

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToAllPlayers(customPacketPayload);
    }

    @Override
    public void sendToServer(CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToServer(customPacketPayload);
    }
}
