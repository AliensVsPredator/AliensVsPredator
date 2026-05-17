package com.blib.mod.common.gameplay.goap;

import com.just.ai.goap.Agent;
import com.just.ai.goap.StateKey;
import com.just.ai.goap.action.Action;
import com.just.ai.goap.condition.Condition;
import com.just.ai.goap.condition.ConditionContainer;
import com.just.ai.goap.effect.Effect;
import com.just.ai.goap.goal.Goal;
import com.just.ai.goap.graph.Graph;
import com.just.ai.goap.plan.Plan;
import com.just.ai.goap.plan.executor.impl.ConcurrentPlanExecutor;
import com.just.ai.goap.sensor.Sensor;
import com.just.ai.goap.state.Blackboard;
import com.just.ai.goap.state.ReadableWorldState;
import com.just.ai.goap.state.SensingWorldState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.BLib;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;
import com.blib.mod.client.render.goap.model.GOAPGraphDebugData;
import com.blib.mod.client.render.goap.model.GOAPGraphEdgeDebugData;
import com.blib.mod.client.render.goap.model.GOAPGraphNodeDebugData;
import com.blib.mod.client.render.goap.model.GOAPPlanDebugData;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
final class GOAPDebugPayloadBuilder {

    private static final int MAX_LABEL_LENGTH = 80;

    private static final int MAX_DETAIL_LENGTH = 180;

    private static final int MAX_DETAILS_PER_NODE = 32;

    private static final int MAX_DIAGNOSTICS = 160;

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
        var debugWorldState = buildDebugWorldState(livingEntity, graph, agent.getCurrentWorldState());
        var plans = buildPlanData(agent, debugWorldState);
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

