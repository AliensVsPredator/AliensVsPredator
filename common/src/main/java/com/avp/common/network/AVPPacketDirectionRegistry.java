package com.avp.common.network;

import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.common.network.packet.C2SPlayerToggleCrawlPayload;
import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;
import com.avp.service.Services;

public class AVPPacketDirectionRegistry {

    public static void initialize() {
        Services.REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SGunHitResultsPayload.TYPE, C2SGunHitResultsPayload.CODEC));
        Services.REGISTRY.registerPacketDirection(new PacketDirection.C2S<>(C2SGunReloadPayload.TYPE, C2SGunReloadPayload.CODEC));
        Services.REGISTRY.registerPacketDirection(
            new PacketDirection.C2S<>(C2SPlayerToggleCrawlPayload.TYPE, C2SPlayerToggleCrawlPayload.CODEC)
        );

        Services.REGISTRY.registerPacketDirection(
            new PacketDirection.S2C<>(S2CBulletHitBlockPayload.TYPE, S2CBulletHitBlockPayload.CODEC)
        );
        Services.REGISTRY.registerPacketDirection(new PacketDirection.S2C<>(S2CGunRecoilPayload.TYPE, S2CGunRecoilPayload.CODEC));
    }
}
