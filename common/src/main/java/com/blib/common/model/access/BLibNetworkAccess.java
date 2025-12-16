package com.blib.common.model.access;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;

public class BLibNetworkAccess {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibNetworkAccess(BLibMod mod) {
        this.mod = mod;
    }

    public void sendToServer(CustomPacketPayload customPacketPayload) {
        BLibInternalServices.CLIENT_NETWORKING.sendToServer(customPacketPayload);
    }

    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToClient(serverPlayer, customPacketPayload);
    }

    public void sendToAllClients(MinecraftServer minecraftServer, CustomPacketPayload customPacketPayload) {
        BLibInternalServices.SERVER_NETWORKING.sendToAllClients(minecraftServer, customPacketPayload);
    }
}
