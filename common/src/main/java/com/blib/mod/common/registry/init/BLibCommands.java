package com.blib.mod.common.registry.init;

import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.registry.v1.impl.BLibCommandRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.command.BLibFactionCommands;
import com.blib.mod.common.command.BLibGOAPCommands;
import com.blib.mod.common.command.BLibPropertyCommands;
import com.blib.mod.common.command.BLibReputationCommands;

@ApiStatus.Internal
public class BLibCommands {

    private static final BLibCommandRegistry REGISTRY = new BLibCommandRegistry(BLib.MOD);

    public static void initialize() {
        REGISTRY.register(
            Commands.literal("blib")
                .requires(source -> source.hasPermission(2))
                .then(BLibFactionCommands.build())
                .then(BLibReputationCommands.build())
                .then(BLibGOAPCommands.build())
                .then(BLibPropertyCommands.build())
        );
    }
}
