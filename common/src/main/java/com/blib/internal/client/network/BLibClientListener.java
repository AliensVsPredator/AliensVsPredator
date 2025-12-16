package com.blib.internal.client.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.network.data.DataUser;
import com.blib.common.network.packet.S2CEntityDataSyncPayload;

@ApiStatus.Internal
public final class BLibClientListener {

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

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
