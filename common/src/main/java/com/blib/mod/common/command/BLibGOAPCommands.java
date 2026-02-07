package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.blib.api.common.goap.v1.GOAPUser;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
public final class BLibGOAPCommands {

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
            );
    }

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
                    var limit = (sensorCount + GOAPDebugTracker.WORLD_STATE_PAGE_SIZE - 1) / GOAPDebugTracker.WORLD_STATE_PAGE_SIZE;

                    totalPages = Math.max(1, limit);
                }

                break;
            }
        }

        if (page > totalPages) {
            source.sendFailure(Component.literal("Invalid page. Max page: %d.".formatted(totalPages)));

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
}
