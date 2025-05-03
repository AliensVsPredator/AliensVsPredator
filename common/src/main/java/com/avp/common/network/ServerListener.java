package com.avp.common.network;

import net.minecraft.world.entity.player.Player;

import com.avp.common.item.GunReloading;
import com.avp.common.network.packet.C2SGunReloadPayload;

public class ServerListener {

    public static void handleGunReloadPayload(C2SGunReloadPayload gunReloadPayload, Player serverPlayer) {
        GunReloading.reload(serverPlayer);
    }

    private ServerListener() {
        throw new UnsupportedOperationException();
    }
}
