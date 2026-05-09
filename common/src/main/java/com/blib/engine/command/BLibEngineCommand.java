package com.blib.engine.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.EngineWorkspaceScreen;

/**
 * {@code /blib engine} — opens the BLib Engine workspace screen. The workspace owns the freecam (orbit nav), entity
 * selection, server pause / resume, and gizmo state; there are no separate "fly" / "orbit" / toggle subcommands —
 * everything is driven from the editor UI now.
 */
@ApiStatus.Internal
public final class BLibEngineCommand {

    private BLibEngineCommand() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("engine").executes(BLibEngineCommand::openWorkspace);
    }

    private static int openWorkspace(CommandContext<CommandSourceStack> context) {
        // setScreen must run on the client thread; the command dispatcher fires on the integrated server thread.
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new EngineWorkspaceScreen()));
        return Command.SINGLE_SUCCESS;
    }
}
