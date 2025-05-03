package com.avp.client.network;

import com.avp.common.network.NetworkHandler;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.service.Services;

public class AVPClientPacketHandlerRegistry {

    public static void initialize() {
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromServer<>(
                S2CBulletHitBlockPayload.TYPE,
                S2CBulletHitBlockPayload.CODEC,
                (payload, player) -> AVPClientListener.handleBulletHitBlockPayload(payload)
            )
        );
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromServer<>(
                S2CGunRecoilPayload.TYPE,
                S2CGunRecoilPayload.CODEC,
                AVPClientListener::handleGunRecoil
            )
        );
    }
}
