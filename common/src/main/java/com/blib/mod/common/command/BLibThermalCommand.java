package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.shader.BLibThermalDebugState;
import com.blib.internal.client.shader.BLibThermalState;

/**
 * Debug-only command that toggles BLib's built-in thermal post-effect and switches the captured-MRT debug overlay. The
 * post-effects themselves are registered via the public post-effect framework
 * ({@code BLib.CLIENT.postEffects().register(...)}); their {@code BooleanSupplier enabledWhen} reads
 * {@link BLibThermalState#isActive()} / {@link BLibThermalDebugState#isActive()}, so the command's only job is to flip
 * those flags — no render-thread work, no GameRenderer.loadEffect calls.
 * <p>
 * Subcommands:
 * <ul>
 * <li>{@code /blib thermal} — toggle thermal vision on/off.</li>
 * <li>{@code /blib thermal debug} — turn the debug overlay off (mode 0).</li>
 * <li>{@code /blib thermal debug <mode>} — switch to one of the {@link BLibThermalDebugState} modes ({@code 0}=off,
 * {@code 1}=mask, {@code 2}=lightmap, {@code 3}=normal, {@code 4}=detail, {@code 5}=block-light, {@code 6}=sky,
 * {@code 7}=face, {@code 8}=specular roughness, {@code 9}=specular emission, {@code 10}=material id).</li>
 * </ul>
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
        return Commands.literal("thermal")
            .executes(BLibThermalCommand::toggle)
            .then(
                Commands.literal("debug")
                    .executes(ctx -> setDebugMode(ctx, BLibThermalDebugState.MODE_OFF))
                    .then(
                        Commands.argument("mode", IntegerArgumentType.integer(0, BLibThermalDebugState.MODE_MAX))
                            .executes(ctx -> setDebugMode(ctx, IntegerArgumentType.getInteger(ctx, "mode")))
                    )
            );
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        var newState = !BLibThermalState.isActive();
        BLibThermalState.setActive(newState);
        context.getSource().sendSuccess(() -> Component.literal("Thermal " + (newState ? "ON" : "OFF") + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setDebugMode(CommandContext<CommandSourceStack> context, int mode) {
        BLibThermalDebugState.setMode(mode);
        context.getSource().sendSuccess(() -> Component.literal("Thermal debug mode: " + describeMode(mode)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static String describeMode(int mode) {
        return switch (mode) {
            case BLibThermalDebugState.MODE_OFF -> "off";
            case BLibThermalDebugState.MODE_ENTITY_MASK -> "1 (entityMask)";
            case BLibThermalDebugState.MODE_ENTITY_LIGHTMAP -> "2 (entityLightmap)";
            case BLibThermalDebugState.MODE_ENTITY_NORMAL -> "3 (entityNormal)";
            case BLibThermalDebugState.MODE_THERMAL_DETAIL -> "4 (thermalData.r — detail)";
            case BLibThermalDebugState.MODE_THERMAL_BLOCK_LIGHT -> "5 (thermalData.g — block light)";
            case BLibThermalDebugState.MODE_THERMAL_SKY -> "6 (thermalData.b — sky light)";
            case BLibThermalDebugState.MODE_THERMAL_FACE -> "7 (thermalData.a — face light)";
            case BLibThermalDebugState.MODE_SPECULAR_ROUGHNESS -> "8 (entitySpecular.g — roughness)";
            case BLibThermalDebugState.MODE_SPECULAR_EMISSION -> "9 (entitySpecular.a — emission)";
            case BLibThermalDebugState.MODE_MATERIAL_ID -> "10 (entityMaterialId — JCL ipbr_id)";
            default -> Integer.toString(mode);
        };
    }
}
