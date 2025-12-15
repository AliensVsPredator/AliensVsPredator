package com.blib.fabric.internal.service.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class FabricBLibClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
