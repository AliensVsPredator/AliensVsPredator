package com.blib.api.common.event.v1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

@FunctionalInterface
public interface BLibChunkUnloadEvent {

    void invoke(ServerLevel level, LevelChunk chunk);
}
