package com.blib.api.common.event.v1;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface BLibServerSaveEvent {

    void invoke(MinecraftServer server);
}
