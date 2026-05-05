package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.shader.BLibThermalState;

/**
 * Debug-only command that toggles BLib's built-in thermal post-effect. The post-effect itself is registered via the
 * public post-effect framework ({@code BLib.CLIENT.postEffects().register(...)}); its
 * {@code BooleanSupplier enabledWhen} reads {@link BLibThermalState#isActive()}, so the command's only job is to flip
 * that flag — no render-thread work, no GameRenderer.loadEffect calls.
 * <p>
 * Registered alongside the engine command behind dev-environment + client-distribution gates, so this never reaches a
 * shipped build and never link-fails on a dedicated server.
 */
@ApiStatus.Internal
public final class BLibThermalCommand {

    private BLibThermalCommand() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("thermal").executes(BLibThermalCommand::toggle);
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        var newState = !BLibThermalState.isActive();
        BLibThermalState.setActive(newState);
        context.getSource().sendSuccess(() -> Component.literal("Thermal " + (newState ? "ON" : "OFF") + "."), false);
        return Command.SINGLE_SUCCESS;
    }
}
