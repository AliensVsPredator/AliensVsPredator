package com.blib.api.common.event.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

@FunctionalInterface
public interface BLibChunkClaimRemovedEvent {

    void invoke(ServerLevel level, ChunkPos pos, ResourceLocation factionId);
}
