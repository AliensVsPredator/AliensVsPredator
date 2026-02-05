package com.blib.mod.common.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.faction.BLibFactionManager;

@ApiStatus.Internal
public final class BLibCommandSuggestions {

    public static final SuggestionProvider<CommandSourceStack> FACTION_IDS = (context, builder) -> SharedSuggestionProvider
        .suggestResource(BLibFactionManager.INSTANCE.getAllIds(), builder);

    public static final SuggestionProvider<CommandSourceStack> FACTION_TYPE_IDS = (context, builder) -> SharedSuggestionProvider
        .suggestResource(BLibBuiltInRegistries.FACTION_TYPES.keySet(), builder);

    private BLibCommandSuggestions() {
        throw new UnsupportedOperationException();
    }
}
