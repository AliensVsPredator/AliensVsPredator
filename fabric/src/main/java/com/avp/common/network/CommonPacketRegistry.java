package com.avp.common.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;

public class CommonPacketRegistry {

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(C2SGunReloadPayload.TYPE, C2SGunReloadPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(S2CBulletHitBlockPayload.TYPE, S2CBulletHitBlockPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CGunRecoilPayload.TYPE, S2CGunRecoilPayload.CODEC);
    }
}
