package com.blib.api.common.event.v1;

import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface BLibLevelSaveEvent {

    void invoke(ServerLevel level);
}
