package com.blib.common.registry.impl;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.model.PacketDirection;
import com.blib.internal.service.BLibInternalServices;

public class BLibNetworkRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibNetworkRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public <T extends CustomPacketPayload> void registerPacketHandler(NetworkHandler<T> networkHandler) {
        BLibInternalServices.REGISTRY.registerPacketHandler(mod, networkHandler);
    }

    public <T extends CustomPacketPayload> void registerPacketDirection(PacketDirection<T> packetDirection) {
        BLibInternalServices.REGISTRY.registerPacketDirection(mod, packetDirection);
    }
}
