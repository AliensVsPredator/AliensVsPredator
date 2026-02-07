package com.blib.internal.common.goap;

import com.just.goap.plan.executor.impl.ConcurrentPlanExecutor;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    public static final int WORLD_STATE_PAGE_SIZE = 30;

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

    public boolean worldStateNext(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.worldStateAutoPage = false;
        state.worldStatePage++;
        return true;
    }

    public boolean worldStatePrevious(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.worldStateAutoPage = false;
        state.worldStatePage--;
        return true;
    }

    public boolean worldStatePage(UUID playerUuid, int page) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.worldStateAutoPage = false;
        state.worldStatePage = page;
        return true;
    }

    public boolean worldStateAuto(UUID playerUuid) {
        var state = trackingByPlayer.get(playerUuid);

        if (state == null) {
            return false;
        }

        state.worldStateAutoPage = true;
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
            var graph = goapUser.blib$getGOAPGraphOrNull();
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

                    var actionBlackboard = snapshotBlackboard(plan.getActionBlackboard());
                    var planBlackboard = snapshotBlackboard(plan.getBlackboard());

                    plans.add(
                        new GOAPPlanDebugData(
                            plan.getGoal().getName(),
                            plan.getPlanState().name(),
                            plan.getInitialCost(),
                            plan.getCurrentActionIndex(),
                            actionNames,
                            actionBlackboard,
                            planBlackboard
                        )
                    );
                }
            }

            var graphGoalCount = 0;
            var graphActionCount = 0;
            var graphSensorKeys = new ArrayList<String>();

            if (graph != null) {
                graphGoalCount = graph.getAvailableGoals().size();
                graphActionCount = graph.getAvailableActions().size();

                for (var sensorKey : graph.getSensorMap().keySet()) {
                    graphSensorKeys.add(truncate(sensorKey.id()));
                }

                graphSensorKeys.sort(String::compareTo);
            }

            var worldState = agent.getCurrentWorldState() == null
                ? Map.<String, String>of()
                : snapshotWorldState(agent.getCurrentWorldState());
            var pos = livingEntity.blockPosition();

            agents.add(
                new GOAPAgentDebugData(
                    livingEntity.getId(),
                    livingEntity.getName().getString(),
                    livingEntity.getUUID().toString(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    agent.getTick(),
                    agent.hasPlan(),
                    plans,
                    graphGoalCount,
                    graphActionCount,
                    graphSensorKeys,
                    worldState
                )
            );
        }

        if (state.selectedIndex >= agents.size() && !agents.isEmpty()) {
            state.selectedIndex = 0;
        }

        var wsPage = state.worldStatePage;

        if (!state.worldStateAutoPage && !agents.isEmpty() && state.selectedIndex < agents.size()) {
            var selectedAgent = agents.get(state.selectedIndex);
            var totalPages = Math.max(
                1,
                (selectedAgent.graphSensorKeys().size() + WORLD_STATE_PAGE_SIZE - 1) / WORLD_STATE_PAGE_SIZE
            );
            wsPage = ((wsPage % totalPages) + totalPages) % totalPages;
            state.worldStatePage = wsPage;
        }

        var payload = new S2CGOAPDebugPayload(agents, state.selectedIndex, state.worldStateAutoPage, wsPage);
        BLib.MOD.networking().sendToClient(player, payload);
    }

    private static Map<String, String> snapshotWorldState(
        ReadableWorldState worldState
    ) {
        var result = new TreeMap<String, String>();

        for (var entry : worldState.getMap().entrySet()) {
            result.put(truncate(entry.getKey().id()), truncate(String.valueOf(entry.getValue())));
        }

        return result;
    }

    private static Map<String, String> snapshotBlackboard(
        com.just.goap.state.Blackboard blackboard
    ) {
        var result = new LinkedHashMap<String, String>();
        var stateMap = blackboard.getStateMap();

        for (var bbEntry : stateMap.entrySet()) {
            result.put(bbEntry.getKey().id(), String.valueOf(bbEntry.getValue()));
        }

        return result;
    }

    private static String truncate(String value) {
        return value.length() > 32 ? value.substring(0, 32) + "..." : value;
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

        private boolean worldStateAutoPage = true;

        private int worldStatePage;

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
