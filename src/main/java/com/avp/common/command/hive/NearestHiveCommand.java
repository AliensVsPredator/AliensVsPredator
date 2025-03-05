package com.avp.common.command.hive;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Objects;

import com.avp.common.level.saveddata.HiveLevelData;

public class NearestHiveCommand {

    private static final String COMMAND_NAME = "hive";

    private static final String NEAREST_ARGUMENT_NAME = "nearest";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .then(
                Commands.literal(NEAREST_ARGUMENT_NAME)
                    .requires(CommandSourceStack::isPlayer)
                    .executes(context -> {
                        HiveLevelData.getOrCreate(context.getSource().getLevel())
                            .flatMap(
                                hiveLevelData -> hiveLevelData.findNearestHive(
                                    Objects.requireNonNull(context.getSource().getPlayer()).blockPosition()
                                )
                            )
                            .ifPresentOrElse(
                                hive -> {
                                    var pos = hive.centerPosition();
                                    context.getSource()
                                        .sendSuccess(
                                            () -> Component.literal(
                                                "Nearest hive: x " + pos.getX() + " y " + pos.getY() + " z " + pos.getZ()
                                            ),
                                            false
                                        );
                                },
                                () -> context.getSource().sendSuccess(() -> Component.literal("No nearby hive found."), false)
                            );

                        return 1;
                    })
            );
    }
}
