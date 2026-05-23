package com.blib.api.common.event.v1;

import net.minecraft.server.MinecraftServer;

/**
 * Fired after BLib has loaded the server's faction store for the current world.
 */
@FunctionalInterface
public interface BLibFactionsLoadedEvent {

    void invoke(MinecraftServer server);
}
