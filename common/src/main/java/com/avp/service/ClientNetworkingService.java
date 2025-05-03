package com.avp.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientNetworkingService {

    void sendToServer(CustomPacketPayload payload);
}
