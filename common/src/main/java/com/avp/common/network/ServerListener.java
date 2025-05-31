package com.avp.common.network;

import com.human.common.gameplay.item.gun.GunReloading;
import com.human.common.gameplay.item.gun.attack.hitscan.GunHitScanAttackHandler;
import net.minecraft.world.entity.player.Player;

import com.avp.common.model.Crawler;
import com.avp.common.network.packet.C2SGunHitResultsPayload;
import com.avp.common.network.packet.C2SGunReloadPayload;
import com.avp.common.network.packet.C2SPlayerToggleCrawlPayload;

public class ServerListener {

    public static void handleGunHitResultsPayload(C2SGunHitResultsPayload gunHitResultsPayload, Player serverPlayer) {
        GunHitScanAttackHandler.handle(gunHitResultsPayload, serverPlayer);
    }

    public static void handleGunReloadPayload(C2SGunReloadPayload gunReloadPayload, Player serverPlayer) {
        GunReloading.reload(serverPlayer);
    }

    public static void handlePlayerToggleCrawlPayload(C2SPlayerToggleCrawlPayload playerToggleCrawlPayload, Player serverPlayer) {
        var crawler = (Crawler) serverPlayer;
        crawler.setCrawling(playerToggleCrawlPayload.shouldCrawl());
    }

    private ServerListener() {
        throw new UnsupportedOperationException();
    }
}