        var worldState = debugWorldState == null ? Map.<String, String>of() : snapshotWorldState(debugWorldState);
        var graphDebug = graph == null
            ? GOAPGraphDebugData.EMPTY
            : buildGraphData(livingEntity, agent, graph, debugWorldState);
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
            worldState,
            snapshotBlackboard(agent.getBlackboard()),
            snapshotBlackboard(agent.getGraphBlackboard()),
            graphDebug
        );
    }

    private static List<GOAPPlanDebugData> buildPlanData(
        Agent<LivingEntity> agent,
        @Nullable ReadableWorldState worldState
    ) {
        var executor = agent.getPlanExecutor();
        var plans = new ArrayList<GOAPPlanDebugData>();

        if (executor instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
            for (var plan : concurrent.getActivePlans()) {
                plans.add(buildSinglePlanData(agent, plan, worldState));
            }
        }

        return plans;
    }

    private static GOAPPlanDebugData buildSinglePlanData(
        Agent<LivingEntity> agent,
        Plan<LivingEntity> plan,
        @Nullable ReadableWorldState worldState
    ) {
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
            planBlackboard,
            plan.getTick(),
            plan.getActionTick(),
            safeRemainingCost(agent, plan, worldState),
            currentActionName(plan)
        );
    }

    private static GOAPGraphDebugData buildGraphData(
        LivingEntity livingEntity,
        Agent<LivingEntity> agent,
        Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState
    ) {
        var nodes = new ArrayList<GOAPGraphNodeDebugData>();
        var edges = new ArrayList<GOAPGraphEdgeDebugData>();
        var diagnostics = new ArrayList<String>();
        var conditionsById = collectGraphConditions(graph);
        var activePlans = activePlanSnapshot(agent, worldState);

        addDiagnostic(
            diagnostics,
            "agent tick=%d has_plan=%s active_plans=%d world_state_entries=%d"
                .formatted(agent.getTick(), agent.hasPlan(), activePlans.activePlanCount(), worldStateSize(worldState))
        );
        addDiagnostic(
            diagnostics,
            "graph goals=%d actions=%d sensor_keys=%d conditions=%d"
                .formatted(
                    graph.getAvailableGoals().size(),
                    graph.getAvailableActions().size(),
                    graph.getSensorMap().size(),
                    conditionsById.size()
                )
        );

        buildSensorNodes(graph, worldState, conditionsById, nodes, edges);
        buildConditionNodes(graph, worldState, conditionsById, nodes);
        buildActionNodes(livingEntity, graph, worldState, conditionsById, activePlans, nodes, edges, diagnostics);
        buildGoalNodes(graph, worldState, activePlans, nodes, edges, diagnostics);

        addDiagnostic(diagnostics, "payload nodes=%d edges=%d".formatted(nodes.size(), edges.size()));

        return new GOAPGraphDebugData(nodes, edges, diagnostics);
    }

    private static void buildSensorNodes(
        Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState,
        Map<String, Condition<?>> conditionsById,
        List<GOAPGraphNodeDebugData> nodes,
        List<GOAPGraphEdgeDebugData> edges
    ) {
        var seenSensors = Collections.newSetFromMap(new IdentityHashMap<Sensor<? super LivingEntity>, Boolean>());
        var sensorEntries = new ArrayList<>(graph.getSensorMap().entrySet());
        sensorEntries.sort(Comparator.comparing(entry -> entry.getKey().id()));

        for (var entry : sensorEntries) {
            var sensor = entry.getValue();

            if (!seenSensors.add(sensor)) {
                continue;
            }

            var outputKeys = sortedSensorOutputKeys(sensor);
            var details = new ArrayList<String>();
            var sensedCount = 0;

            for (var outputKey : outputKeys) {
                var value = safeWorldStateValue(worldState, outputKey);
                addDetail(details, "output %s=%s".formatted(outputKey.id(), value.value()));

                if (value.present()) {
                    sensedCount++;
                }
            }

            var status = outputKeys.isEmpty()
                ? "NO_OUTPUTS"
                : sensedCount == outputKeys.size() ? "SENSED" : sensedCount == 0 ? "UNSENSED" : "PARTIAL";
            nodes.add(
                new GOAPGraphNodeDebugData(
                    sensorId(sensor),
                    "SENSOR",
                    truncate(sensor.getClass().getSimpleName()),
                    status,
                    "%d/%d outputs".formatted(sensedCount, outputKeys.size()),
                    0,
                    details
                )
            );

            var outputIds = new LinkedHashSet<String>();
            for (var outputKey : outputKeys) {
                outputIds.add(outputKey.id());
            }

            for (var condition : conditionsById.values()) {
                if (condition instanceof Condition.Sensed<?> && outputIds.contains(condition.key().id())) {
                    edges.add(
                        new GOAPGraphEdgeDebugData(
                            sensorId(sensor),
                            conditionId(condition),
                            "SENSES",
                            "senses",
                            false
                        )
                    );
                }
            }
        }
    }

    private static void buildConditionNodes(
        Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState,
        Map<String, Condition<?>> conditionsById,
        List<GOAPGraphNodeDebugData> nodes
    ) {
        for (var condition : conditionsById.values()) {
            var snapshot = conditionSnapshot(condition, worldState);
            var satisfiers = sortedActionNames(graph.getActionsThatSatisfy(condition));
            var details = new ArrayList<String>();
            addDetail(details, "key=%s".formatted(condition.key().id()));
            addDetail(details, "type=%s".formatted(conditionKind(condition)));
            addDetail(details, "expression=%s".formatted(condition.toString()));
            addDetail(details, "world_state=%s".formatted(snapshot.value()));
            addDetail(details, "satisfied=%s".formatted(snapshot.satisfied()));
            addDetail(details, "satisfying_actions=%s".formatted(satisfiers.isEmpty() ? "<none>" : String.join(", ", satisfiers)));

            if (snapshot.error() != null) {
                addDetail(details, "error=%s".formatted(snapshot.error()));
            }

            nodes.add(
                new GOAPGraphNodeDebugData(
                    conditionId(condition),
                    condition instanceof Condition.Sensed<?> ? "SENSED_CONDITION" : "DERIVED_CONDITION",
                    truncate(condition.toString()),
                    snapshot.status(),
                    snapshot.value(),
                    condition instanceof Condition.Sensed<?> ? 1 : 3,
                    details
                )
            );
        }
    }

    private static void buildActionNodes(
        LivingEntity livingEntity,
        Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState,
        Map<String, Condition<?>> conditionsById,
        ActivePlanSnapshot activePlans,
        List<GOAPGraphNodeDebugData> nodes,
        List<GOAPGraphEdgeDebugData> edges,
        List<String> diagnostics
    ) {
        for (var action : sortedActions(graph)) {
            var planTime = containerSnapshot(action.getPlanTimePreconditionContainer(), worldState);
            var runtime = containerSnapshot(action.getRuntimePreconditionContainer(), worldState);
            var current = activePlans.currentActions().contains(action);
            var planned = activePlans.plannedActions().contains(action);
            var status = current
                ? "CURRENT"
                : planned ? "PLANNED" : planTime.satisfied() && runtime.satisfied() ? "READY" : "BLOCKED";
            var cost = safeActionCost(action, livingEntity, worldState);
            var details = new ArrayList<String>();

            addDetail(details, "cost=%s".formatted(cost));
            addDetail(details, "plan_time_satisfied=%s".formatted(planTime.summary()));
            addConditionDetails(details, "plan_time_condition", action.getPlanTimePreconditionContainer(), worldState);
            addDetail(details, "runtime_satisfied=%s".formatted(runtime.summary()));
            addConditionDetails(details, "runtime_condition", action.getRuntimePreconditionContainer(), worldState);
            addDetail(details, "outcome_already_true=%s".formatted(effectsSatisfiedNow(action, worldState)));

            for (var effect : action.getEffectContainer().getEffects()) {
                addDetail(details, "produces=%s".formatted(effectSummary(effect)));
            }

            for (var usage : activePlans.actionUsages().getOrDefault(action, List.of())) {
                addDetail(details, "active_plan %s".formatted(usage));
            }

            nodes.add(
                new GOAPGraphNodeDebugData(
                    actionId(action),
                    "ACTION",
                    truncate(action.getName()),
                    status,
                    cost,
                    2,
                    details
                )
            );

            addActionPreconditionEdges(action, action.getPlanTimePreconditionContainer(), "PLAN_PRECONDITION", "plan", planned, edges);
            addActionPreconditionEdges(action, action.getRuntimePreconditionContainer(), "RUNTIME_PRECONDITION", "runtime", current, edges);
            addActionEffectEdges(action, conditionsById, planned || current, edges);

            if (!planned && (!planTime.satisfied() || !runtime.satisfied())) {
                addDiagnostic(
                    diagnostics,
                    "action %s blocked: plan_time=%s runtime=%s"
                        .formatted(action.getName(), planTime.unsatisfiedSummary(), runtime.unsatisfiedSummary())
                );
            }
        }
    }

    private static void buildGoalNodes(
        Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState,
        ActivePlanSnapshot activePlans,
        List<GOAPGraphNodeDebugData> nodes,
        List<GOAPGraphEdgeDebugData> edges,
        List<String> diagnostics
    ) {
        for (var goal : sortedGoals(graph)) {
            var desired = containerSnapshot(goal.getDesiredConditions(), worldState);
            var preconditions = containerSnapshot(goal.getPreconditions(), worldState);
            var active = activePlans.activeGoals().contains(goal);
            var unreachable = hasUnreachableDesiredCondition(graph, goal, worldState);
            var status = active
                ? "ACTIVE"
                : !preconditions.satisfied() ? "BLOCKED" : desired.satisfied() ? "SATISFIED" : unreachable ? "UNREACHABLE" : "ELIGIBLE";
            var details = new ArrayList<String>();

            addDetail(details, "goal_preconditions_satisfied=%s".formatted(preconditions.summary()));
            addConditionDetails(details, "goal_precondition", goal.getPreconditions(), worldState);
            addDetail(details, "desired_conditions_satisfied=%s".formatted(desired.summary()));

            for (var condition : goal.getDesiredConditions().getConditions()) {
                var satisfiers = sortedActionNames(graph.getActionsThatSatisfy(condition));
                addConditionDetail(
                    details,
                    "desired_condition",
                    condition,
                    worldState,
                    "satisfiers=%s".formatted(satisfiers.isEmpty() ? "<none>" : String.join(", ", satisfiers))
                );
            }

            for (var usage : activePlans.goalUsages().getOrDefault(goal, List.of())) {
                addDetail(details, "active_plan %s".formatted(usage));
            }

            nodes.add(
                new GOAPGraphNodeDebugData(
                    goalId(goal),
                    "GOAL",
                    truncate(goal.getName()),
                    status,
                    desired.summary(),
                    4,
                    details
                )
            );

            for (var condition : goal.getDesiredConditions().getConditions()) {
                edges.add(new GOAPGraphEdgeDebugData(goalId(goal), conditionId(condition), "DESIRES", "desires", active));
            }

            for (var condition : goal.getPreconditions().getConditions()) {
                edges.add(new GOAPGraphEdgeDebugData(conditionId(condition), goalId(goal), "GOAL_PRECONDITION", "pre", active));
            }

            if (!activePlans.hasPlans()) {
                addNoPlanGoalDiagnostic(graph, goal, preconditions, desired, worldState, diagnostics);
            }
        }
    }

    private static void addActionPreconditionEdges(
        Action<? super LivingEntity> action,
        ConditionContainer conditions,
        String kind,
        String label,
        boolean active,
        List<GOAPGraphEdgeDebugData> edges
    ) {
        for (var condition : conditions.getConditions()) {
            edges.add(new GOAPGraphEdgeDebugData(conditionId(condition), actionId(action), kind, label, active));
        }
    }

    private static void addActionEffectEdges(
        Action<? super LivingEntity> action,
        Map<String, Condition<?>> conditionsById,
        boolean active,
        List<GOAPGraphEdgeDebugData> edges
    ) {
        for (var condition : conditionsById.values()) {
            if (!safelySatisfiedByEffects(condition, action)) {
                continue;
            }

            edges.add(new GOAPGraphEdgeDebugData(actionId(action), conditionId(condition), "SATISFIES", "effect", active));
        }
    }

    private static void addNoPlanGoalDiagnostic(
        Graph<LivingEntity> graph,
        Goal goal,
        ContainerSnapshot preconditions,
        ContainerSnapshot desired,
        @Nullable ReadableWorldState worldState,
        List<String> diagnostics
    ) {
        if (!preconditions.satisfied()) {
            addDiagnostic(diagnostics, "goal %s blocked by %s".formatted(goal.getName(), preconditions.unsatisfiedSummary()));
            return;
        }

        if (desired.satisfied()) {
            addDiagnostic(diagnostics, "goal %s already satisfied by current world state".formatted(goal.getName()));
            return;
        }

        var unreachable = new ArrayList<String>();
        var eligible = new ArrayList<String>();

        for (var condition : goal.getDesiredConditions().getConditions()) {
            if (conditionSnapshot(condition, worldState).satisfied()) {
                continue;
            }

            var satisfiers = sortedActionNames(graph.getActionsThatSatisfy(condition));

            if (satisfiers.isEmpty()) {
                unreachable.add(condition.toString());
            } else {
                eligible.add("%s <= %s".formatted(condition, String.join(", ", satisfiers)));
            }
        }

        if (!unreachable.isEmpty()) {
            addDiagnostic(diagnostics, "goal %s unreachable: %s".formatted(goal.getName(), String.join("; ", unreachable)));
        } else {
            addDiagnostic(diagnostics, "goal %s eligible root satisfiers: %s".formatted(goal.getName(), String.join("; ", eligible)));
        }
    }

    private static Map<String, Condition<?>> collectGraphConditions(Graph<LivingEntity> graph) {
        var conditions = new TreeMap<String, Condition<?>>();

        for (var goal : sortedGoals(graph)) {
            addConditions(conditions, goal.getPreconditions());
            addConditions(conditions, goal.getDesiredConditions());
        }

        for (var action : sortedActions(graph)) {
            addConditions(conditions, action.getPlanTimePreconditionContainer());
            addConditions(conditions, action.getRuntimePreconditionContainer());
        }

        return conditions;
    }

    private static void addConditions(Map<String, Condition<?>> conditionsById, ConditionContainer container) {
        for (var condition : container.getConditions()) {
            conditionsById.putIfAbsent(conditionId(condition), condition);
        }
    }

    private static ActivePlanSnapshot activePlanSnapshot(
        Agent<LivingEntity> agent,
        @Nullable ReadableWorldState worldState
    ) {
        var plannedActions = Collections.newSetFromMap(new IdentityHashMap<Action<? super LivingEntity>, Boolean>());
        var currentActions = Collections.newSetFromMap(new IdentityHashMap<Action<? super LivingEntity>, Boolean>());
        var activeGoals = Collections.newSetFromMap(new IdentityHashMap<Goal, Boolean>());
        var actionUsages = new IdentityHashMap<Action<? super LivingEntity>, List<String>>();
        var goalUsages = new IdentityHashMap<Goal, List<String>>();
        var activePlanCount = 0;

        if (agent.getPlanExecutor() instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
            for (var plan : concurrent.getActivePlans()) {
                activePlanCount++;
                var goal = plan.getGoal();
                activeGoals.add(goal);
                goalUsages.computeIfAbsent(goal, $ -> new ArrayList<>())
                    .add(
                        "state=%s cost=%.2f remaining=%.2f tick=%d action_tick=%d current=%s"
                            .formatted(
                                plan.getPlanState().name(),
                                plan.getInitialCost(),
                                safeRemainingCost(agent, plan, worldState),
                                plan.getTick(),
                                plan.getActionTick(),
                                currentActionName(plan)
                            )
                    );

                var actions = plan.getActions();

                for (int i = 0; i < actions.size(); i++) {
                    var action = actions.get(i);
                    var current = i == plan.getCurrentActionIndex();
                    plannedActions.add(action);

                    if (current) {
                        currentActions.add(action);
                    }

                    actionUsages.computeIfAbsent(action, $ -> new ArrayList<>())
                        .add(
                            "goal=%s index=%d/%d current=%s plan_tick=%d action_tick=%d"
                                .formatted(goal.getName(), i, actions.size(), current, plan.getTick(), plan.getActionTick())
                        );
                }
            }
        }

        return new ActivePlanSnapshot(plannedActions, currentActions, activeGoals, actionUsages, goalUsages, activePlanCount);
    }

    private static void populateSensorValues(
        @Nullable Graph<LivingEntity> graph,
        @Nullable ReadableWorldState worldState
    ) {
        if (graph == null || worldState == null) {
            return;
        }

        var sensorKeys = new ArrayList<>(graph.getSensorMap().keySet());
        sensorKeys.sort(Comparator.comparing(key -> key.id()));

        for (var sensorKey : sensorKeys) {
            try {
                worldState.getOrNull(sensorKey);
            } catch (RuntimeException ignored) {
                // The graph node records the failing key as an error later; keep the debug packet alive.
            }
        }
    }

    private static @Nullable ReadableWorldState buildDebugWorldState(
        LivingEntity livingEntity,
        @Nullable Graph<LivingEntity> graph,
        @Nullable ReadableWorldState currentWorldState
    ) {
        if (graph == null) {
            return currentWorldState;
        }

        var debugWorldState = new SensingWorldState<>(graph);
        debugWorldState.setActor(livingEntity);

        if (currentWorldState != null) {
            debugWorldState.setAll(currentWorldState.getMap());
        }

        populateSensorValues(graph, debugWorldState);
        return debugWorldState;
    }

    private static List<Action<? super LivingEntity>> sortedActions(Graph<LivingEntity> graph) {
        var actions = new ArrayList<>(graph.getAvailableActions());
        actions.sort(Comparator.comparing(action -> action.getName()));
        return actions;
    }

    private static List<Goal> sortedGoals(Graph<LivingEntity> graph) {
        var goals = new ArrayList<>(graph.getAvailableGoals());
        goals.sort(Comparator.comparing(goal -> goal.getName()));
        return goals;
    }

    private static List<String> sortedActionNames(Set<Action<? super LivingEntity>> actions) {
        var names = new ArrayList<String>();

        for (var action : actions) {
            names.add(action.getName());
        }

        names.sort(String::compareTo);
        return names;
    }

    private static List<StateKey.Sensed<?>> sortedSensorOutputKeys(Sensor<? super LivingEntity> sensor) {
        var keys = new ArrayList<>(sensor.outputKeys());
        keys.sort(Comparator.comparing(key -> key.id()));
        return keys;
    }

    private static boolean hasUnreachableDesiredCondition(
        Graph<LivingEntity> graph,
        Goal goal,
        @Nullable ReadableWorldState worldState
    ) {
        for (var condition : goal.getDesiredConditions().getConditions()) {
            if (conditionSnapshot(condition, worldState).satisfied()) {
                continue;
            }

            if (graph.getActionsThatSatisfy(condition).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static boolean safelySatisfiedByEffects(Condition<?> condition, Action<? super LivingEntity> action) {
        try {
            return condition.satisfiedBy(action.getEffectContainer());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean effectsSatisfiedNow(
        Action<? super LivingEntity> action,
        @Nullable ReadableWorldState worldState
    ) {
        if (worldState == null) {
            return false;
        }

        try {
            return worldState.satisfies(action.getEffectContainer());
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static String safeActionCost(
        Action<? super LivingEntity> action,
        LivingEntity livingEntity,
        @Nullable ReadableWorldState worldState
    ) {
        if (worldState == null) {
            return "<no world state>";
        }

        try {
            return String.format(Locale.ROOT, "%.2f", action.getCost(livingEntity, worldState));
        } catch (RuntimeException exception) {
            return "<%s>".formatted(exception.getClass().getSimpleName());
        }
    }

    private static float safeRemainingCost(
        Agent<LivingEntity> agent,
        Plan<LivingEntity> plan,
        @Nullable ReadableWorldState worldState
    ) {
        if (worldState == null) {
            return -1.0f;
        }

        try {
            return plan.getRemainingCost(agent.getActor(), worldState);
        } catch (RuntimeException ignored) {
            return -1.0f;
        }
    }

    private static String currentActionName(Plan<LivingEntity> plan) {
        var index = plan.getCurrentActionIndex();
        var actions = plan.getActions();

        if (index < 0 || index >= actions.size()) {
            return "<none>";
        }

        return actions.get(index).getName();
    }

    private static ContainerSnapshot containerSnapshot(
        ConditionContainer container,
        @Nullable ReadableWorldState worldState
    ) {
        var satisfied = true;
        var unsatisfied = new ArrayList<String>();
        var total = container.getConditions().size();

        for (var condition : container.getConditions()) {
            var snapshot = conditionSnapshot(condition, worldState);

            if (!snapshot.satisfied()) {
                satisfied = false;
                unsatisfied.add(condition.toString());
            }
        }

        return new ContainerSnapshot(satisfied, total, total - unsatisfied.size(), unsatisfied);
    }

    private static void addConditionDetails(
        List<String> details,
        String key,
        ConditionContainer container,
        @Nullable ReadableWorldState worldState
    ) {
        for (var condition : container.getConditions()) {
            addConditionDetail(details, key, condition, worldState, "");
        }
    }

    private static void addConditionDetail(
        List<String> details,
        String key,
        Condition<?> condition,
        @Nullable ReadableWorldState worldState,
        String extra
    ) {
        var snapshot = conditionSnapshot(condition, worldState);
        var detail = "%s=%s | %s | world=%s".formatted(key, snapshot.status(), condition, snapshot.value());
        if (!extra.isBlank()) {
            detail += " | " + extra;
        }
        if (snapshot.error() != null) {
            detail += " | error=" + snapshot.error();
        }
        addDetail(details, detail);
    }

    private static ConditionSnapshot conditionSnapshot(
        Condition<?> condition,
        @Nullable ReadableWorldState worldState
    ) {
        if (worldState == null) {
            return new ConditionSnapshot(false, false, "<no world state>", "UNKNOWN", null);
        }

        try {
            var value = worldState.getOrNull(condition.key());
            var present = value != null;
            var satisfied = condition.satisfiedBy(worldState);
            var status = satisfied ? "SATISFIED" : present ? "UNSATISFIED" : "MISSING";
            return new ConditionSnapshot(satisfied, present, stringify(value, "<unset>"), status, null);
        } catch (RuntimeException exception) {
            return new ConditionSnapshot(false, false, "<error>", "ERROR", exception.getClass().getSimpleName());
        }
    }

    private static WorldStateValue safeWorldStateValue(
        @Nullable ReadableWorldState worldState,
        StateKey<?> key
    ) {
        if (worldState == null) {
            return new WorldStateValue(false, "<no world state>");
        }

        try {
            var value = worldState.getOrNull(key);
            return new WorldStateValue(value != null, stringify(value, "<unset>"));
        } catch (RuntimeException exception) {
            return new WorldStateValue(false, "<%s>".formatted(exception.getClass().getSimpleName()));
        }
    }

    private static int worldStateSize(@Nullable ReadableWorldState worldState) {
        return worldState == null ? 0 : worldState.getMap().size();
    }

    private static String conditionKind(Condition<?> condition) {
        return condition instanceof Condition.Sensed<?> ? "sensed" : "derived";
    }

    private static String effectSummary(Effect<?> effect) {
        if (effect instanceof Effect.Value<?> value) {
            return "%s=%s".formatted(effect.key().id(), stringify(value.value(), "null"));
        }

        return "%s=<dynamic>".formatted(effect.key().id());
    }

    private static String sensorId(Sensor<? super LivingEntity> sensor) {
        var outputIds = new ArrayList<String>();

        for (var key : sortedSensorOutputKeys(sensor)) {
            outputIds.add(key.id());
        }

        return "sensor:%s:%s".formatted(sensor.getClass().getName(), String.join(",", outputIds));
    }

    private static String actionId(Action<? super LivingEntity> action) {
        return "action:%s:%s".formatted(action.getName(), Integer.toHexString(System.identityHashCode(action)));
    }

    private static String goalId(Goal goal) {
        return "goal:%s:%s".formatted(goal.getName(), Integer.toHexString(System.identityHashCode(goal)));
    }

    private static String conditionId(Condition<?> condition) {
        return "condition:%s:%s".formatted(conditionKind(condition), condition.toString());
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
                (selectedAgent.graphSensorKeys().size() + GOAPDebugTracker.getWorldStatePageSize() - 1)
                    / GOAPDebugTracker.getWorldStatePageSize()
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

    private static Map<String, String> snapshotBlackboard(Blackboard blackboard) {
        var result = new LinkedHashMap<String, String>();
        var stateMap = blackboard.getStateMap();

        for (var bbEntry : stateMap.entrySet()) {
            result.put(truncate(bbEntry.getKey().id()), truncate(String.valueOf(bbEntry.getValue())));
        }

        return result;
    }

    static String truncate(String value) {
        return truncate(value, MAX_LABEL_LENGTH);
    }

    private static String truncateDetail(String value) {
        return truncate(value, MAX_DETAIL_LENGTH);
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return "null";
        }

        return value.length() > maxLength ? value.substring(0, maxLength) + "..." : value;
    }

    private static String stringify(@Nullable Object value, String nullValue) {
        return value == null ? nullValue : truncateDetail(String.valueOf(value));
    }

    private static void addDetail(List<String> details, String detail) {
        if (details.size() >= MAX_DETAILS_PER_NODE) {
            return;
        }

        details.add(truncateDetail(detail));
    }

    private static void addDiagnostic(List<String> diagnostics, String diagnostic) {
        if (diagnostics.size() >= MAX_DIAGNOSTICS) {
            return;
        }

        diagnostics.add(truncateDetail(diagnostic));
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

    private record ActivePlanSnapshot(
        Set<Action<? super LivingEntity>> plannedActions,
        Set<Action<? super LivingEntity>> currentActions,
        Set<Goal> activeGoals,
        Map<Action<? super LivingEntity>, List<String>> actionUsages,
        Map<Goal, List<String>> goalUsages,
        int activePlanCount
    ) {

        boolean hasPlans() {
            return activePlanCount > 0;
        }
    }

    private record ConditionSnapshot(
        boolean satisfied,
        boolean present,
        String value,
        String status,
        @Nullable String error
    ) {}

    private record ContainerSnapshot(
        boolean satisfied,
        int total,
        int satisfiedCount,
        List<String> unsatisfied
    ) {

        String summary() {
            return "%d/%d".formatted(satisfiedCount, total);
        }

        String unsatisfiedSummary() {
            return unsatisfied.isEmpty() ? "<none>" : String.join("; ", unsatisfied);
        }
    }

    private record WorldStateValue(
        boolean present,
        String value
    ) {}
}
