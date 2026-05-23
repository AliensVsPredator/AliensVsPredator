package com.blib.mod.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.api.common.pathfinding.v1.debug.PathDebugUtil;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;

@ApiStatus.Internal
public final class BLibServerListener {

    private BLibServerListener() {}

    public static void handleGOAPTrack(C2SGOAPTrackPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (payload.entityIds().isEmpty()) {
            GOAPDebugTracker.INSTANCE.untrack(serverPlayer.getUUID(), serverPlayer.server);
            return;
        }

        var tracked = new ArrayList<UUID>();
        var trackedEntities = new ArrayList<LivingEntity>();
        for (var entityId : payload.entityIds()) {
            var entity = serverPlayer.serverLevel().getEntity(entityId);
            if (entity instanceof LivingEntity living && living instanceof GOAPUser<?>) {
                tracked.add(living.getUUID());
                trackedEntities.add(living);
            }
        }

        if (tracked.isEmpty()) {
            GOAPDebugTracker.INSTANCE.untrack(serverPlayer.getUUID(), serverPlayer.server);
            return;
        }

        GOAPDebugTracker.INSTANCE.track(serverPlayer.getUUID(), tracked, serverPlayer.server);
        sendInitialPathfindingState(serverPlayer, trackedEntities);
    }

    private static void sendInitialPathfindingState(ServerPlayer player, List<LivingEntity> entities) {
        for (var entity : entities) {
            if (!(entity instanceof Mob mob) || !(entity instanceof PathNavigatorUser navigatorUser)) {
                continue;
            }

            PathDebugUtil.sendDebugState(player, mob, navigatorUser.getPathNavigator());
        }
    }
}
