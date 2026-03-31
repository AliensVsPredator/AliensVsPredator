package com.blib.api.common.territory.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public interface TerritoryManager {

    boolean addClaim(ServerLevel level, ChunkPos pos, ChunkClaim claim);

    boolean removeClaim(ServerLevel level, ChunkPos pos, Claimant claimant, ResourceLocation reason);

    void removeAllClaims(ServerLevel level, ChunkPos pos, Claimant claimant);

    Set<ChunkClaim> getClaims(ServerLevel level, ChunkPos pos);

    boolean isClaimed(ServerLevel level, ChunkPos pos);

    boolean isContested(ServerLevel level, ChunkPos pos);

    Set<ChunkPos> getChunks(ServerLevel level, Claimant claimant);

    Set<ChunkPos> getContestedChunks(ServerLevel level, ResourceLocation reason);

    Set<ChunkPos> getAllContestedChunks(ServerLevel level);

    Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius);
}
