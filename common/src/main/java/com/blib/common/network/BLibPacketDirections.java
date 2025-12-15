package com.blib.common.network;

import com.avp.service.Services;

import com.blib.common.network.model.PacketDirection;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;

public class BLibPacketDirections {

    public static void initialize() {
        Services.REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CEntityDataSyncPayload.TYPE, S2CEntityDataSyncPayload.CODEC));
    }
}
