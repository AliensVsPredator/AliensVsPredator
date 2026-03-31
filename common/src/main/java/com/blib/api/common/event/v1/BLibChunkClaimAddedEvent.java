package com.blib.api.common.event.v1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import com.blib.api.common.territory.v1.Claimant;

@FunctionalInterface
public interface BLibChunkClaimAddedEvent {

    void invoke(ServerLevel level, ChunkPos pos, Claimant claimant);
}
