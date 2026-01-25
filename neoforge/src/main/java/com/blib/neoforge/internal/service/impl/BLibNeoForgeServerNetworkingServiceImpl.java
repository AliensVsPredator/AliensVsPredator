package com.blib.neoforge.internal.service.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
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
    public void sendToAllClients(MinecraftServer server, CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }

    @Override
    public void sendToAllClientsTrackingChunk(ServerLevel level, BlockPos blockPos, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(blockPos), payload);
    }

    @Override
    public void sendToAllClientsTrackingEntity(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
    }
}
