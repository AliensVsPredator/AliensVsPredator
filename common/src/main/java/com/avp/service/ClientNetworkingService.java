package com.avp.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Deprecated(forRemoval = true)
public interface ClientNetworkingService {

    void sendToServer(CustomPacketPayload payload);
}
