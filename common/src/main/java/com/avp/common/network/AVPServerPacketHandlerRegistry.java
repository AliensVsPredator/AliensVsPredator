package com.avp.common.network;

import com.avp.client.network.AVPClientListener;
import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.avp.service.Services;

public class AVPServerPacketHandlerRegistry {

    public static void initialize() {
        registerClientBoundPacketHandlers();
    }

    private static void registerClientBoundPacketHandlers() {
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromServer<>(
                S2CEntityDataSyncPayload.TYPE,
                S2CEntityDataSyncPayload.CODEC,
                AVPClientListener::handleEntityDataSync
            )
        );
    }
}
