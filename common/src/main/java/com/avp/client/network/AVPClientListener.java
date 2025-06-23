package com.avp.client.network;

import com.lib.common.network.SyncedDataUser;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import com.avp.common.network.packet.S2CBulletHitBlockPayload;
import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.avp.common.network.packet.S2CGunRecoilPayload;

public class AVPClientListener {

    public static void handleBulletHitBlockPayload(S2CBulletHitBlockPayload bulletHitBlockPayload) {
        var blockPos = bulletHitBlockPayload.blockPos();
        var direction = bulletHitBlockPayload.direction();

        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, direction);
        }
    }

    public static void handleGunRecoil(S2CGunRecoilPayload gunRecoilPayload, Player player) {
        var level = player.level();
        var baseRecoilX = level.getRandom().nextBoolean() ? 1f : -1f;

        player.turn(baseRecoilX * 2, -gunRecoilPayload.recoil() * 2);
    }

    public static void handleEntityDataSync(S2CEntityDataSyncPayload entityDataSyncPayload, Player player) {
        var targetEntity = player.level().getEntity(entityDataSyncPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var syncedDataContainer = ((SyncedDataUser) targetEntity).getSyncedDataContainer();

        entityDataSyncPayload.rawDataSyncMap()
            .rawDataById()
            .forEach(syncedDataContainer::set);
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
