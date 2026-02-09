package com.blib.api.common.mod.v1.model.access;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.client.service.BLibInternalClientServices;
import com.blib.internal.service.BLibInternalServices;

public class BLibNetworkAccess {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibNetworkAccess(BLibMod mod) {
        this.mod = mod;
    }

    public void sendToServer(CustomPacketPayload customPacketPayload) {
        BLibInternalClientServices.CLIENT_NETWORKING.sendToServer(customPacketPayload);
    }

    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToClient(serverPlayer, customPacketPayload);
    }

    public void sendToAllClients(MinecraftServer minecraftServer, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToAllClients(minecraftServer, customPacketPayload);
    }

    public void sendToAllClientsTrackingChunk(ServerLevel serverLevel, BlockPos blockPos, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToAllClientsTrackingChunk(serverLevel, blockPos, customPacketPayload);
    }

    public void sendToAllClientsTrackingEntity(Entity entity, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToAllClientsTrackingEntity(entity, customPacketPayload);
    }
}
