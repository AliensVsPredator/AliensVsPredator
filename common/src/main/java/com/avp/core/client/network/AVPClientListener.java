package com.avp.core.client.network;

import com.avp.core.platform.PacketHandleContext;
import net.minecraft.client.Minecraft;

import com.avp.core.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.core.common.network.packet.S2CGunRecoilPayload;

public class AVPClientListener {

    public static void handleBulletHitBlockPayload(PacketHandleContext.Client<S2CBulletHitBlockPayload> context) {
        var payload = context.payload();
        var blockPos = payload.blockPos();
        var direction = payload.direction();

        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, direction);
        }
    }

    public static void handleGunRecoil(PacketHandleContext.Client<S2CGunRecoilPayload> context) {
        var payload = context.payload();
        var player = context.player();
        var level = player.level();
        var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;

        player.turn(baseRecoilX * 2, -payload.recoil() * 2);
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
