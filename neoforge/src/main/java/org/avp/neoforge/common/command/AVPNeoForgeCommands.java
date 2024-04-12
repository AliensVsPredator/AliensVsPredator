package org.avp.neoforge.common.command;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.avp.common.command.AVPCommands;

public final class AVPNeoForgeCommands {

    public static void registerCommandsEvent(RegisterCommandsEvent registerCommandsEvent) {
        var dispatcher = registerCommandsEvent.getDispatcher();
        AVPCommands.registerAll(dispatcher);
    }

    private AVPNeoForgeCommands() {
        throw new UnsupportedOperationException();
    }
}
