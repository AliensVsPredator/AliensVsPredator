package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibGizmoInput;
import com.blib.api.client.render.v1.item.BLibGizmoMode;
import com.blib.api.client.render.v1.item.BLibGizmoState;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.api.client.render.v1.item.BLibItemTransformOverrides;

/**
 * Debug-only command that lets you live-tweak BLib item transforms in-game. Targets any item whose renderer uses
 * {@link com.blib.api.client.render.v1.item.BLibTunableItemTransforms} — overrides take effect on the next render.
 * <p>
 * Subcommands (all under {@code /blib transform-tune}):
 * <ul>
 * <li>{@code set <item> <mode> <context> <field> <value>} — set a single field to an absolute value.</li>
 * <li>{@code nudge <item> <mode> <context> <field> <delta>} — add delta to the current effective value
 * (override-or-base), useful for fine adjustments.</li>
 * <li>{@code dump <item>} — print the current overrides as Java code, ready to paste back into your renderer's
 * {@link com.blib.api.client.render.v1.item.BLibItemTransforms} constants.</li>
 * <li>{@code reset <item>} — clear all overrides for an item.</li>
 * <li>{@code reset <item> <mode>} — clear overrides for one mode of an item.</li>
 * </ul>
 * <p>
 * Field names: {@code tx ty tz rx ry rz scale}. Modes: {@code idle blocking}. Context names use the lower snake_case
 * form of {@link ItemDisplayContext} ({@code first_person_right_hand}, {@code gui}, etc.).
 */
@ApiStatus.Internal
public final class BLibTransformTuneCommand {

    private BLibTransformTuneCommand() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("transform-tune")
            .then(buildSetOrNudge("set", false))
            .then(buildSetOrNudge("nudge", true))
            .then(buildDump())
            .then(buildReset())
            .then(buildDebug());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDebug() {
        return Commands.literal("debug")
            .then(Commands.literal("pivot").executes(BLibTransformTuneCommand::executeTogglePivotDebug))
            .then(
                Commands.literal("gizmo")
                    .then(Commands.literal("translate").executes(ctx -> executeSetGizmoMode(ctx, BLibGizmoMode.TRANSLATE)))
                    .then(Commands.literal("rotate").executes(ctx -> executeSetGizmoMode(ctx, BLibGizmoMode.ROTATE)))
                    .then(Commands.literal("scale").executes(ctx -> executeSetGizmoMode(ctx, BLibGizmoMode.SCALE)))
                    .then(Commands.literal("off").executes(ctx -> executeSetGizmoMode(ctx, BLibGizmoMode.OFF)))
            )
            .then(Commands.literal("trace").executes(BLibTransformTuneCommand::executeToggleGizmoTrace))
            .then(Commands.literal("blocking").executes(BLibTransformTuneCommand::executeToggleForceBlocking));
    }

