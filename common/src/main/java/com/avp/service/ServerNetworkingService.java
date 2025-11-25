package com.avp.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@Deprecated(forRemoval = true)
public interface ServerNetworkingService {

    void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload);

    void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload);
}
