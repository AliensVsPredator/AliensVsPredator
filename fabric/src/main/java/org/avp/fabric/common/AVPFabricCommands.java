package org.avp.fabric.common;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.avp.common.command.AVPCommands;

public class AVPFabricCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> AVPCommands.registerAll(dispatcher)
        );
    }

    private AVPFabricCommands() {
        throw new UnsupportedOperationException();
    }
}
