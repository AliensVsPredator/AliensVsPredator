package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.internal.common.reputation.BLibReputationManager;

@ApiStatus.Internal
public final class BLibReputationCommands {

    private BLibReputationCommands() {
        throw new UnsupportedOperationException();
    }

    @FunctionalInterface
    private interface ReputationKeyResolver {

        ReputationKey resolve(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface LeafAttacher {

        void attach(
            RequiredArgumentBuilder<CommandSourceStack, ?> toArg,
            ReputationKeyResolver fromResolver,
            ReputationKeyResolver toResolver
        );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("reputation")
            .then(buildGetCommand())
            .then(buildSetCommand())
            .then(buildRemoveCommand());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildGetCommand() {
        var get = Commands.literal("get");
        addFromBranches(
            get,
            (toArg, fromResolver, toResolver) -> toArg
                .executes(ctx -> executeGet(ctx, fromResolver, toResolver))
        );
        return get;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetCommand() {
        var set = Commands.literal("set");
        addFromBranches(
            set,
            (toArg, fromResolver, toResolver) -> toArg.then(
                Commands.argument("value", IntegerArgumentType.integer())
                    .executes(ctx -> executeSet(ctx, fromResolver, toResolver))
            )
        );
        return set;
    }

    private static void addFromBranches(LiteralArgumentBuilder<CommandSourceStack> parent, LeafAttacher attacher) {
        parent.then(
            Commands.literal("faction")
                .then(
                    addToBranches(
                        Commands.argument("from_faction", ResourceLocationArgument.id())
                            .suggests(BLibCommandSuggestions.FACTION_IDS),
                        ctx -> ReputationKey.faction(ResourceLocationArgument.getId(ctx, "from_faction")),
                        attacher
                    )
                )
        );

        parent.then(
            Commands.literal("entity")
                .then(
                    addToBranches(
                        Commands.argument("from_entity", EntityArgument.entity()),
                        ctx -> ReputationKey.entity(EntityArgument.getEntity(ctx, "from_entity")),
                        attacher
                    )
                )
        );
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addToBranches(
        T fromArg,
        ReputationKeyResolver fromResolver,
        LeafAttacher attacher
    ) {
        var toFaction = Commands.argument("to_faction", ResourceLocationArgument.id())
            .suggests(BLibCommandSuggestions.FACTION_IDS);
        attacher.attach(toFaction, fromResolver, c -> ReputationKey.faction(ResourceLocationArgument.getId(c, "to_faction")));
        fromArg.then(Commands.literal("faction").then(toFaction));

        var toEntity = Commands.argument("to_entity", EntityArgument.entity());
        attacher.attach(toEntity, fromResolver, c -> ReputationKey.entity(EntityArgument.getEntity(c, "to_entity")));
        fromArg.then(Commands.literal("entity").then(toEntity));

        return fromArg;
    }

    private static int executeGet(
        CommandContext<CommandSourceStack> context,
        ReputationKeyResolver fromResolver,
        ReputationKeyResolver toResolver
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var from = fromResolver.resolve(context);
        var to = toResolver.resolve(context);
        var value = BLibReputationManager.INSTANCE.getReputation(from, to);

        source.sendSuccess(
            () -> Component.literal(
                "Reputation from %s to %s is %d.".formatted(formatReputationKey(from), formatReputationKey(to), value)
            ),
            false
        );
        return value;
    }

    private static int executeSet(
        CommandContext<CommandSourceStack> context,
        ReputationKeyResolver fromResolver,
        ReputationKeyResolver toResolver
    ) throws CommandSyntaxException {
        var source = context.getSource();
        var from = fromResolver.resolve(context);
        var to = toResolver.resolve(context);
        var value = IntegerArgumentType.getInteger(context, "value");

        BLibReputationManager.INSTANCE.setReputation(from, to, value);

        source.sendSuccess(
            () -> Component.literal(
                "Set reputation from %s to %s to %d.".formatted(formatReputationKey(from), formatReputationKey(to), value)
            ),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRemoveCommand() {
        return Commands.literal("remove")
            .then(
                Commands.literal("faction")
                    .then(
                        Commands.argument("faction_id", ResourceLocationArgument.id())
                            .suggests(BLibCommandSuggestions.FACTION_IDS)
                            .executes(BLibReputationCommands::executeRemoveFaction)
                    )
            )
            .then(
                Commands.literal("entity")
                    .then(
                        Commands.argument("target", EntityArgument.entity())
                            .executes(BLibReputationCommands::executeRemoveEntity)
                    )
            );
    }

    private static int executeRemoveFaction(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");
        var key = ReputationKey.faction(factionId);

        BLibReputationManager.INSTANCE.removeReputation(key);

        source.sendSuccess(
            () -> Component.literal("Removed all reputation for faction '%s'.".formatted(factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemoveEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var entity = EntityArgument.getEntity(context, "target");
        var uuid = entity.getUUID();
        var key = ReputationKey.entity(uuid);

        BLibReputationManager.INSTANCE.removeReputation(key);

        source.sendSuccess(
            () -> Component.literal("Removed all reputation for entity '%s'.".formatted(uuid)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static String formatReputationKey(ReputationKey key) {
        return switch (key) {
            case ReputationKey.Faction(var factionId) -> "faction '%s'".formatted(factionId);
            case ReputationKey.Entity(var uuid) -> "entity '%s'".formatted(uuid);
        };
    }
}
