package com.avp.common.network;

import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.avp.service.Services;

public class AVPPacketDirectionRegistry {

    public static void initialize() {
        Services.REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityDataSyncPayload.TYPE, S2CEntityDataSyncPayload.CODEC));
    }
}
