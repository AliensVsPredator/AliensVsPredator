package com.blib.common.network;

import com.blib.BLib;
import com.blib.client.network.BLibClientListener;
import com.blib.common.network.model.NetworkHandler;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.common.registry.impl.BLibNetworkRegistry;

public class BLibServerPacketHandlers {

    private static final BLibNetworkRegistry REGISTRY = BLib.MOD.registries().createNetworkRegistry();

    public static void initialize() {
        registerClientBoundPacketHandlers();
    }

    private static void registerClientBoundPacketHandlers() {
        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CEntityDataSyncPayload.TYPE,
                S2CEntityDataSyncPayload.CODEC,
                BLibClientListener::handleEntityDataSync
            )
        );
    }
}
