package com.avp.client.network;

import com.lib.common.network.DataUser;
import net.minecraft.world.entity.player.Player;

import com.avp.common.network.packet.S2CEntityDataSyncPayload;

public class AVPClientListener {

    public static void handleEntityDataSync(S2CEntityDataSyncPayload entityDataSyncPayload, Player player) {
        var targetEntity = player.level().getEntity(entityDataSyncPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var dataContainer = ((DataUser) targetEntity).getDataContainer();

        entityDataSyncPayload.rawDataSyncMap()
            .rawDataById()
            .forEach(dataContainer::set);
    }

    private AVPClientListener() {
        throw new UnsupportedOperationException();
    }
}
