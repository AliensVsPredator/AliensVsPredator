package com.blib.api.common.mod.v1.model.access;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.territory.v1.Claimant;
import com.blib.api.common.territory.v1.TerritoryManager;
import com.blib.internal.common.territory.BLibTerritoryManager;

public class BLibTerritoryAccess implements TerritoryManager {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibTerritoryAccess(BLibMod mod) {
        this.mod = mod;
    }

    @Override
    public boolean addClaim(ServerLevel level, ChunkPos pos, Claimant claimant) {
        return BLibTerritoryManager.INSTANCE.addClaim(level, pos, claimant);
    }

    @Override
    public boolean removeClaim(ServerLevel level, ChunkPos pos, Claimant claimant) {
        return BLibTerritoryManager.INSTANCE.removeClaim(level, pos, claimant);
    }

    @Override
    public boolean transferClaim(ServerLevel level, ChunkPos pos, Claimant from, Claimant to) {
        return BLibTerritoryManager.INSTANCE.transferClaim(level, pos, from, to);
    }

    @Override
    public Set<Claimant> getClaimants(ServerLevel level, ChunkPos pos) {
        return BLibTerritoryManager.INSTANCE.getClaimants(level, pos);
    }

    @Override
    public boolean isClaimed(ServerLevel level, ChunkPos pos) {
        return BLibTerritoryManager.INSTANCE.isClaimed(level, pos);
    }

    @Override
    public boolean isClaimedBy(ServerLevel level, ChunkPos pos, Claimant claimant) {
        return BLibTerritoryManager.INSTANCE.isClaimedBy(level, pos, claimant);
    }

    @Override
    public boolean isContested(ServerLevel level, ChunkPos pos) {
        return BLibTerritoryManager.INSTANCE.isContested(level, pos);
    }

    @Override
    public Set<ChunkPos> getChunks(ServerLevel level, Claimant claimant) {
        return BLibTerritoryManager.INSTANCE.getChunks(level, claimant);
    }

    @Override
    public Set<ChunkPos> getAdjacentClaimedChunks(ServerLevel level, ChunkPos pos, Claimant claimant) {
        return BLibTerritoryManager.INSTANCE.getAdjacentClaimedChunks(level, pos, claimant);
    }

    @Override
    public Set<ChunkPos> getAllContestedChunks(ServerLevel level) {
        return BLibTerritoryManager.INSTANCE.getAllContestedChunks(level);
    }

    @Override
    public Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius) {
        return BLibTerritoryManager.INSTANCE.getUnclaimedChunks(level, center, radius);
    }
}
