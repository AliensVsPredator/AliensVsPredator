package com.blib.mod.common.gameplay.goap;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class GOAPDebugTracker {

    public static final GOAPDebugTracker INSTANCE = new GOAPDebugTracker();

    public static final int WORLD_STATE_PAGE_SIZE = 30;

    private static final int DEDICATED_TICK_INTERVAL = 10;

    private final Map<UUID, GOAPDebugTrackingState> trackingByPlayer = new ConcurrentHashMap<>();

    private int tickCounter;

    private GOAPDebugTracker() {}

    public void track(UUID playerUuid, List<UUID> entityUuids) {
        trackingByPlayer.put(playerUuid, new GOAPDebugTrackingState(new ArrayList<>(entityUuids), 0));
    }

    public void untrack(UUID playerUuid) {
        trackingByPlayer.remove(playerUuid);
    }

    public int next(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null || state.entityUuids().isEmpty()) {
            return -1;
        }

        state.setSelectedIndex((state.selectedIndex() + 1) % state.entityUuids().size());
        return state.selectedIndex();
    }

    public int previous(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null || state.entityUuids().isEmpty()) {
            return -1;
        }

        state.setSelectedIndex((state.selectedIndex() - 1 + state.entityUuids().size()) % state.entityUuids().size());
        return state.selectedIndex();
    }

    public @Nullable GOAPDebugTrackingState getTrackingState(UUID playerUuid) {
        return trackingByPlayer.get(playerUuid);
    }

    public boolean worldStateNext(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.setWorldStateAutoPage(false);
        state.setWorldStatePage(state.worldStatePage() + 1);
        return true;
    }

    public boolean worldStatePrevious(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.setWorldStateAutoPage(false);
        state.setWorldStatePage(state.worldStatePage() - 1);
        return true;
    }

    public boolean worldStatePage(UUID playerUuid, int page) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.setWorldStateAutoPage(false);
        state.setWorldStatePage(page);
        return true;
    }

    public boolean worldStateAuto(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.setWorldStateAutoPage(true);
        return true;
    }

    public void clear(MinecraftServer server) {
        trackingByPlayer.clear();
        tickCounter = 0;
    }

    public void tick(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            tickCounter++;

            if (tickCounter < DEDICATED_TICK_INTERVAL) {
                return;
            }

            tickCounter = 0;
        }

        for (var entry : trackingByPlayer.entrySet()) {
            var playerUuid = entry.getKey();
            var state = entry.getValue();

            var player = server.getPlayerList().getPlayer(playerUuid);

            if (player == null) {
                continue;
            }

            GOAPDebugPayloadBuilder.buildAndSend(server, player, state);
        }
    }
}
