package com.avp.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;

public class AVPClientPacketHandlerRegistry {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(
            S2CBulletHitBlockPayload.TYPE,
            (payload, context) -> context.client().execute(() -> AVPClientListener.handleBulletHitBlockPayload(payload))
        );
        ClientPlayNetworking.registerGlobalReceiver(
            S2CGunRecoilPayload.TYPE,
            (payload, context) -> context.client().execute(() -> AVPClientListener.handleGunRecoil(context, payload))
        );
    }
}
