package org.avp.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class AVPCommands {

    public static void registerAll(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        AVPLegacySchematicCommand.register(commandDispatcher);
    }

    private AVPCommands() {
        throw new UnsupportedOperationException();
    }
}
