package com.avp.common.network;

import com.avp.client.network.AVPClientListener;
import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.service.Services;

public class AVPServerPacketHandlerRegistry {

    public static void initialize() {
        registerServerBoundPacketHandlers();
        registerClientBoundPacketHandlers();
    }

    private static void registerServerBoundPacketHandlers() {
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

    private static void registerClientBoundPacketHandlers() {
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
