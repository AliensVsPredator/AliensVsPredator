package com.blib.internal.common.goap;

import com.just.goap.plan.executor.impl.ConcurrentPlanExecutor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload.GOAPAgentDebugData;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload.GOAPPlanDebugData;

@ApiStatus.Internal
public final class GOAPDebugTracker {

    public static final GOAPDebugTracker INSTANCE = new GOAPDebugTracker();

    private static final int DEDICATED_TICK_INTERVAL = 10;

    private final Map<UUID, TrackingState> trackingByPlayer = new ConcurrentHashMap<>();

    private int tickCounter;

    private GOAPDebugTracker() {}

    public void track(UUID playerUuid, List<UUID> entityUuids) {
        trackingByPlayer.put(playerUuid, new TrackingState(new ArrayList<>(entityUuids), 0));
    }

    public void untrack(UUID playerUuid) {
        trackingByPlayer.remove(playerUuid);
    }

    public int next(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null || state.entityUuids.isEmpty()) {
            return -1;
        }

        state.selectedIndex = (state.selectedIndex + 1) % state.entityUuids.size();
        return state.selectedIndex;
    }

    public int previous(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null || state.entityUuids.isEmpty()) {
            return -1;
        }

        state.selectedIndex = (state.selectedIndex - 1 + state.entityUuids.size()) % state.entityUuids.size();
        return state.selectedIndex;
    }

    public @Nullable TrackingState getTrackingState(UUID playerUuid) {
        return trackingByPlayer.get(playerUuid);
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

            sendDebugPayload(server, player, state);
        }
    }

    @SuppressWarnings("unchecked")
    private void sendDebugPayload(MinecraftServer server, ServerPlayer player, TrackingState state) {
        var agents = new ArrayList<GOAPAgentDebugData>();

        for (var entityUuid : state.entityUuids) {
            var entity = resolveEntity(server, entityUuid);

            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            var goapUser = (GOAPUser<LivingEntity>) livingEntity;
            var livingEntityAgent = goapUser.blib$getGOAPAgentOrNull();

            if (livingEntityAgent == null) {
                continue;
            }

            var agent = livingEntityAgent.getBackingAgent();
            var executor = agent.getPlanExecutor();
            var plans = new ArrayList<GOAPPlanDebugData>();

            if (executor instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
                for (var plan : concurrent.getActivePlans()) {
                    var actionNames = new ArrayList<String>();

                    for (var action : plan.getActions()) {
                        actionNames.add(action.getName());
                    }

                    plans.add(
                        new GOAPPlanDebugData(
                            plan.getGoal().getName(),
                            plan.getPlanState().name(),
                            plan.getInitialCost(),
                            plan.getActionTick(),
                            actionNames
                        )
                    );
                }
            }

            agents.add(
                new GOAPAgentDebugData(
                    livingEntity.getId(),
                    livingEntity.getName().getString(),
                    agent.getTick(),
                    agent.hasPlan(),
                    plans
                )
            );
        }

        if (state.selectedIndex >= agents.size() && !agents.isEmpty()) {
            state.selectedIndex = 0;
        }

        var payload = new S2CGOAPDebugPayload(agents, state.selectedIndex);
        BLib.MOD.networking().sendToClient(player, payload);
    }

    private @Nullable Entity resolveEntity(MinecraftServer server, UUID entityUuid) {
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(entityUuid);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    public static final class TrackingState {

        private final List<UUID> entityUuids;

        private int selectedIndex;

        TrackingState(List<UUID> entityUuids, int selectedIndex) {
            this.entityUuids = entityUuids;
            this.selectedIndex = selectedIndex;
        }

        public List<UUID> entityUuids() {
            return entityUuids;
        }

        public int selectedIndex() {
            return selectedIndex;
        }
    }
}
