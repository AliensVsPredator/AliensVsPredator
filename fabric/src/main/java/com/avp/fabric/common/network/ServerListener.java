package com.avp.fabric.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import com.avp.common.item.GunReloading;
import com.avp.fabric.common.network.packet.C2SGunReloadPayload;

public class ServerListener {

    public static void handleGunReloadPayload(ServerPlayNetworking.Context context, C2SGunReloadPayload gunReloadPayload) {
        var player = context.player();
        GunReloading.reload(player);
    }

    private ServerListener() {
        throw new UnsupportedOperationException();
    }
}
