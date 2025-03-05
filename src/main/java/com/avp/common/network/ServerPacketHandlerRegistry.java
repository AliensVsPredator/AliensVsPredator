package com.avp.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.avp.common.network.packet.C2SGunReloadPayload;

public class ServerPacketHandlerRegistry {

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(
            C2SGunReloadPayload.TYPE,
            (payload, context) -> context.server().execute(() -> ServerListener.handleGunReloadPayload(context, payload))
        );
    }
}
