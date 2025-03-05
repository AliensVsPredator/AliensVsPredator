package com.avp.core.common.command.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.config.io.ConfigSaver;

public class ConfigResetCommand {

    private static final String COMMAND_NAME = "reset";

    private static final String PROPERTY_ARGUMENT_NAME = "property";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .then(
                Commands.argument(PROPERTY_ARGUMENT_NAME, StringArgumentType.word())
                    .suggests(ConfigCommandUtil.PROPERTY_SUGGESTIONS)
                    .executes(ConfigResetCommand::execute)
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

        var hasResetProperty = false;

        for (var configEntry : AVP.CONFIGS.entrySet()) {
            var configContainer = configEntry.getValue();
            var baseConfig = configContainer.baseConfig();
            var config = configContainer.config();
            var template = configContainer.template();

            var propertyValueOptional = baseConfig.properties().get(propertyKey);

            if (propertyValueOptional.isPresent()) {
                var currentValue = config.properties().getOrNull(propertyKey);
                var baseValue = propertyValueOptional.get();

                config.properties().put(propertyKey, baseValue);
                ConfigSaver.save(config, template);

                context.getSource()
                    .sendSuccess(
                        () -> Component.literal("Modified property ")
                            .append(Component.literal(property).withStyle(ChatFormatting.AQUA))
                            .append(" in config ")
                            .append(Component.literal(config.name()).withStyle(ChatFormatting.AQUA))
                            .append(" (")
                            .append(
                                Component.literal(currentValue == null ? "null" : currentValue.toString()).withStyle(ChatFormatting.YELLOW)
                            )
                            .append(" -> ")
                            .append(Component.literal(baseValue.toString()).withStyle(ChatFormatting.LIGHT_PURPLE))
                            .append(")."),
                        false
                    );

                hasResetProperty = true;
            }
        }

        if (!hasResetProperty) {
            ConfigCommandUtil.sendPropertyDoesNotExistErrorMessage(context, property);

            // TODO: Error code here?
            return -1;
        }

        return 1;
    }
}
