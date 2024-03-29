package org.avp.common.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;

/**
 * @author Boston Vanseghi
 */
public interface NetworkHandler {
    void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload);
    void sendToServer(CustomPacketPayload customPacketPayload);
}
