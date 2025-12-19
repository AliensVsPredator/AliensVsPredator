package com.blib.neoforge.internal.service.impl;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.service.BLibServerNetworkingService;

@ApiStatus.Internal
public class BLibNeoForgeServerNetworkingServiceImpl implements BLibServerNetworkingService {

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(serverPlayer, payload);
    }

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        PacketDistributor.sendToAllPlayers(customPacketPayload);
    }
}
