package com.blib.mod.common.registry.init;

import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.BLibAPI;
import com.blib.api.common.registry.v1.impl.BLibCommandRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.command.BLibDismembermentCommands;
import com.blib.mod.common.command.BLibFactionCommands;
import com.blib.mod.common.command.BLibGOAPCommands;
import com.blib.mod.common.command.BLibPropertyCommands;
import com.blib.mod.common.command.BLibReputationCommands;
import com.blib.mod.common.command.BLibTerritoryCommands;

@ApiStatus.Internal
public class BLibCommands {

    private static final BLibCommandRegistry REGISTRY = new BLibCommandRegistry(BLib.MOD);

    public static void initialize() {
        var root = Commands.literal("blib")
            .requires(source -> source.hasPermission(2))
            .then(BLibFactionCommands.build())
            .then(BLibReputationCommands.build())
            .then(BLibGOAPCommands.build())
            .then(BLibPropertyCommands.build())
            .then(BLibTerritoryCommands.build());

        // Debug-only: only register the dismemberment subcommand in development environments so it never ships with a
        // production build. Op-level-2 gating from the parent already blocks survival players from typing it.
        if (BLibAPI.isDevelopmentEnvironment()) {
            root = root.then(BLibDismembermentCommands.build());
        }

        REGISTRY.register(root);
    }
}
