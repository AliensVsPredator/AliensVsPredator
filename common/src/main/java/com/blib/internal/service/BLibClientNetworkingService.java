package com.blib.internal.service;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface BLibClientNetworkingService {

    void sendToServer(CustomPacketPayload payload);
}
