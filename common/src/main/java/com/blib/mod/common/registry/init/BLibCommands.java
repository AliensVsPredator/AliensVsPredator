package com.blib.mod.common.registry.init;

import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.registry.v1.impl.BLibCommandRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.command.BLibPropertyCommands;
import com.blib.mod.common.command.BLibReputationCommands;

@ApiStatus.Internal
public class BLibCommands {

    private static final BLibCommandRegistry REGISTRY = new BLibCommandRegistry(BLib.MOD);

    public static void initialize() {
        var root = Commands.literal("blib")
            .requires(source -> source.hasPermission(2))
            .then(BLibReputationCommands.build())
            .then(BLibPropertyCommands.build());

        REGISTRY.register(root);
    }
}
