package com.blib.internal.service;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BLibServerNetworkingService {

    void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload payload);

    void sendToAllClients(MinecraftServer server, CustomPacketPayload payload);

    void sendToAllClientsTrackingChunk(ServerLevel level, BlockPos blockPos, CustomPacketPayload payload);

    void sendToAllClientsTrackingEntity(Entity entity, CustomPacketPayload payload);
}
