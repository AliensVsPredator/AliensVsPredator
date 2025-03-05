package com.avp.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;

public class AVPClientListener {

    public static void handleBulletHitBlockPayload(S2CBulletHitBlockPayload bulletHitBlockPayload) {
        var blockPos = bulletHitBlockPayload.blockPos();
        var direction = bulletHitBlockPayload.direction();

        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, direction);
        }
    }

    public static void handleGunRecoil(ClientPlayNetworking.Context context, S2CGunRecoilPayload gunRecoilPayload) {
        var player = context.player();
        var level = player.level();
        var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;

        player.turn(baseRecoilX * 2, -gunRecoilPayload.recoil() * 2);
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
