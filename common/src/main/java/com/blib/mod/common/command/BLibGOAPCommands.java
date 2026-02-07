package com.blib.mod.common.command;

import com.just.goap.Agent;
import com.just.goap.action.Action;
import com.just.goap.goal.Goal;
import com.just.goap.graph.Graph;
import com.just.goap.plan.Plan;
import com.just.goap.plan.executor.impl.ConcurrentPlanExecutor;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.api.common.goap.v1.LivingEntityAgent;
import com.blib.api.common.goap.v1.action.BLibAction;
import com.blib.internal.common.goap.GOAPDebugTracker;
import com.blib.internal.mixin.MixinPlan_Accessor;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
public final class BLibGOAPCommands {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private BLibGOAPCommands() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("goap")
            .then(
                Commands.literal("track")
                    .then(
                        Commands.argument("targets", EntityArgument.entities())
                            .executes(BLibGOAPCommands::executeTrack)
                    )
            )
            .then(
                Commands.literal("untrack")
                    .executes(BLibGOAPCommands::executeUntrack)
            )
            .then(
                Commands.literal("next")
                    .executes(BLibGOAPCommands::executeNext)
            )
            .then(
                Commands.literal("previous")
                    .executes(BLibGOAPCommands::executePrevious)
            )
            .then(
                Commands.literal("worldstate")
                    .then(
                        Commands.literal("next")
                            .executes(BLibGOAPCommands::executeWorldStateNext)
                    )
                    .then(
                        Commands.literal("previous")
                            .executes(BLibGOAPCommands::executeWorldStatePrevious)
                    )
                    .then(
                        Commands.literal("page")
                            .then(
                                Commands.argument("page", IntegerArgumentType.integer(1))
                                    .executes(BLibGOAPCommands::executeWorldStatePage)
                            )
                    )
                    .then(
                        Commands.literal("auto")
                            .executes(BLibGOAPCommands::executeWorldStateAuto)
                    )
            )
            .then(
                Commands.argument("targets", EntityArgument.entities())
                    .executes(BLibGOAPCommands::executeInspect)
                    .then(
                        Commands.literal("snapshot")
                            .executes(BLibGOAPCommands::executeSnapshot)
                    )
            );
    }

    // region tracking

    @SuppressWarnings("unchecked")
    private static int executeTrack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var entities = EntityArgument.getEntities(context, "targets");
        var uuids = new ArrayList<UUID>();

        for (var entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            var goapUser = (GOAPUser<LivingEntity>) livingEntity;

            if (goapUser.blib$getGOAPGraphOrNull() == null) {
                continue;
            }

            if (goapUser.blib$getGOAPAgentOrNull() == null) {
                continue;
            }

            uuids.add(entity.getUUID());
        }

        if (uuids.isEmpty()) {
            source.sendFailure(Component.literal("No entities with GOAP agents found."));
            return 0;
        }

        GOAPDebugTracker.INSTANCE.track(player.getUUID(), uuids);
        var count = uuids.size();
        source.sendSuccess(() -> Component.literal("Now tracking %d GOAP agent(s).".formatted(count)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeUntrack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        GOAPDebugTracker.INSTANCE.untrack(player.getUUID());
        BLib.MOD.networking().sendToClient(player, new S2CGOAPDebugPayload(List.of(), 0, true, 0));
        source.sendSuccess(() -> Component.literal("Stopped tracking all GOAP agents."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeNext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var newIndex = GOAPDebugTracker.INSTANCE.next(player.getUUID());

        if (newIndex == -1) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        var displayIndex = newIndex + 1;
        source.sendSuccess(() -> Component.literal("Switched to tracked agent #%d.".formatted(displayIndex)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executePrevious(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var newIndex = GOAPDebugTracker.INSTANCE.previous(player.getUUID());

        if (newIndex == -1) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        var displayIndex = newIndex + 1;
        source.sendSuccess(() -> Component.literal("Switched to tracked agent #%d.".formatted(displayIndex)), false);
        return Command.SINGLE_SUCCESS;
    }

    // endregion

    // region worldstate

    private static int executeWorldStateNext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        if (!GOAPDebugTracker.INSTANCE.worldStateNext(player.getUUID())) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("World state: next page."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeWorldStatePrevious(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        if (!GOAPDebugTracker.INSTANCE.worldStatePrevious(player.getUUID())) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("World state: previous page."), false);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static int executeWorldStatePage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var page = IntegerArgumentType.getInteger(context, "page");

        var state = GOAPDebugTracker.INSTANCE.getTrackingState(player.getUUID());

        if (state == null) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        var server = source.getServer();
        var selectedIndex = state.selectedIndex();

        if (selectedIndex >= state.entityUuids().size()) {
            source.sendFailure(Component.literal("No selected agent."));
            return 0;
        }

        var entityUuid = state.entityUuids().get(selectedIndex);
        var totalPages = 1;

        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(entityUuid);

            if (entity instanceof LivingEntity livingEntity) {
                var goapUser = (GOAPUser<LivingEntity>) livingEntity;
                var graph = goapUser.blib$getGOAPGraphOrNull();

                if (graph != null) {
                    var sensorCount = graph.getSensorMap().size();
                    totalPages = Math.max(
                        1,
                        (sensorCount + GOAPDebugTracker.WORLD_STATE_PAGE_SIZE - 1) / GOAPDebugTracker.WORLD_STATE_PAGE_SIZE
                    );
                }

                break;
            }
        }

        if (page > totalPages) {
            var max = totalPages;
            source.sendFailure(Component.literal("Invalid page. Max page: %d.".formatted(max)));
            return 0;
        }

        GOAPDebugTracker.INSTANCE.worldStatePage(player.getUUID(), page - 1);
        source.sendSuccess(() -> Component.literal("World state: page %d.".formatted(page)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeWorldStateAuto(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        if (!GOAPDebugTracker.INSTANCE.worldStateAuto(player.getUUID())) {
            source.sendFailure(Component.literal("Not tracking any GOAP agents."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("World state: auto page cycling enabled."), false);
        return Command.SINGLE_SUCCESS;
    }

    // endregion

    // region inspect

    @SuppressWarnings("unchecked")
    private static int executeInspect(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var entities = EntityArgument.getEntities(context, "targets");
        var count = 0;

        for (var entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            var goapUser = (GOAPUser<LivingEntity>) livingEntity;
            var graph = goapUser.blib$getGOAPGraphOrNull();

            if (graph == null) {
                continue;
            }

            var livingEntityAgent = goapUser.blib$getGOAPAgentOrNull();

            if (livingEntityAgent == null) {
                continue;
            }

            inspectEntity(source, entity, livingEntityAgent);
            count++;
        }

        if (count == 0) {
            source.sendFailure(Component.literal("No entities with GOAP agents found."));
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void inspectEntity(
        CommandSourceStack source,
        Entity entity,
        LivingEntityAgent<LivingEntity> livingEntityAgent
    ) {
        var agent = livingEntityAgent.getBackingAgent();
        var executor = agent.getPlanExecutor();
        var pos = entity.blockPosition();

        source.sendSuccess(
            () -> Component.literal(
                "=== GOAP Agent: %s [%s] at %d, %d, %d ===".formatted(
                    entity.getName().getString(),
                    entity.getUUID(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
                )
            ),
            false
        );
        source.sendSuccess(
            () -> Component.literal("Tick: %d | Has plan: %s".formatted(agent.getTick(), agent.hasPlan())),
            false
        );

        if (executor instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
            source.sendSuccess(
                () -> Component.literal(
                    "Active plans: %d / %d".formatted(concurrent.getActivePlanCount(), concurrent.getMaxConcurrentPlans())
                ),
                false
            );

            var plans = concurrent.getActivePlans();

            for (var i = 0; i < plans.size(); i++) {
                var plan = plans.get(i);
                var planIndex = i;

                source.sendSuccess(
                    () -> Component.literal(
                        "  Plan #%d: [%s] Goal: '%s' (cost: %.1f)".formatted(
                            planIndex,
                            plan.getPlanState(),
                            plan.getGoal().getName(),
                            plan.getInitialCost()
                        )
                    ),
                    false
                );

                formatPlanActions(plan, source);
            }
        }

        if (!agent.hasPlan()) {
            source.sendSuccess(() -> Component.literal("No active plans."), false);
        }
    }

    private static void formatPlanActions(Plan<LivingEntity> plan, CommandSourceStack source) {
        var actions = plan.getActions();
        var currentActionIndex = ((MixinPlan_Accessor) (Object) plan).getCurrentActionIndex();

        for (var j = 0; j < actions.size(); j++) {
            var action = actions.get(j);
            var marker = j == currentActionIndex ? " >> " : "    ";
            var actionIndex = j;

            source.sendSuccess(
                () -> Component.literal(
                    "%s%d. %s".formatted(marker, actionIndex + 1, action.getName())
                ),
                false
            );
        }
    }

    // endregion

    // region snapshot

    @SuppressWarnings("unchecked")
    private static int executeSnapshot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var entities = EntityArgument.getEntities(context, "targets");

        var timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        var snapshotDir = Path.of("blib_goap_snapshots", "snapshot_" + timestamp);
        var count = 0;

        for (var entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            var goapUser = (GOAPUser<LivingEntity>) livingEntity;
            var graph = goapUser.blib$getGOAPGraphOrNull();

            if (graph == null) {
                continue;
            }

            var livingEntityAgent = goapUser.blib$getGOAPAgentOrNull();

            if (livingEntityAgent == null) {
                continue;
            }

            var agent = livingEntityAgent.getBackingAgent();

            var fileName = "%s_%s.txt".formatted(
                entity.getName().getString().replaceAll("[^a-zA-Z0-9_-]", "_"),
                entity.getUUID()
            );

            try {
                Files.createDirectories(snapshotDir);

                try (var writer = new PrintWriter(Files.newBufferedWriter(snapshotDir.resolve(fileName)))) {
                    writeSnapshot(writer, entity, agent, graph);
                }

                count++;
            } catch (IOException e) {
                source.sendFailure(
                    Component.literal("Failed to write snapshot for '%s': %s".formatted(entity.getName().getString(), e.getMessage()))
                );
            }
        }

        if (count == 0) {
            source.sendFailure(Component.literal("No entities with GOAP agents found."));
            return 0;
        }

        var totalCount = count;
        source.sendSuccess(
            () -> Component.literal("Wrote %d GOAP snapshot(s) to %s".formatted(totalCount, snapshotDir)),
            false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static void writeSnapshot(
        PrintWriter w,
        Entity entity,
        Agent<LivingEntity> agent,
        Graph<LivingEntity> graph
    ) {
        var pos = entity.blockPosition();

        w.println("========================================");
        w.println("GOAP Agent Snapshot: " + entity.getName().getString());
        w.println("UUID: " + entity.getUUID());
        w.println("Position: %d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ()));
        w.println("Timestamp: " + LocalDateTime.now());
        w.println("Agent Tick: " + agent.getTick());
        w.println("Has Plan: " + agent.hasPlan());
        w.println("========================================");

        writePlans(w, agent);
        writeGraph(w, graph);
    }

    private static void writePlans(PrintWriter w, Agent<LivingEntity> agent) {
        var executor = agent.getPlanExecutor();

        w.println();
        w.println("--- Active Plans ---");

        if (executor instanceof ConcurrentPlanExecutor<LivingEntity> concurrent) {
            w.println("Max Concurrent Plans: " + concurrent.getMaxConcurrentPlans());
            w.println("Active Plan Count: " + concurrent.getActivePlanCount());

            var plans = concurrent.getActivePlans();

            for (int i = 0; i < plans.size(); i++) {
                writePlan(w, plans.get(i), i);
            }

            if (plans.isEmpty()) {
                w.println("(none)");
            }
        } else {
            w.println("Executor type: " + executor.getClass().getSimpleName());
        }
    }

    private static void writePlan(PrintWriter w, Plan<LivingEntity> plan, int index) {
        w.println();
        w.println("  Plan #" + index + ":");
        w.println("    Goal: " + plan.getGoal().getName());
        w.println("    State: " + plan.getPlanState());
        w.println("    Initial Cost: " + plan.getInitialCost());
        w.println("    Plan Tick: " + plan.getTick());
        var currentActionIndex = ((MixinPlan_Accessor) (Object) plan).getCurrentActionIndex();
        w.println("    Current Action Index: " + currentActionIndex);

        writeGoalDetails(w, plan.getGoal(), "    ");

        var actions = plan.getActions();
        w.println("    Actions (" + actions.size() + "):");

        for (int j = 0; j < actions.size(); j++) {
            var action = actions.get(j);
            var marker = j == currentActionIndex ? " >> " : "    ";
            w.println("      " + marker + (j + 1) + ". " + action.getName());
            writeActionDetails(w, action, "            ");
        }
    }

    // endregion

    // region shared formatting

    private static void writeGoalDetails(PrintWriter w, Goal goal, String indent) {
        var preconditions = goal.getPreconditions().getConditions();
        var desired = goal.getDesiredConditions().getConditions();

        if (!preconditions.isEmpty()) {
            w.println(indent + "Goal Preconditions:");
            for (var condition : preconditions) {
                w.println(indent + "  - " + condition);
            }
        }

        if (!desired.isEmpty()) {
            w.println(indent + "Desired Conditions:");
            for (var condition : desired) {
                w.println(indent + "  - " + condition);
            }
        }
    }

    private static void writeActionDetails(PrintWriter w, Action<?> action, String indent) {
        var preconditions = action.getPreconditionContainer().getConditions();
        var effects = action.getEffectContainer().getEffects();

        if (!preconditions.isEmpty()) {
            w.println(indent + "Preconditions:");
            for (var condition : preconditions) {
                w.println(indent + "  - " + condition);
            }
        }

        if (!effects.isEmpty()) {
            w.println(indent + "Effects:");
            for (var effect : effects) {
                w.println(indent + "  - " + effect);
            }
        }

        if (action instanceof BLibAction<?> blibAction) {
            var masks = blibAction.getMasks();
            if (!masks.isEmpty()) {
                w.println(indent + "Masks: " + masks.stream().map(m -> m.identifier()).toList());
            }
        }
    }

    private static void writeGraph(PrintWriter w, Graph<LivingEntity> graph) {
        w.println();
        w.println("--- Available Goals ---");

        var goals = graph.getAvailableGoals();
        if (goals.isEmpty()) {
            w.println("(none)");
        }
        for (var goal : goals) {
            w.println("  " + goal.getName());
            writeGoalDetails(w, goal, "    ");
        }

        w.println();
        w.println("--- Available Actions ---");

        var actions = graph.getAvailableActions();
        if (actions.isEmpty()) {
            w.println("(none)");
        }
        for (var action : actions) {
            w.println("  " + action.getName());
            writeActionDetails(w, action, "    ");
        }

        w.println();
        w.println("--- Sensors ---");

        var sensors = graph.getSensorMap();
        if (sensors.isEmpty()) {
            w.println("(none)");
        }
        for (var entry : sensors.entrySet()) {
            w.println("  " + entry.getKey().id() + " -> " + entry.getValue().getClass().getSimpleName());
        }
    }

    // endregion
}
