package com.blib.mod.common.gameplay.goap;

import com.just.goap.Agent;
import com.just.goap.graph.Graph;
import com.just.goap.plan.Plan;
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

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.BLib;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;
import com.blib.mod.client.render.goap.model.GOAPPlanDebugData;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
final class GOAPDebugPayloadBuilder {

    private GOAPDebugPayloadBuilder() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    static void buildAndSend(MinecraftServer server, ServerPlayer player, GOAPDebugTrackingState state) {
        var agents = new ArrayList<GOAPAgentDebugData>();

        for (var entityUuid : state.entityUuids()) {
            var entity = resolveEntity(server, entityUuid);

            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            var goapUser = (GOAPUser<LivingEntity>) livingEntity;
            var livingEntityAgent = goapUser.blib$getGOAPAgentOrNull();

            if (livingEntityAgent == null) {
                continue;
            }

            var graph = goapUser.blib$getGOAPGraphOrNull();
            var agent = livingEntityAgent.getBackingAgent();
            agents.add(buildAgentData(livingEntity, agent, graph));
        }

        clampSelection(state, agents);
        var wsPage = clampWorldStatePage(state, agents);

        var payload = new S2CGOAPDebugPayload(agents, state.selectedIndex(), state.worldStateAutoPage(), wsPage);
        BLib.MOD.networking().sendToClient(player, payload);
    }

    private static GOAPAgentDebugData buildAgentData(
        LivingEntity livingEntity,
        Agent<LivingEntity> agent,
        @Nullable Graph<LivingEntity> graph
    ) {
        var plans = buildPlanData(agent);
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

        return new GOAPAgentDebugData(
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
        );
    }

    private static List<GOAPPlanDebugData> buildPlanData(Agent<LivingEntity> agent) {
        var executor = agent.getPlanExecutor();
        var plans = new ArrayList<GOAPPlanDebugData>();

        if (executor instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
            for (var plan : concurrent.getActivePlans()) {
                plans.add(buildSinglePlanData(plan));
            }
        }

        return plans;
    }

    private static GOAPPlanDebugData buildSinglePlanData(Plan<LivingEntity> plan) {
        var actionNames = new ArrayList<String>();

        for (var action : plan.getActions()) {
            actionNames.add(action.getName());
        }

        var actionBlackboard = snapshotBlackboard(plan.getActionBlackboard());
        var planBlackboard = snapshotBlackboard(plan.getBlackboard());

        return new GOAPPlanDebugData(
            plan.getGoal().getName(),
            plan.getPlanState().name(),
            plan.getInitialCost(),
            plan.getCurrentActionIndex(),
            actionNames,
            actionBlackboard,
            planBlackboard
        );
    }

    private static void clampSelection(GOAPDebugTrackingState state, List<GOAPAgentDebugData> agents) {
        if (state.selectedIndex() >= agents.size() && !agents.isEmpty()) {
            state.setSelectedIndex(0);
        }
    }

    private static int clampWorldStatePage(GOAPDebugTrackingState state, List<GOAPAgentDebugData> agents) {
        var wsPage = state.worldStatePage();

        if (!state.worldStateAutoPage() && !agents.isEmpty() && state.selectedIndex() < agents.size()) {
            var selectedAgent = agents.get(state.selectedIndex());
            var totalPages = Math.max(
                1,
                (selectedAgent.graphSensorKeys().size() + GOAPDebugTracker.WORLD_STATE_PAGE_SIZE - 1)
                    / GOAPDebugTracker.WORLD_STATE_PAGE_SIZE
            );
            wsPage = ((wsPage % totalPages) + totalPages) % totalPages;
            state.setWorldStatePage(wsPage);
        }

        return wsPage;
    }

    private static Map<String, String> snapshotWorldState(ReadableWorldState worldState) {
        var result = new TreeMap<String, String>();

        for (var entry : worldState.getMap().entrySet()) {
            result.put(truncate(entry.getKey().id()), truncate(String.valueOf(entry.getValue())));
        }

        return result;
    }

    private static Map<String, String> snapshotBlackboard(com.just.goap.state.Blackboard blackboard) {
        var result = new LinkedHashMap<String, String>();
        var stateMap = blackboard.getStateMap();

        for (var bbEntry : stateMap.entrySet()) {
            result.put(bbEntry.getKey().id(), String.valueOf(bbEntry.getValue()));
        }

        return result;
    }

    static String truncate(String value) {
        return value.length() > 32 ? value.substring(0, 32) + "..." : value;
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
