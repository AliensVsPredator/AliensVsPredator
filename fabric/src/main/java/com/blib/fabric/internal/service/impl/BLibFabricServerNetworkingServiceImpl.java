package com.blib.fabric.internal.service.impl;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.service.BLibServerNetworkingService;

@ApiStatus.Internal
public class BLibFabricServerNetworkingServiceImpl implements BLibServerNetworkingService {

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload) {
        ServerPlayNetworking.send(serverPlayer, payload);
    }

    @Override
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload payload) {
        for (var player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToAllClientsTrackingChunk(ServerLevel level, BlockPos blockPos, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(level, blockPos)) {
            sendToClient(player, payload);
        }
    }

    @Override
    public void sendToAllClientsTrackingEntity(Entity entity, CustomPacketPayload payload) {
        if (entity instanceof ServerPlayer serverPlayer) {
            sendToClient(serverPlayer, payload);
            // TODO: Should probably return here?
        }

        for (var player : PlayerLookup.tracking(entity)) {
            sendToClient(player, payload);
        }
    }
}
