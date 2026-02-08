package com.blib.mod.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.property.v1.BLibPropertyContainer;
import com.blib.api.common.property.v1.BLibPropertyContainerType;
import com.blib.api.common.property.v1.BLibPropertyKey;
import com.blib.api.common.property.v1.BLibPropertySchema;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;

@ApiStatus.Internal
public final class BLibPropertyCommands {

    private BLibPropertyCommands() {
        throw new UnsupportedOperationException();
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("property")
            .then(buildListContainers())
            .then(buildContainerArg());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildListContainers() {
        return Commands.literal("list")
            .executes(BLibPropertyCommands::executeListContainers);
    }

    private static int executeListContainers(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var registry = BLibBuiltInRegistries.PROPERTY_CONTAINER_TYPES;
        var ids = registry.keySet();

        if (ids.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No property containers registered."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Property containers (%d):".formatted(ids.size())), false);

        for (var id : ids) {
            source.sendSuccess(() -> Component.literal(" - %s".formatted(id)), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, ?> buildContainerArg() {
        return Commands.argument("container", ResourceLocationArgument.id())
            .suggests(
                (context, builder) -> SharedSuggestionProvider.suggestResource(
                    BLibBuiltInRegistries.PROPERTY_CONTAINER_TYPES.keySet(),
                    builder
                )
            )
            .then(buildContainerListProperties())
            .then(buildContainerGet())
            .then(buildContainerSet())
            .then(buildContainerResetAll())
            .then(buildContainerResetProperty());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContainerListProperties() {
        return Commands.literal("list")
            .executes(BLibPropertyCommands::executeListProperties);
    }

    private static int executeListProperties(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var type = resolveContainerTypeOrNull(context);

        if (type == null) {
            source.sendFailure(
                Component.literal(
                    "Unknown property container '%s'.".formatted(
                        ResourceLocationArgument.getId(context, "container")
                    )
                )
            );
            return 0;
        }

        var container = type.container();
        var schema = container.getSchema();
        var properties = getPropertyLines(schema);

        if (properties.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No properties in this container."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Properties (%d):".formatted(properties.size())), false);

        for (var property : properties) {
            var path = property.leaf().path();
            var currentValue = getCurrentValueString(container, property);
            source.sendSuccess(() -> Component.literal(" - %s = %s".formatted(path, currentValue)), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContainerGet() {
        return Commands.literal("get")
            .then(
                Commands.argument("property_path", StringArgumentType.string())
                    .suggests(
                        (context, builder) -> {
                            var type = resolveContainerTypeOrNull(context);

                            if (type == null) {
                                return builder.buildFuture();
                            }

                            var paths = getPropertyLines(type.container().getSchema()).stream()
                                .map(p -> p.leaf().path());
                            return SharedSuggestionProvider.suggest(paths, builder);
                        }
                    )
                    .executes(BLibPropertyCommands::executeGet)
            );
    }

    private static int executeGet(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var type = resolveContainerTypeOrNull(context);

        if (type == null) {
            source.sendFailure(
                Component.literal(
                    "Unknown property container '%s'.".formatted(
                        ResourceLocationArgument.getId(context, "container")
                    )
                )
            );
            return 0;
        }

        var propertyPath = StringArgumentType.getString(context, "property_path");
        var container = type.container();
        var property = container.getSchema().getPropertyByPathOrNull(propertyPath);

        if (property == null) {
            source.sendFailure(Component.literal("Unknown property '%s'.".formatted(propertyPath)));
            return 0;
        }

        var currentValue = getCurrentValueString(container, property);
        source.sendSuccess(() -> Component.literal("%s = %s".formatted(propertyPath, currentValue)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContainerSet() {
        return Commands.literal("set")
            .then(
                Commands.argument("property_path", StringArgumentType.string())
                    .suggests(
                        (context, builder) -> {
                            var type = resolveContainerTypeOrNull(context);

                            if (type == null) {
                                return builder.buildFuture();
                            }

                            var paths = getPropertyLines(type.container().getSchema()).stream()
                                .map(p -> p.leaf().path());
                            return SharedSuggestionProvider.suggest(paths, builder);
                        }
                    )
                    .then(
                        Commands.argument("value", StringArgumentType.greedyString())
                            .executes(BLibPropertyCommands::executeSet)
                    )
            );
    }

    @SuppressWarnings("unchecked")
    private static int executeSet(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var type = resolveContainerTypeOrNull(context);

        if (type == null) {
            source.sendFailure(
                Component.literal(
                    "Unknown property container '%s'.".formatted(
                        ResourceLocationArgument.getId(context, "container")
                    )
                )
            );
            return 0;
        }

        var propertyPath = StringArgumentType.getString(context, "property_path");
        var rawValue = StringArgumentType.getString(context, "value");
        var container = type.container();
        var property = container.getSchema().getPropertyByPathOrNull(propertyPath);

        if (property == null) {
            source.sendFailure(Component.literal("Unknown property '%s'.".formatted(propertyPath)));
            return 0;
        }

        var leaf = property.leaf();
        var deserialized = leaf.serializer().deserializeOrNull(rawValue);

        if (deserialized == null) {
            source.sendFailure(
                Component.literal(
                    "Failed to deserialize value '%s' for property '%s'.".formatted(
                        rawValue,
                        propertyPath
                    )
                )
            );
            return 0;
        }

        @SuppressWarnings("rawtypes")
        var castLeaf = (BLibPropertyKey.Leaf) leaf;
        container.set(castLeaf, deserialized);

        source.sendSuccess(() -> Component.literal("Set %s = %s".formatted(propertyPath, rawValue)), true);
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContainerResetAll() {
        return Commands.literal("reset")
            .executes(BLibPropertyCommands::executeResetAll);
    }

    private static int executeResetAll(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var type = resolveContainerTypeOrNull(context);

        if (type == null) {
            source.sendFailure(
                Component.literal(
                    "Unknown property container '%s'.".formatted(
                        ResourceLocationArgument.getId(context, "container")
                    )
                )
            );
            return 0;
        }

        var container = type.container();
        var properties = getPropertyLines(container.getSchema());

        for (var property : properties) {
            resetProperty(container, property);
        }

        source.sendSuccess(
            () -> Component.literal("Reset all properties (%d) to defaults.".formatted(properties.size())),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildContainerResetProperty() {
        return Commands.literal("reset-property")
            .then(
                Commands.argument("property_path", StringArgumentType.string())
                    .suggests(
                        (context, builder) -> {
                            var type = resolveContainerTypeOrNull(context);

                            if (type == null) {
                                return builder.buildFuture();
                            }

                            var paths = getPropertyLines(type.container().getSchema()).stream()
                                .map(p -> p.leaf().path());
                            return SharedSuggestionProvider.suggest(paths, builder);
                        }
                    )
                    .executes(BLibPropertyCommands::executeResetProperty)
            );
    }

    private static int executeResetProperty(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var type = resolveContainerTypeOrNull(context);

        if (type == null) {
            source.sendFailure(
                Component.literal(
                    "Unknown property container '%s'.".formatted(
                        ResourceLocationArgument.getId(context, "container")
                    )
                )
            );
            return 0;
        }

        var propertyPath = StringArgumentType.getString(context, "property_path");
        var container = type.container();
        var property = container.getSchema().getPropertyByPathOrNull(propertyPath);

        if (property == null) {
            source.sendFailure(Component.literal("Unknown property '%s'.".formatted(propertyPath)));
            return 0;
        }

        var oldValue = getCurrentValueString(container, property);
        var defaultValue = getDefaultValueString(property);
        resetProperty(container, property);

        source.sendSuccess(
            () -> Component.literal("Reset %s: %s -> %s".formatted(propertyPath, oldValue, defaultValue)),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static @Nullable BLibPropertyContainerType resolveContainerTypeOrNull(CommandContext<CommandSourceStack> context) {
        var location = ResourceLocationArgument.getId(context, "container");
        return BLibBuiltInRegistries.PROPERTY_CONTAINER_TYPES.get(location);
    }

    private static java.util.List<BLibPropertySchema.Line.Property<?>> getPropertyLines(BLibPropertySchema schema) {
        var result = new java.util.ArrayList<BLibPropertySchema.Line.Property<?>>();

        for (var line : schema.getLines()) {
            if (line instanceof BLibPropertySchema.Line.Property<?> property) {
                result.add(property);
            }
        }

        return result;
    }

    private static <T> String getCurrentValueString(BLibPropertyContainer container, BLibPropertySchema.Line.Property<T> property) {
        var leaf = property.leaf();
        var value = container.getOrNull(leaf);

        if (value == null) {
            return String.valueOf(property.defaultValue());
        }

        return leaf.serializer().serialize(value);
    }

    private static <T> String getDefaultValueString(BLibPropertySchema.Line.Property<T> property) {
        return property.leaf().serializer().serialize(property.defaultValue());
    }

    private static <T> void resetProperty(BLibPropertyContainer container, BLibPropertySchema.Line.Property<T> property) {
        container.set(property.leaf(), property.defaultValue());
    }
}
