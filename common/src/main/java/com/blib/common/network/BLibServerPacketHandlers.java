package com.blib.common.network;

import com.avp.service.Services;

import com.blib.client.network.BLibClientListener;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;

public class BLibServerPacketHandlers {

    public static void initialize() {
        registerClientBoundPacketHandlers();
    }

    private static void registerClientBoundPacketHandlers() {
        Services.REGISTRY.registerPacketHandlers(
            new NetworkHandler.FromServer<>(
                S2CEntityDataSyncPayload.TYPE,
                S2CEntityDataSyncPayload.CODEC,
                BLibClientListener::handleEntityDataSync
            )
        );
    }
}
