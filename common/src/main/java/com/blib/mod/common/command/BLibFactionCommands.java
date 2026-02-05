package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

import com.blib.api.common.faction.v1.FactionMember;
import com.blib.internal.common.faction.BLibFactionManager;

@ApiStatus.Internal
public final class BLibFactionCommands {

    private BLibFactionCommands() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("factions")
            .then(buildCreate())
            .then(buildRemove())
            .then(buildAddMember())
            .then(buildRemoveMember())
            .then(buildList());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCreate() {
        return Commands.literal("create")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .then(
                        Commands.argument("faction_type", ResourceLocationArgument.id())
                            .suggests(BLibCommandSuggestions.FACTION_TYPE_IDS)
                            .executes(BLibFactionCommands::executeCreate)
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRemove() {
        return Commands.literal("remove")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .executes(BLibFactionCommands::executeRemove)
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildAddMember() {
        return Commands.literal("add-member")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes(BLibFactionCommands::executeAddMemberEntity)
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRemoveMember() {
        return Commands.literal("remove-member")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes(BLibFactionCommands::executeRemoveMemberEntity)
                    )
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildList() {
        return Commands.literal("list")
            .executes(BLibFactionCommands::executeList);
    }

    private static int executeCreate(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");
        var typeId = ResourceLocationArgument.getId(context, "faction_type");

        if (BLibFactionManager.INSTANCE.exists(factionId)) {
            source.sendFailure(Component.literal("Faction '%s' already exists.".formatted(factionId)));
            return 0;
        }

        var result = BLibFactionManager.INSTANCE.getOrCreateByTypeId(factionId, typeId);

        if (result.isErr()) {
            source.sendFailure(Component.literal("Unknown faction type '%s'.".formatted(typeId)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Created faction '%s' with type '%s'.".formatted(factionId, typeId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");

        if (!BLibFactionManager.INSTANCE.remove(factionId)) {
            source.sendFailure(Component.literal("Faction '%s' not found.".formatted(factionId)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Removed faction '%s'.".formatted(factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAddMemberEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = EntityArgument.getEntity(context, "target");
        return addMember(context, entity.getUUID());
    }

    private static int addMember(CommandContext<CommandSourceStack> context, UUID uuid) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");

        if (!BLibFactionManager.INSTANCE.exists(factionId)) {
            source.sendFailure(Component.literal("Faction '%s' not found.".formatted(factionId)));
            return 0;
        }

        var relationships = BLibFactionManager.INSTANCE.getRelationships(factionId);
        var member = new FactionMember.Entity(uuid);

        if (!relationships.addMember(member)) {
            source.sendFailure(Component.literal("Entity '%s' is already a member of faction '%s'.".formatted(uuid, factionId)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Added entity '%s' to faction '%s'.".formatted(uuid, factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemoveMemberEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var entity = EntityArgument.getEntity(context, "target");
        return removeMember(context, entity.getUUID());
    }

    private static int removeMember(CommandContext<CommandSourceStack> context, UUID uuid) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");

        if (!BLibFactionManager.INSTANCE.exists(factionId)) {
            source.sendFailure(Component.literal("Faction '%s' not found.".formatted(factionId)));
            return 0;
        }

        var relationships = BLibFactionManager.INSTANCE.getRelationships(factionId);
        var member = new FactionMember.Entity(uuid);

        if (!relationships.removeMember(member)) {
            source.sendFailure(Component.literal("Entity '%s' is not a member of faction '%s'.".formatted(uuid, factionId)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Removed entity '%s' from faction '%s'.".formatted(uuid, factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var allIds = BLibFactionManager.INSTANCE.getAllIds();

        if (allIds.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No factions exist."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Factions (%d):".formatted(allIds.size())), false);

        for (var id : allIds) {
            source.sendSuccess(() -> Component.literal(" - %s".formatted(id)), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}
