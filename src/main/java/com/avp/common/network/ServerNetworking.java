package com.avp.common.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerNetworking {

    public static void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        ServerPlayNetworking.send(serverPlayer, customPacketPayload);
    }

    public static void sendToAllClients(MinecraftServer server, CustomPacketPayload customPacketPayload) {
        for (var player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, customPacketPayload);
        }
    }
}
