package com.blib.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface BLibClientNetworkingService {

    void sendToServer(CustomPacketPayload payload);
}
