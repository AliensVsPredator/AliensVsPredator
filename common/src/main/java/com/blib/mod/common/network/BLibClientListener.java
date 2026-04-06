package com.blib.mod.common.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.client.render.debug.PathfindingNavDebugHUD;
import com.blib.mod.client.render.debug.PathfindingSearchDebugRenderer;
import com.blib.mod.client.render.goap.GOAPDebugHUD;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

@ApiStatus.Internal
public final class BLibClientListener {

    public static void handleChunkClaimsSync(S2CChunkClaimsSyncPayload payload, Player player) {
        ClientTerritoryCache.INSTANCE.updateChunk(payload.chunkX(), payload.chunkZ(), payload.factionIds());
    }

    public static void handleFactionMetadataSync(S2CFactionMetadataSyncPayload payload, Player player) {
        ClientFactionCache.INSTANCE.update(payload.factionId(), payload.name(), payload.color());
    }

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

    public static void handlePathfindingSearchDebug(S2CPathfindingSearchDebugPayload payload, Player player) {
        PathfindingSearchDebugRenderer.INSTANCE.update(payload);
    }

    public static void handlePathfindingNavDebug(S2CPathfindingNavDebugPayload payload, Player player) {
        PathfindingNavDebugHUD.INSTANCE.update(payload);
    }

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
