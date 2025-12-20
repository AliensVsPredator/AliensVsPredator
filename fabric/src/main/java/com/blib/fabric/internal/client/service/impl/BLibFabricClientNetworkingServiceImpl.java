package com.blib.fabric.internal.client.service.impl;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.service.BLibClientNetworkingService;

@ApiStatus.Internal
public class BLibFabricClientNetworkingServiceImpl implements BLibClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
