package com.blib.fabric.service;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import com.avp.service.ClientNetworkingService;

public class FabricClientNetworkingService implements ClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