    private static int executeToggleForceBlocking(CommandContext<CommandSourceStack> ctx) {
        var newState = !BLibItemTransformOverrides.isForceBlockingEnabled();
        BLibItemTransformOverrides.setForceBlockingEnabled(newState);
        ctx.getSource()
            .sendSuccess(
                () -> Component.literal(
                    "Force-blocking pose: " + (newState ? "ON (renderer treats item as in-use; tunes BLOCKING slot)" : "OFF")
                ),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeToggleGizmoTrace(CommandContext<CommandSourceStack> ctx) {
        var newState = !BLibGizmoInput.isTraceEnabled();
        BLibGizmoInput.setTraceEnabled(newState);
        ctx.getSource()
            .sendSuccess(
                () -> Component.literal(
                    "Gizmo trace logging: " + (newState ? "ON (check logs/latest.log)" : "OFF")
                ),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeTogglePivotDebug(CommandContext<CommandSourceStack> ctx) {
        var newState = !BLibItemTransformOverrides.isPivotVisualizationEnabled();
        BLibItemTransformOverrides.setPivotVisualizationEnabled(newState);
        ctx.getSource()
            .sendSuccess(
                () -> Component.literal(
                    "Pivot visualization: " + (newState ? "ON" : "OFF")
                ),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSetGizmoMode(CommandContext<CommandSourceStack> ctx, BLibGizmoMode mode) {
        BLibGizmoState.setMode(mode);

        var label = switch (mode) {
            case OFF -> "OFF";
            case TRANSLATE -> "TRANSLATE (drag arrows to move; chat must be open for cursor)";
            case ROTATE -> "ROTATE (drag rings to rotate; chat must be open for cursor)";
            case SCALE -> "SCALE (drag the white handle: up = scale up, down = scale down; chat must be open for cursor)";
        };

        ctx.getSource().sendSuccess(() -> Component.literal("Gizmo: " + label), false);
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetOrNudge(String literal, boolean nudge) {
        var root = Commands.literal(literal);
        var itemArg = itemArg();

        for (var mode : BLibItemTransformMode.values()) {
            // Capture for lambda
            final var capturedMode = mode;
            var modeNode = Commands.literal(mode.name().toLowerCase(Locale.ROOT));

            for (var context : ItemDisplayContext.values()) {
                if (context == ItemDisplayContext.NONE)
                    continue;
                final var capturedContext = context;
                var contextNode = Commands.literal(context.name().toLowerCase(Locale.ROOT));

                for (var field : Field.values()) {
                    final var capturedField = field;
                    contextNode.then(
                        Commands.literal(field.name().toLowerCase(Locale.ROOT))
                            .then(
                                Commands.argument(nudge ? "delta" : "value", FloatArgumentType.floatArg())
                                    .executes(
                                        ctx -> executeSetOrNudge(
                                            ctx,
                                            capturedMode,
                                            capturedContext,
                                            capturedField,
                                            FloatArgumentType.getFloat(ctx, nudge ? "delta" : "value"),
                                            nudge
                                        )
                                    )
                            )
                    );
                }

                modeNode.then(contextNode);
            }

            itemArg.then(modeNode);
        }

        return root.then(itemArg);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDump() {
        return Commands.literal("dump").then(itemArg().executes(BLibTransformTuneCommand::executeDump));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildReset() {
        var itemArg = itemArg();

        for (var mode : BLibItemTransformMode.values()) {
            final var capturedMode = mode;
            itemArg.then(
                Commands.literal(mode.name().toLowerCase(Locale.ROOT))
                    .executes(ctx -> executeReset(ctx, capturedMode))
            );
        }

        itemArg.executes(ctx -> executeReset(ctx, null));
        return Commands.literal("reset").then(itemArg);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> itemArg() {
        return Commands.argument("item", ResourceLocationArgument.id())
            .suggests(BLibTransformTuneCommand::suggestItems);
    }

    private static int executeSetOrNudge(
        CommandContext<CommandSourceStack> ctx,
        BLibItemTransformMode mode,
        ItemDisplayContext context,
        Field field,
        float value,
        boolean nudge
    ) {
        var itemId = ResourceLocationArgument.getId(ctx, "item");
        var current = BLibItemTransformOverrides.getEffective(itemId, mode, context);
        var updated = field.apply(current, value, nudge);
        BLibItemTransformOverrides.set(itemId, mode, context, updated);

        var label = nudge ? "Nudge" : "Set";
        var newValue = field.read(updated);
        ctx.getSource()
            .sendSuccess(
                () -> Component.literal(
                    "%s %s/%s/%s.%s %s%s -> %s".formatted(
                        label,
                        itemId,
                        mode.name().toLowerCase(Locale.ROOT),
                        context.name().toLowerCase(Locale.ROOT),
                        field.name().toLowerCase(Locale.ROOT),
                        nudge ? "by " : "= ",
                        formatFloat(value),
                        formatFloat(newValue)
                    )
                ),
                false
            );
        return Command.SINGLE_SUCCESS;
    }

    private static int executeDump(CommandContext<CommandSourceStack> ctx) {
        var itemId = ResourceLocationArgument.getId(ctx, "item");
        var source = ctx.getSource();

        var idleEffective = collectEffectiveTransforms(itemId, BLibItemTransformMode.IDLE);
        var blockingEffective = collectEffectiveTransforms(itemId, BLibItemTransformMode.BLOCKING);
        var idleWallFixed = BLibItemTransformOverrides.getWallFixed(itemId, BLibItemTransformMode.IDLE);
        var blockingWallFixed = BLibItemTransformOverrides.getWallFixed(itemId, BLibItemTransformMode.BLOCKING);

        if (idleEffective.isEmpty() && blockingEffective.isEmpty() && idleWallFixed == null && blockingWallFixed == null) {
            source.sendSuccess(() -> Component.literal("(no transforms registered or overridden for " + itemId + ")"), false);
            return Command.SINGLE_SUCCESS;
        }

        var content = formatDumpFile(itemId, idleEffective, blockingEffective, idleWallFixed, blockingWallFixed);
        var dumpPath = resolveDumpPath(itemId);

        try {
            Files.createDirectories(dumpPath.getParent());
            Files.writeString(dumpPath, content);
        } catch (IOException e) {
            source.sendFailure(Component.literal("Failed to write dump: " + e.getMessage()));
            return 0;
        }

        var gameDir = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath();
        var displayPath = relativizeIfPossible(gameDir, dumpPath);
        source.sendSuccess(() -> Component.literal("Dumped " + itemId + " to " + displayPath), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Map<ItemDisplayContext, BLibTransform> collectEffectiveTransforms(ResourceLocation itemId, BLibItemTransformMode mode) {
        // Mode-specific lookup (no cascade): only emit contexts that are explicitly set in this mode's
        // override or base. The renderer's runtime cascade handles the rest, and we don't want the dumped
        // BLOCKING block to silently swallow idle's values — that would defeat the per-context override
        // semantics on paste-back.
        var result = new LinkedHashMap<ItemDisplayContext, BLibTransform>();
        for (var context : ItemDisplayContext.values()) {
            if (context == ItemDisplayContext.NONE)
                continue;
            var modeValue = BLibItemTransformOverrides.getModeValueOrNull(itemId, mode, context);
            if (modeValue == null)
                continue;
            if (modeValue.equals(BLibTransform.IDENTITY))
                continue;
            result.put(context, modeValue);
        }
        return result;
    }

    private static Path resolveDumpPath(ResourceLocation itemId) {
        var safeName = (itemId.getNamespace() + "__" + itemId.getPath()).replaceAll("[^A-Za-z0-9_]", "_");
        return Minecraft.getInstance().gameDirectory.toPath()
            .toAbsolutePath()
            .resolve("blib_transform_dumps")
            .resolve(safeName + ".java");
    }

    private static String relativizeIfPossible(Path base, Path target) {
        try {
            return base.relativize(target).toString();
        } catch (IllegalArgumentException e) {
            return target.toString();
        }
    }

    private static String formatDumpFile(
        ResourceLocation itemId,
        Map<ItemDisplayContext, BLibTransform> idle,
        Map<ItemDisplayContext, BLibTransform> blocking,
        @org.jetbrains.annotations.Nullable BLibTransform idleWallFixed,
        @org.jetbrains.annotations.Nullable BLibTransform blockingWallFixed
    ) {
        var timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        var sb = new StringBuilder();
        sb.append("// Generated by /blib transform-tune dump\n");
        sb.append("// Item: ").append(itemId).append('\n');
        sb.append("// Timestamp: ").append(timestamp).append('\n');
        sb.append("// Paste each block over the corresponding constant in your renderer.\n\n");

        if (!idle.isEmpty() || idleWallFixed != null) {
            sb.append("// IDLE\n");
            sb.append("BLibItemTransforms.builder()\n");
            for (var entry : idle.entrySet()) {
                sb.append("    ").append(formatJavaLine(entry.getKey(), entry.getValue())).append('\n');
            }
            if (idleWallFixed != null) {
                sb.append("    ").append(formatWallFixedJavaLine(idleWallFixed)).append('\n');
            }
            sb.append("    .build();\n");
        }

        if (!blocking.isEmpty() || blockingWallFixed != null) {
            if (!idle.isEmpty() || idleWallFixed != null)
                sb.append('\n');
            sb.append("// BLOCKING\n");
            sb.append("BLibItemTransforms.builder()\n");
            for (var entry : blocking.entrySet()) {
                sb.append("    ").append(formatJavaLine(entry.getKey(), entry.getValue())).append('\n');
            }
            if (blockingWallFixed != null) {
                sb.append("    ").append(formatWallFixedJavaLine(blockingWallFixed)).append('\n');
            }
            sb.append("    .build();\n");
        }

        return sb.toString();
    }

    private static String formatWallFixedJavaLine(BLibTransform t) {
        // Wall-fixed isn't context-keyed, so it gets its own builder method (`fixedWall`) rather than
        // routing through `builderMethodFor(ItemDisplayContext)`. Otherwise the formatting is identical
        // to the regular per-context line.
        var scaleX = t.scale().x;
        var scaleEqual = scaleX == t.scale().y && t.scale().y == t.scale().z;
        var scaleStr = scaleEqual
            ? formatFloat(scaleX) + "f"
            : "/* non-uniform " + formatFloat(t.scale().x) + "/" + formatFloat(t.scale().y) + "/" + formatFloat(t.scale().z) + " */ "
                + formatFloat(scaleX) + "f";

        var pivotX = t.pivot().x;
        var pivotY = t.pivot().y;
        var pivotZ = t.pivot().z;
        var hasPivot = pivotX != 0 || pivotY != 0 || pivotZ != 0;

        if (hasPivot) {
            return ".fixedWall(BLibTransform.of(%sf, %sf, %sf, %sf, %sf, %sf, %s, %sf, %sf, %sf))".formatted(
                formatFloat(t.translation().x),
                formatFloat(t.translation().y),
                formatFloat(t.translation().z),
                formatFloat(t.rotation().x),
                formatFloat(t.rotation().y),
                formatFloat(t.rotation().z),
                scaleStr,
                formatFloat(pivotX),
                formatFloat(pivotY),
                formatFloat(pivotZ)
            );
        }

        return ".fixedWall(BLibTransform.of(%sf, %sf, %sf, %sf, %sf, %sf, %s))".formatted(
            formatFloat(t.translation().x),
            formatFloat(t.translation().y),
            formatFloat(t.translation().z),
            formatFloat(t.rotation().x),
            formatFloat(t.rotation().y),
            formatFloat(t.rotation().z),
            scaleStr
        );
    }

    private static int executeReset(CommandContext<CommandSourceStack> ctx, BLibItemTransformMode mode) {
        var itemId = ResourceLocationArgument.getId(ctx, "item");
        if (mode == null) {
            BLibItemTransformOverrides.clearAll(itemId);
            ctx.getSource().sendSuccess(() -> Component.literal("Cleared all transform overrides for " + itemId), false);
        } else {
            BLibItemTransformOverrides.clear(itemId, mode);
            ctx.getSource()
                .sendSuccess(
                    () -> Component.literal(
                        "Cleared %s overrides for %s".formatted(mode.name().toLowerCase(Locale.ROOT), itemId)
                    ),
                    false
                );
        }
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(BLibItemTransformOverrides.tunableItemIds(), builder);
    }

    private static String formatFloat(float v) {
        var s = String.format(Locale.ROOT, "%.4f", v);
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "");
            if (s.endsWith("."))
                s = s + "0";
        }
        return s;
    }

    private static String formatJavaLine(ItemDisplayContext context, BLibTransform t) {
        var builderMethod = builderMethodFor(context);
        var scaleX = t.scale().x;
        var scaleEqual = scaleX == t.scale().y && t.scale().y == t.scale().z;
        var scaleStr = scaleEqual
            ? formatFloat(scaleX) + "f"
            : "/* non-uniform " + formatFloat(t.scale().x) + "/" + formatFloat(t.scale().y) + "/" + formatFloat(t.scale().z) + " */ "
                + formatFloat(scaleX) + "f";

        var pivotX = t.pivot().x;
        var pivotY = t.pivot().y;
        var pivotZ = t.pivot().z;
        var hasPivot = pivotX != 0 || pivotY != 0 || pivotZ != 0;

        if (hasPivot) {
            return ".%s(BLibTransform.of(%sf, %sf, %sf, %sf, %sf, %sf, %s, %sf, %sf, %sf))".formatted(
                builderMethod,
                formatFloat(t.translation().x),
                formatFloat(t.translation().y),
                formatFloat(t.translation().z),
                formatFloat(t.rotation().x),
                formatFloat(t.rotation().y),
                formatFloat(t.rotation().z),
                scaleStr,
                formatFloat(pivotX),
                formatFloat(pivotY),
                formatFloat(pivotZ)
            );
        }

        return ".%s(BLibTransform.of(%sf, %sf, %sf, %sf, %sf, %sf, %s))".formatted(
            builderMethod,
            formatFloat(t.translation().x),
            formatFloat(t.translation().y),
            formatFloat(t.translation().z),
            formatFloat(t.rotation().x),
            formatFloat(t.rotation().y),
            formatFloat(t.rotation().z),
            scaleStr
        );
    }

    private static String builderMethodFor(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> "gui";
            case GROUND -> "ground";
            case FIXED -> "fixed";
            case HEAD -> "head";
            case FIRST_PERSON_LEFT_HAND -> "firstPersonLeftHand";
            case FIRST_PERSON_RIGHT_HAND -> "firstPersonRightHand";
            case THIRD_PERSON_LEFT_HAND -> "thirdPersonLeftHand";
            case THIRD_PERSON_RIGHT_HAND -> "thirdPersonRightHand";
            case NONE -> "/* NONE — handle via .set(ItemDisplayContext.NONE, ...) */ ";
        };
    }

    enum Field {

        TX,
        TY,
        TZ,
        RX,
        RY,
        RZ,
        SCALE,
        PX,
        PY,
        PZ;

        BLibTransform apply(BLibTransform t, float v, boolean nudge) {
            float tx = t.translation().x, ty = t.translation().y, tz = t.translation().z;
            float rx = t.rotation().x, ry = t.rotation().y, rz = t.rotation().z;
            float sx = t.scale().x, sy = t.scale().y, sz = t.scale().z;
            float px = t.pivot().x, py = t.pivot().y, pz = t.pivot().z;

            switch (this) {
                case TX -> tx = nudge ? tx + v : v;
                case TY -> ty = nudge ? ty + v : v;
                case TZ -> tz = nudge ? tz + v : v;
                case RX -> rx = nudge ? rx + v : v;
                case RY -> ry = nudge ? ry + v : v;
                case RZ -> rz = nudge ? rz + v : v;
                case SCALE -> {
                    sx = nudge ? sx + v : v;
                    sy = nudge ? sy + v : v;
                    sz = nudge ? sz + v : v;
                }
                case PX -> px = nudge ? px + v : v;
                case PY -> py = nudge ? py + v : v;
                case PZ -> pz = nudge ? pz + v : v;
            }

            return new BLibTransform(
                new Vector3f(tx, ty, tz),
                new Vector3f(rx, ry, rz),
                new Vector3f(sx, sy, sz),
                new Vector3f(px, py, pz)
            );
        }

        float read(BLibTransform t) {
            return switch (this) {
                case TX -> t.translation().x;
                case TY -> t.translation().y;
                case TZ -> t.translation().z;
                case RX -> t.rotation().x;
                case RY -> t.rotation().y;
                case RZ -> t.rotation().z;
                case SCALE -> t.scale().x;
                case PX -> t.pivot().x;
                case PY -> t.pivot().y;
                case PZ -> t.pivot().z;
            };
        }
    }
}
