package com.avp.core.client.network;

import com.avp.core.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.core.common.network.packet.S2CGunRecoilPayload;
import com.avp.core.platform.service.Services;

public class ClientPacketRegistry {

    public static void initialize() {
        Services.CLIENT_REGISTRY.registerS2CPacket(S2CBulletHitBlockPayload.TYPE, AVPClientListener::handleBulletHitBlockPayload);
        Services.CLIENT_REGISTRY.registerS2CPacket(S2CGunRecoilPayload.TYPE, AVPClientListener::handleGunRecoil);
    }
}
