package org.avp.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;

import org.avp.api.GameObject;
import org.avp.common.legacy.schematic.LegacySchematic;
import org.avp.common.legacy.schematic.LegacySchematics;

public class AVPLegacySchematicCommand {

    private static final List<GameObject<LegacySchematic>> LEGACY_SCHEMATICS = List.of(
        LegacySchematics.DERELICT_SCHEMATIC,
        LegacySchematics.DERELICT_OLD_SCHEMATIC,
        LegacySchematics.TEST_SCHEMATIC
    );

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        var generate = Commands.literal("generate");

        for (GameObject<LegacySchematic> legacySchematicGameObject : LEGACY_SCHEMATICS) {
            var id = legacySchematicGameObject.getResourceLocation().getPath();
            generate.then(
                Commands.literal(id)
                    .executes(ctx -> execute(legacySchematicGameObject, ctx))
            );
        }

        commandDispatcher.register(
            Commands.literal("schematic")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(generate)
        );
    }

    private static int execute(
        GameObject<LegacySchematic> legacySchematicGameObject,
        CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        var commandSourceStack = context.getSource();

        if (!commandSourceStack.isPlayer()) {
            context.getSource()
                .sendFailure(Component.literal("Command must be ran by a player!"));
            return 0;
        }

        var player = commandSourceStack.getPlayerOrException();
        var playerBlockPos = player.getOnPos();
        var serverLevel = commandSourceStack.getLevel();
        var schematicOptional = legacySchematicGameObject.getOptional();

        if (schematicOptional.isEmpty()) {
            context.getSource().sendFailure(Component.literal("Could not retrieve schematic file."));
            return 0;
        }

        var legacySchematic = schematicOptional.get();
        legacySchematic.generateInLevel(serverLevel, playerBlockPos, true);
        context.getSource().sendSuccess(() -> Component.literal("Successfully generated."), false);
        return 1;
    }

    private AVPLegacySchematicCommand() {
        throw new UnsupportedOperationException();
    }
}
