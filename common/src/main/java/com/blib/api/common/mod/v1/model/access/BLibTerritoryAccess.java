package com.blib.api.common.mod.v1.model.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.territory.v1.ChunkClaim;
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
    public boolean addClaim(ServerLevel level, ChunkPos pos, ChunkClaim claim) {
        return BLibTerritoryManager.INSTANCE.addClaim(level, pos, claim);
    }

    @Override
    public boolean removeClaim(ServerLevel level, ChunkPos pos, Claimant claimant, ResourceLocation reason) {
        return BLibTerritoryManager.INSTANCE.removeClaim(level, pos, claimant, reason);
    }

    @Override
    public void removeAllClaims(ServerLevel level, ChunkPos pos, Claimant claimant) {
        BLibTerritoryManager.INSTANCE.removeAllClaims(level, pos, claimant);
    }

    @Override
    public Set<ChunkClaim> getClaims(ServerLevel level, ChunkPos pos) {
        return BLibTerritoryManager.INSTANCE.getClaims(level, pos);
    }

    @Override
    public boolean isClaimed(ServerLevel level, ChunkPos pos) {
        return BLibTerritoryManager.INSTANCE.isClaimed(level, pos);
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
    public Set<ChunkPos> getContestedChunks(ServerLevel level, ResourceLocation reason) {
        return BLibTerritoryManager.INSTANCE.getContestedChunks(level, reason);
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
