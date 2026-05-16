package com.blib.mod.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.UUID;

import com.blib.api.common.goap.v1.GOAPUser;
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
            GOAPDebugTracker.INSTANCE.untrack(serverPlayer.getUUID());
            return;
        }

        var tracked = new ArrayList<UUID>();
        for (var entityId : payload.entityIds()) {
            var entity = serverPlayer.serverLevel().getEntity(entityId);
            if (entity instanceof LivingEntity living && living instanceof GOAPUser<?>) {
                tracked.add(living.getUUID());
            }
        }

        if (tracked.isEmpty()) {
            GOAPDebugTracker.INSTANCE.untrack(serverPlayer.getUUID());
            return;
        }

        GOAPDebugTracker.INSTANCE.track(serverPlayer.getUUID(), tracked);
    }
}
