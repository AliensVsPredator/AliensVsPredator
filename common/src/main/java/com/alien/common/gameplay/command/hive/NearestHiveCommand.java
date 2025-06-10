package com.alien.common.gameplay.command.hive;

import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class NearestHiveCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("nearest")
            .requires(CommandSourceStack::isPlayer)
            .executes(context -> {
                HiveLevelData.getOrCreate(context.getSource().getLevel())
                    .andThen(
                        hiveLevelData -> hiveLevelData.findNearestHive(
                            Objects.requireNonNull(context.getSource().getPlayer()).blockPosition()
                        )
                    )
                    .inspect(hive -> {
                        var pos = hive.centerPosition();
                        context.getSource()
                            .sendSuccess(
                                () -> Component.literal(
                                    "Nearest hive: x " + pos.getX() + " y " + pos.getY() + " z " + pos.getZ()
                                ),
                                false
                            );
                    })
                    .ifNone(() -> context.getSource().sendSuccess(() -> Component.literal("No nearby hive found."), false));

                return 1;
            });
    }
}
