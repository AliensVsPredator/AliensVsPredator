package com.blib.mod.common.network;

import com.blib.api.common.codec.v1.stream.adapter.M2JStreamCodecAdapter;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CBlockEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CItemStackDispatchCommandPayload;

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

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CBlockEntityDispatchCommandPayload.TYPE,
                new M2JStreamCodecAdapter<>(S2CBlockEntityDispatchCommandPayload.CODEC),
                BLibClientListener::handleBlockEntityDispatchCommand
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CEntityDispatchCommandPayload.TYPE,
                new M2JStreamCodecAdapter<>(S2CEntityDispatchCommandPayload.CODEC),
                BLibClientListener::handleEntityDispatchCommand
            )
        );

        REGISTRY.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CItemStackDispatchCommandPayload.TYPE,
                new M2JStreamCodecAdapter<>(S2CItemStackDispatchCommandPayload.CODEC),
                BLibClientListener::handleItemStackDispatchCommand
            )
        );
    }
}
