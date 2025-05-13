package com.avp.common.command.hive;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Objects;

import com.avp.common.level.saveddata.HiveLevelData;

public class CurrentHiveLayerCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("current")
            .requires(CommandSourceStack::isPlayer)
            .executes(context -> {
                var playerPos = Objects.requireNonNull(context.getSource().getPlayer()).blockPosition();

                HiveLevelData.getOrCreate(context.getSource().getLevel())
                    .andThen(
                        hiveLevelData -> hiveLevelData.findNearestHive(
                            Objects.requireNonNull(context.getSource().getPlayer()).blockPosition()
                        )
                    )
                    .inspect(hive -> {
                        var currentLayer = hive.getSpaceManager().getLayerOrNull(playerPos);

                        if (currentLayer == null) {
                            context.getSource()
                                .sendSuccess(() -> Component.literal("No layer found."), false);
                        } else {
                            var hiveLayer = hive.getSpaceManager().getHiveLayerOrNull(playerPos);

                            context.getSource()
                                .sendSuccess(
                                    () -> Component.literal("Current hive layer: " + hiveLayer),
                                    false
                                );
                        }
                    })
                    .ifNone(() -> context.getSource().sendSuccess(() -> Component.literal("No nearby hive found."), false));

                return 1;
            });
    }
}
