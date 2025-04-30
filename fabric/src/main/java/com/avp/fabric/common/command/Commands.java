package com.avp.fabric.common.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.avp.fabric.common.command.count.CountCommand;
import com.avp.fabric.common.command.hive.NearestHiveCommand;
import com.avp.fabric.common.command.nuke.NukeCommand;

public class Commands {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> dispatcher.register(
                net.minecraft.commands.Commands.literal("avp")
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
            )
        );
    }
}
