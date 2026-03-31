package com.blib.api.common.territory.v1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;

public interface TerritoryManager {

    boolean addClaim(ServerLevel level, ChunkPos pos, Claimant claimant);

    boolean removeClaim(ServerLevel level, ChunkPos pos, Claimant claimant);

    boolean transferClaim(ServerLevel level, ChunkPos pos, Claimant from, Claimant to);

    Set<Claimant> getClaimants(ServerLevel level, ChunkPos pos);

    boolean isClaimed(ServerLevel level, ChunkPos pos);

    boolean isClaimedBy(ServerLevel level, ChunkPos pos, Claimant claimant);

    boolean isContested(ServerLevel level, ChunkPos pos);

    Set<ChunkPos> getAdjacentClaimedChunks(ServerLevel level, ChunkPos pos, Claimant claimant);

    Set<ChunkPos> getChunks(ServerLevel level, Claimant claimant);

    Set<ChunkPos> getAllContestedChunks(ServerLevel level);

    Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius);
}
