package com.avp.neoforge.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

import com.avp.service.ClientNetworkingService;

public class NeoForgeClientNetworkingService implements ClientNetworkingService {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
