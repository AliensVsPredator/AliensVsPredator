package com.blib.mod.common.gameplay.goap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.common.property.BLibModProperties;
import com.blib.mod.common.property.BLibModPropertyAccess;

@ApiStatus.Internal
public final class GOAPDebugTracker {

    public static final GOAPDebugTracker INSTANCE = new GOAPDebugTracker();

    public static int getWorldStatePageSize() {
        return BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.Goap.WORLD_STATE_PAGE_SIZE);
    }

    private final Map<UUID, GOAPDebugTrackingState> trackingByPlayer = new ConcurrentHashMap<>();

    private final Set<UUID> diagnosticsEnabledEntityUuids = ConcurrentHashMap.newKeySet();

    private int tickCounter;

    private GOAPDebugTracker() {}

    public void tick(MinecraftServer server) {
        if (server.isDedicatedServer()) {
            tickCounter++;

            if (tickCounter < BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.Goap.DEDICATED_TICK_INTERVAL)) {
                return;
            }

            tickCounter = 0;
        }

        syncDiagnosticsEnabled(server);

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

    public void track(UUID playerUuid, List<UUID> entityUuids) {
        trackingByPlayer.put(playerUuid, new GOAPDebugTrackingState(new ArrayList<>(entityUuids), 0));
    }

    public void track(UUID playerUuid, List<UUID> entityUuids, MinecraftServer server) {
        track(playerUuid, entityUuids);
        syncDiagnosticsEnabled(server);
    }

    public void untrack(UUID playerUuid) {
        trackingByPlayer.remove(playerUuid);
    }

    public void untrack(UUID playerUuid, MinecraftServer server) {
        untrack(playerUuid);
        syncDiagnosticsEnabled(server);
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

    public List<ServerPlayer> playersTracking(MinecraftServer server, UUID entityUuid) {
        var players = new ArrayList<ServerPlayer>();
        for (var entry : trackingByPlayer.entrySet()) {
            if (!entry.getValue().entityUuids().contains(entityUuid)) {
                continue;
            }

            var player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null) {
                players.add(player);
            }
        }
        return players;
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
        disableTrackedDiagnostics(server);
        trackingByPlayer.clear();
        diagnosticsEnabledEntityUuids.clear();
        tickCounter = 0;
    }

    @SuppressWarnings("unchecked")
    private void syncDiagnosticsEnabled(MinecraftServer server) {
        var currentlyTracked = new HashSet<UUID>();

        for (var entry : trackingByPlayer.entrySet()) {
            if (server.getPlayerList().getPlayer(entry.getKey()) == null) {
                continue;
            }

            currentlyTracked.addAll(entry.getValue().entityUuids());
        }

        for (var entityUuid : currentlyTracked) {
            if (diagnosticsEnabledEntityUuids.contains(entityUuid)) {
                continue;
            }

            var entity = resolveEntity(server, entityUuid);
            if (!(entity instanceof LivingEntity livingEntity) || !(livingEntity instanceof GOAPUser<?> goapUser)) {
                continue;
            }

            var livingEntityAgent = ((GOAPUser<LivingEntity>) goapUser).blib$getGOAPAgentOrNull();
            if (livingEntityAgent == null) {
                continue;
            }

            livingEntityAgent.getBackingAgent().getDebug().getDiagnostics().setEnabled(true);
            diagnosticsEnabledEntityUuids.add(entityUuid);
        }

        for (var iterator = diagnosticsEnabledEntityUuids.iterator(); iterator.hasNext();) {
            var entityUuid = iterator.next();
            if (currentlyTracked.contains(entityUuid)) {
                continue;
            }

            disableDiagnostics(server, entityUuid);
            iterator.remove();
        }
    }

    private void disableTrackedDiagnostics(MinecraftServer server) {
        for (var entityUuid : diagnosticsEnabledEntityUuids) {
            disableDiagnostics(server, entityUuid);
        }
    }

    @SuppressWarnings("unchecked")
    private static void disableDiagnostics(MinecraftServer server, UUID entityUuid) {
        var entity = resolveEntity(server, entityUuid);
        if (!(entity instanceof LivingEntity livingEntity) || !(livingEntity instanceof GOAPUser<?> goapUser)) {
            return;
        }

        var livingEntityAgent = ((GOAPUser<LivingEntity>) goapUser).blib$getGOAPAgentOrNull();
        if (livingEntityAgent == null) {
            return;
        }

        var diagnostics = livingEntityAgent.getBackingAgent().getDebug().getDiagnostics();
        diagnostics.setEnabled(false);
        diagnostics.clear();
    }

    private static @Nullable Entity resolveEntity(MinecraftServer server, UUID entityUuid) {
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(entityUuid);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }
}
