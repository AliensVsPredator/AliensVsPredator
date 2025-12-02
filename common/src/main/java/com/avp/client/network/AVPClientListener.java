package com.avp.client.network;

import com.avp.common.network.packet.S2CEntityDataSyncPayload;
import com.lib.common.network.DataUser;
import net.minecraft.world.entity.player.Player;

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
