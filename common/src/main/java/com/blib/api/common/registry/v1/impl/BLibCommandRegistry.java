package com.blib.api.common.registry.v1.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
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
