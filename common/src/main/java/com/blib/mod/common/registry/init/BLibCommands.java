package com.blib.mod.common.registry.init;

import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.BLibAPI;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.registry.v1.impl.BLibCommandRegistry;
import com.blib.engine.command.BLibEngineCommand;
import com.blib.engine.command.BLibTransformTuneCommand;
import com.blib.mod.BLib;
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

        // Engine mode is intrinsically client-side (camera detachment, gizmos, freecam HUD); only register on the
        // client distribution so referencing client classes from `BLibEngineCommand` can't link-fail on a dedicated
        // server. Available in production — op-level-2 gating from the parent keeps survival players out.
        if (BLibAPI.getDistributionType() == DistributionEnvironmentType.CLIENT) {
            root = root.then(BLibEngineCommand.build());
        }

        // Debug-only: only register dev subcommands in development environments so they never ship with a production
        // build. Op-level-2 gating from the parent already blocks survival players from typing it.
        if (BLibAPI.isDevelopmentEnvironment()) {
            if (BLibAPI.getDistributionType() == DistributionEnvironmentType.CLIENT) {
                root = root.then(BLibTransformTuneCommand.build());
            }
        }

        REGISTRY.register(root);
    }
}
