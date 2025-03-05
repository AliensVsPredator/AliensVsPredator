package com.avp.core.common.network;

import com.avp.core.common.network.packet.C2SGunReloadPayload;
import com.avp.core.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.core.common.network.packet.S2CGunRecoilPayload;
import com.avp.core.platform.service.Services;

public class CommonPacketRegistry {

    public static void initialize() {
        Services.REGISTRY.registerC2SPacket(C2SGunReloadPayload.TYPE, C2SGunReloadPayload.CODEC, ServerListener::handleGunReloadPayload);

        Services.REGISTRY.registerS2CPacketCodec(S2CBulletHitBlockPayload.TYPE, S2CBulletHitBlockPayload.CODEC);
        Services.REGISTRY.registerS2CPacketCodec(S2CGunRecoilPayload.TYPE, S2CGunRecoilPayload.CODEC);
    }
}
