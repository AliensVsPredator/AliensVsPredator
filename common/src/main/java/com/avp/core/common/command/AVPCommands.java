package com.avp.core.common.command;

import com.avp.core.platform.service.Services;

import com.avp.core.common.command.config.ConfigCheckCommand;
import com.avp.core.common.command.config.ConfigResetCommand;
import com.avp.core.common.command.config.ConfigSetCommand;
import com.avp.core.common.command.count.CountCommand;
import com.avp.core.common.command.hive.NearestHiveCommand;
import com.avp.core.common.command.nuke.NukeCommand;
import net.minecraft.commands.Commands;

public class AVPCommands {

    public static void initialize() {
        Services.REGISTRY.registerCommand(
            Commands.literal("avp")
                .then(
                    net.minecraft.commands.Commands.literal("config")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(net.minecraft.commands.Commands.LEVEL_ADMINS))
                        .then(ConfigCheckCommand.create())
                        .then(ConfigResetCommand.create())
                        .then(ConfigSetCommand.create())
                )
                .then(
                    net.minecraft.commands.Commands.literal("debug")
                        .requires(
                            commandSourceStack -> commandSourceStack.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(CountCommand.create())
                        .then(NearestHiveCommand.create())
                )
                .then(
                    net.minecraft.commands.Commands.literal("test")
                        .requires(
                            commandSourceStack -> commandSourceStack.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(NukeCommand.create())
                )
        );
    }
}
