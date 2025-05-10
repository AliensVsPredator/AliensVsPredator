package com.avp.common.network;

import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.service.Services;

public class AVPServerPacketHandlerRegistry {

    public static void initialize() {
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromClient<>(
                C2SGunReloadPayload.TYPE,
                C2SGunReloadPayload.CODEC,
                ServerListener::handleGunReloadPayload
            )
        );
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromClient<>(
                C2SGunHitResultsPayload.TYPE,
                C2SGunHitResultsPayload.CODEC,
                ServerListener::handleGunHitResultsPayload
            )
        );
    }
}
