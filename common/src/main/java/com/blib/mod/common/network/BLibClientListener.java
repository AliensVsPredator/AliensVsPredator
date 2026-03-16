package com.blib.mod.common.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.mod.client.render.goap.GOAPDebugHUD;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

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

    public static void handleGOAPDebug(S2CGOAPDebugPayload payload, Player player) {
        GOAPDebugHUD.INSTANCE.update(payload);
    }

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
