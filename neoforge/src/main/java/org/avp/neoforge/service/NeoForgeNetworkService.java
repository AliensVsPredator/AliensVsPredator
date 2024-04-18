package org.avp.neoforge.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.PacketDistributor;

import org.avp.common.service.NetworkService;

public class NeoForgeNetworkService implements NetworkService {

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        PacketDistributor.ALL.noArg().send(customPacketPayload);
    }

    @Override
    public void sendToServer(CustomPacketPayload customPacketPayload) {
        PacketDistributor.SERVER.noArg().send(customPacketPayload);
    }
}
