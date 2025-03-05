package com.avp.common.command.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import com.avp.AVP;
import com.avp.common.config.ConfigProperties;
import com.avp.common.config.io.ConfigSaver;
import com.avp.common.config.property.ConfigPropertyRegistry;

public class ConfigSetCommand {

    private static final String COMMAND_NAME = "set";

    private static final String PROPERTY_ARGUMENT_NAME = "property";

    private static final String VALUE_ARGUMENT_NAME = "value";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .then(
                Commands.argument(PROPERTY_ARGUMENT_NAME, StringArgumentType.word())
                    .suggests(ConfigCommandUtil.PROPERTY_SUGGESTIONS)
                    .then(
                        Commands.argument(VALUE_ARGUMENT_NAME, StringArgumentType.string())
                            .executes(ConfigSetCommand::execute)
                    )
            );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        var property = StringArgumentType.getString(context, PROPERTY_ARGUMENT_NAME);
        var propertyKey = ConfigProperties.keyOrNull(property);

        if (propertyKey == null) {
            ConfigCommandUtil.sendPropertyDoesNotExistErrorMessage(context, property);

            // TODO: Error code here?
            return -1;
        }

        var deserializer = ConfigPropertyRegistry.instance().getDeserializerOrNull(propertyKey);

        if (deserializer == null) {
            ConfigCommandUtil.sendPropertyDoesNotExistErrorMessage(context, property);

            // TODO: Error code here?
            return -1;
        }

        var value = StringArgumentType.getString(context, VALUE_ARGUMENT_NAME);
        var deserializedValue = deserializer.deserialize(value);

        var hasSetProperty = false;

        for (var configEntry : AVP.CONFIGS.entrySet()) {
            var config = configEntry.getValue().config();
            var template = configEntry.getValue().template();

            var propertyValueOptional = config.properties().get(propertyKey);

            if (propertyValueOptional.isPresent()) {
                var currentValue = propertyValueOptional.get();

                config.properties().put(propertyKey, deserializedValue);
                ConfigSaver.save(config, template);

                context.getSource()
                    .sendSuccess(
                        () -> Component.literal("Modified property ")
                            .append(Component.literal(property).withStyle(ChatFormatting.AQUA))
                            .append(" in config ")
                            .append(Component.literal(config.name()).withStyle(ChatFormatting.AQUA))
                            .append(" (")
                            .append(Component.literal(currentValue.toString()).withStyle(ChatFormatting.YELLOW))
                            .append(" -> ")
                            .append(Component.literal(value).withStyle(ChatFormatting.GREEN))
                            .append(")."),
                        false
                    );

                hasSetProperty = true;
            }
        }

        if (!hasSetProperty) {
            ConfigCommandUtil.sendPropertyDoesNotExistErrorMessage(context, property);

            // TODO: Error code here?
            return -1;
        }

        return 1;
    }
}
