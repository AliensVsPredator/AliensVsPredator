package com.avp.common.command.config;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

import com.avp.common.config.ConfigProperties;

public class ConfigCommandUtil {

    public static final SuggestionProvider<CommandSourceStack> PROPERTY_SUGGESTIONS = (context, builder) -> suggestConfigProperties(
        builder
    );

    public static CompletableFuture<Suggestions> suggestConfigProperties(SuggestionsBuilder builder) {
        var input = builder.getRemaining().toLowerCase();

        for (var property : ConfigProperties.values()) {
            var id = property.identifier();

            if (id.toLowerCase().startsWith(input)) {
                builder.suggest(id);
            }
        }

        return builder.buildFuture();
    }

    public static void sendPropertyDoesNotExistErrorMessage(CommandContext<CommandSourceStack> context, String property) {
        context.getSource()
            .sendFailure(
                Component.literal("Property '")
                    .append(property)
                    .append("' does not exist.")
                    .withStyle(ChatFormatting.RED)
            );
    }
}
