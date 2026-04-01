package com.blib.api.common.territory.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public interface TerritoryManager {

    boolean addClaim(ServerLevel level, ChunkPos pos, ResourceLocation factionId);

    boolean removeClaim(ServerLevel level, ChunkPos pos, ResourceLocation factionId);

    boolean transferClaim(ServerLevel level, ChunkPos pos, ResourceLocation from, ResourceLocation to);

    Set<ResourceLocation> getClaimants(ServerLevel level, ChunkPos pos);

    boolean isClaimed(ServerLevel level, ChunkPos pos);

    boolean isClaimedBy(ServerLevel level, ChunkPos pos, ResourceLocation factionId);

    boolean isContested(ServerLevel level, ChunkPos pos);

    Set<ChunkPos> getAdjacentClaimedChunks(ServerLevel level, ChunkPos pos, ResourceLocation factionId);

    Set<ChunkPos> getChunks(ServerLevel level, ResourceLocation factionId);

    Set<ChunkPos> getAllContestedChunks(ServerLevel level);

    Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius);
}
