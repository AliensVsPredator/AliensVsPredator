package com.avp.core.platform.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface NetworkService {

    void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload);

    void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload);

    void sendToServer(CustomPacketPayload customPacketPayload);
}