package com.avp.common.registry.init;

import com.alien.common.gameplay.command.hive.CurrentHiveLayerCommand;
import com.alien.common.gameplay.command.hive.NearestHiveCommand;
import net.minecraft.commands.Commands;

import com.avp.common.gameplay.command.count.CountCommand;
import com.avp.common.gameplay.command.nuke.NukeCommand;
import com.avp.service.Services;

public class AVPCommands {

    public static void initialize() {
        Services.REGISTRY.registerCommand(
            Commands.literal("avp")
                .then(
                    Commands.literal("debug")
                        .requires(
                            commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(CountCommand.create())
                        .then(
                            Commands.literal("hive")
                                .then(NearestHiveCommand.create())
                                .then(
                                    Commands.literal("layer")
                                        .then(CurrentHiveLayerCommand.create())
                                )
                        )
                )
                .then(
                    Commands.literal("test")
                        .requires(
                            commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(NukeCommand.create())
                )
        );
    }
}
