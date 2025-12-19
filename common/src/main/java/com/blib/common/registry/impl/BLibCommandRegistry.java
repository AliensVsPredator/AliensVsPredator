package com.blib.common.registry.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.model.BLibModState;
import com.blib.internal.service.BLibInternalServices;

public class BLibCommandRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibCommandRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a command outside of mod's initialization window. LiteralArgumentBuilder: %s, Mod State: %s"
                    .formatted(literalArgumentBuilder, mod.state())
            );
        }

        BLibInternalServices.REGISTRY.registerCommand(mod, literalArgumentBuilder);
    }
}
