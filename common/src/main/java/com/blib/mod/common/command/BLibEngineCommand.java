package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.engine.EngineMode;
import com.blib.internal.client.engine.NavigationMode;

/**
 * Debug-only command that toggles the in-game engine mode editor and switches navigation schemes. Engine mode detaches
 * the camera, suppresses player input, and (in subsequent phases) lets you click-select objects and manipulate them via
 * 3D gizmos.
 * <p>
 * Subcommands:
 * <ul>
 * <li>{@code /blib engine} — toggle engine mode on/off.</li>
 * <li>{@code /blib engine fly} — switch to spectator-style WASD + mouse-look navigation (default).</li>
 * <li>{@code /blib engine orbit} — switch to DCC-style mouse-driven orbit / pan / zoom navigation.</li>
 * </ul>
 * Registered alongside the dismemberment command, behind the same dev-environment gate, so this never reaches a shipped
 * build.
 */
@ApiStatus.Internal
public final class BLibEngineCommand {

    private BLibEngineCommand() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("engine")
            .executes(BLibEngineCommand::toggle)
            .then(Commands.literal("fly").executes(ctx -> setMode(ctx, NavigationMode.FLY)))
            .then(Commands.literal("orbit").executes(ctx -> setMode(ctx, NavigationMode.ORBIT)));
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        EngineMode.get().toggle();
        var active = EngineMode.get().isActive();
        context.getSource()
            .sendSuccess(() -> Component.literal("Engine mode " + (active ? "ON" : "OFF") + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMode(CommandContext<CommandSourceStack> context, NavigationMode mode) {
        var session = EngineMode.get().session();

        if (session == null) {
            context.getSource().sendFailure(Component.literal("Engine mode is not active."));
            return 0;
        }

        session.setMode(mode);
        context.getSource()
            .sendSuccess(() -> Component.literal("Engine navigation: " + mode.name().toLowerCase() + "."), false);
        return Command.SINGLE_SUCCESS;
    }
}
