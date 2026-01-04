package com.blib.common.network;

import com.blib.BLib;
import com.blib.common.network.model.PacketDirection;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.common.registry.impl.BLibNetworkRegistry;

public class BLibPacketDirections {

    private static final BLibNetworkRegistry REGISTRY = BLib.MOD.registries().createNetworkRegistry();

    public static void initialize() {
        REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityDataSyncPayload.TYPE, S2CEntityDataSyncPayload.CODEC));
    }
}
