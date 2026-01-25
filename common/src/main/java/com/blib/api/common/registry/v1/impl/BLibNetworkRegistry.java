package com.blib.api.common.registry.v1.impl;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.network.v1.PacketDirection;
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
