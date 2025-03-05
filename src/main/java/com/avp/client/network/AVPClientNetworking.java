package com.avp.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class AVPClientNetworking {

    public static void sendToServer(CustomPacketPayload customPacketPayload) {
        ClientPlayNetworking.send(customPacketPayload);
    }
}
