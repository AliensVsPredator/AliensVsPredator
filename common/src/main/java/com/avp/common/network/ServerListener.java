package com.avp.common.network;

import net.minecraft.world.entity.player.Player;

import com.avp.common.item.gun.GunReloading;
import com.avp.common.item.gun.attack.hitscan.GunHitScanAttackHandler;
import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.network.packet.C2SGunReloadPayload;

public class ServerListener {

    public static void handleGunReloadPayload(C2SGunReloadPayload gunReloadPayload, Player serverPlayer) {
        GunReloading.reload(serverPlayer);
    }

    public static void handleGunHitResultsPayload(C2SGunHitResultsPayload gunHitResultsPayload, Player serverPlayer) {
        GunHitScanAttackHandler.handle(gunHitResultsPayload, serverPlayer);
    }

    private ServerListener() {
        throw new UnsupportedOperationException();
    }
}
