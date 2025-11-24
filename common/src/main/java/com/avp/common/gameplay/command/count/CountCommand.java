package com.avp.common.gameplay.command.count;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class CountCommand {

    private static final String COMMAND_NAME = "count";

    private static final String ENTITY_ARGUMENT_NAME = "entity";

    private static final String HIVE_ARGUMENT_NAME = "hive";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .then(
                Commands.argument(ENTITY_ARGUMENT_NAME, EntityArgument.entities())
                    .executes(context -> {
                        var result = EntityArgument.getEntities(context, "entity");
                        var count = result.size();

                        context.getSource().sendSuccess(() -> {
                            var areOrIs = count == 1 ? "is" : "are";
                            var pluralEntity = count == 1 ? "that entity" : "those entities";
                            return Component.literal(
                                "There " + areOrIs + " " + count + " of " + pluralEntity + " in the world."
                            );
                        }, false);

                        return 1;
                    })
            );
        // FIXME:
//            .then(
//                Commands.literal(HIVE_ARGUMENT_NAME)
//                    .executes(context -> {
//                        int count = HiveLevelData.getOrCreate(context.getSource().getLevel())
//                            .map(hiveLevelData -> hiveLevelData.allHives().size())
//                            .unwrapOr(0);
//
//                        context.getSource().sendSuccess(() -> {
//                            var areOrIs = count == 1 ? "is" : "are";
//                            var pluralHive = count == 1 ? "hive" : "hives";
//                            return Component.literal(
//                                "There " + areOrIs + " " + count + " " + pluralHive + " in the world."
//                            );
//                        }, false);
//
//                        return 1;
//                    })
//            );
    }
}
