package org.avp.fabric.service;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import org.avp.common.service.NetworkHandler;

public class FabricNetworkHandler implements NetworkHandler {

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        var packetBuf = PacketByteBufs.create();
        customPacketPayload.write(packetBuf);

        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, customPacketPayload.id(), packetBuf);
        }
    }

    @Override
    public void sendToServer(CustomPacketPayload customPacketPayload) {
        var packetBuf = PacketByteBufs.create();
        customPacketPayload.write(packetBuf);

        ClientPlayNetworking.send(customPacketPayload.id(), packetBuf);
    }
}
