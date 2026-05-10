package com.blib.engine.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.ProjectPickerScreen;

/**
 * {@code /blib engine} — opens the BLib Engine project picker. The picker is the entry point: it shows the user's
 * existing BLib projects (datapacks under {@code <world>/datapacks/} marked with {@code blib_project.json}) and gates
 * entry into {@link EngineWorkspaceScreen} on a successful Open. The workspace owns the freecam (orbit nav), entity
 * selection, server pause / resume, and gizmo state — but only after a project is selected.
 * <p>
 * Cancel from the picker drops the user back into the game without entering engine mode (the workspace's
 * {@code EngineTickControl.captureAndPause()} only fires on Open).
 */
@ApiStatus.Internal
public final class BLibEngineCommand {

    private BLibEngineCommand() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("engine").executes(BLibEngineCommand::openPicker);
    }

    private static int openPicker(CommandContext<CommandSourceStack> context) {
        // setScreen must run on the client thread; the command dispatcher fires on the integrated server thread.
        // The picker's onConfirmedOpen swaps to the workspace screen on a successful project open.
        Minecraft.getInstance()
            .execute(() -> Minecraft.getInstance().setScreen(new ProjectPickerScreen(BLibEngineCommand::openWorkspace)));
        return Command.SINGLE_SUCCESS;
    }

    private static void openWorkspace() {
        Minecraft.getInstance().setScreen(new EngineWorkspaceScreen());
    }
}
