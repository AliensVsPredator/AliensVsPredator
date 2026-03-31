package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.common.territory.BLibTerritoryManager;

@ApiStatus.Internal
public final class BLibTerritoryCommands {

    private BLibTerritoryCommands() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("territory")
            .then(buildClaim())
            .then(buildUnclaim())
            .then(buildInfo())
            .then(buildList())
            .then(buildContested());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildClaim() {
        return Commands.literal("claim")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .executes(BLibTerritoryCommands::executeClaim)
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildUnclaim() {
        return Commands.literal("unclaim")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .executes(BLibTerritoryCommands::executeUnclaim)
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildInfo() {
        return Commands.literal("info")
            .executes(BLibTerritoryCommands::executeInfo);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildList() {
        return Commands.literal("list")
            .then(
                Commands.argument("faction_id", ResourceLocationArgument.id())
                    .suggests(BLibCommandSuggestions.FACTION_IDS)
                    .executes(BLibTerritoryCommands::executeList)
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContested() {
        return Commands.literal("contested")
            .executes(BLibTerritoryCommands::executeContested);
    }

    private static int executeClaim(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");
        var level = source.getLevel();
        var pos = new ChunkPos(net.minecraft.core.BlockPos.containing(source.getPosition()));
        var claimant = Claimant.faction(factionId);

        if (!BLibTerritoryManager.INSTANCE.addClaim(level, pos, claimant)) {
            source.sendFailure(Component.literal("Chunk [%d, %d] is already claimed by faction '%s'.".formatted(pos.x, pos.z, factionId)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Claimed chunk [%d, %d] for faction '%s'.".formatted(pos.x, pos.z, factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeUnclaim(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");
        var level = source.getLevel();
        var pos = new ChunkPos(net.minecraft.core.BlockPos.containing(source.getPosition()));
        var claimant = Claimant.faction(factionId);

        if (!BLibTerritoryManager.INSTANCE.removeClaim(level, pos, claimant)) {
            source.sendFailure(Component.literal("No claim by faction '%s' on chunk [%d, %d].".formatted(factionId, pos.x, pos.z)));
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Removed claim on chunk [%d, %d] for faction '%s'.".formatted(pos.x, pos.z, factionId)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeInfo(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var level = source.getLevel();
        var pos = new ChunkPos(net.minecraft.core.BlockPos.containing(source.getPosition()));
        var claimants = BLibTerritoryManager.INSTANCE.getClaimants(level, pos);

        if (claimants.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Chunk [%d, %d] has no claims.".formatted(pos.x, pos.z)), false);
            return 0;
        }

        var contested = BLibTerritoryManager.INSTANCE.isContested(level, pos);

        source.sendSuccess(
            () -> Component.literal(
                "Chunk [%d, %d] — %d claimant(s)%s:".formatted(
                    pos.x,
                    pos.z,
                    claimants.size(),
                    contested ? " (CONTESTED)" : ""
                )
            ),
            false
        );

        for (var claimant : claimants) {
            source.sendSuccess(
                () -> Component.literal("  - %s".formatted(formatClaimant(claimant))),
                false
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var factionId = ResourceLocationArgument.getId(context, "faction_id");
        var level = source.getLevel();
        var claimant = Claimant.faction(factionId);
        var chunks = BLibTerritoryManager.INSTANCE.getChunks(level, claimant);

        if (chunks.isEmpty()) {
            source.sendSuccess(
                () -> Component.literal("Faction '%s' has no claimed chunks in this dimension.".formatted(factionId)),
                false
            );
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("Faction '%s' claims %d chunk(s):".formatted(factionId, chunks.size())),
            false
        );

        for (var chunk : chunks) {
            source.sendSuccess(
                () -> Component.literal("  - [%d, %d]".formatted(chunk.x, chunk.z)),
                false
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int executeContested(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var level = source.getLevel();
        var contested = BLibTerritoryManager.INSTANCE.getAllContestedChunks(level);

        if (contested.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No contested chunks in this dimension."), false);
            return 0;
        }

        source.sendSuccess(
            () -> Component.literal("%d contested chunk(s):".formatted(contested.size())),
            false
        );

        for (var chunk : contested) {
            source.sendSuccess(
                () -> Component.literal("  - [%d, %d]".formatted(chunk.x, chunk.z)),
                false
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private static String formatClaimant(Claimant claimant) {
        return switch (claimant) {
            case Claimant.EntityClaimant(var entityId) -> "entity:%s".formatted(entityId);
            case Claimant.FactionClaimant(var factionId) -> "faction:%s".formatted(factionId);
        };
    }
}
