package com.blib.internal.common.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.territory.v1.ChunkClaim;
import com.blib.api.common.territory.v1.Claimant;

@ApiStatus.Internal
public class BLibTerritoryIndex {

    private final Map<Claimant, Set<ChunkPos>> claimantToChunks;

    private final Map<ResourceLocation, Set<ChunkPos>> contestedByReason;

    private final Map<ChunkPos, Set<Claimant>> chunkToClaimants;

    private final Set<ChunkPos> allClaimedChunks;

    public BLibTerritoryIndex() {
        this.claimantToChunks = new HashMap<>();
        this.contestedByReason = new HashMap<>();
        this.chunkToClaimants = new HashMap<>();
        this.allClaimedChunks = new HashSet<>();
    }

    public void onChunkLoaded(ChunkPos pos, Set<ChunkClaim> claims) {
        for (var claim : claims) {
            onClaimAdded(pos, claim);
        }
    }

    public void onChunkUnloaded(ChunkPos pos) {
        var claimants = chunkToClaimants.remove(pos);

        if (claimants == null) {
            return;
        }

        allClaimedChunks.remove(pos);

        for (var claimant : claimants) {
            var chunks = claimantToChunks.get(claimant);

            if (chunks != null) {
                chunks.remove(pos);

                if (chunks.isEmpty()) {
                    claimantToChunks.remove(claimant);
                }
            }
        }

        for (var entry : contestedByReason.entrySet()) {
            entry.getValue().remove(pos);
        }

        contestedByReason.values().removeIf(Set::isEmpty);
    }

    public void onClaimAdded(ChunkPos pos, ChunkClaim claim) {
        claimantToChunks
            .computeIfAbsent(claim.claimant(), $ -> new HashSet<>())
            .add(pos);

        chunkToClaimants
            .computeIfAbsent(pos, $ -> new HashSet<>())
            .add(claim.claimant());

        allClaimedChunks.add(pos);

        updateContested(pos, claim.reason());
    }

    public void onClaimRemoved(ChunkPos pos, ChunkClaim claim) {
        var chunks = claimantToChunks.get(claim.claimant());

        if (chunks != null) {
            chunks.remove(pos);

            if (chunks.isEmpty()) {
                claimantToChunks.remove(claim.claimant());
            }
        }

        var claimants = chunkToClaimants.get(pos);

        if (claimants != null) {
            claimants.remove(claim.claimant());

            if (claimants.isEmpty()) {
                chunkToClaimants.remove(pos);
                allClaimedChunks.remove(pos);
            }
        }

        updateContested(pos, claim.reason());
    }

    private void updateContested(ChunkPos pos, ResourceLocation reason) {
        var claimants = chunkToClaimants.get(pos);
        var claimantCount = claimants == null ? 0 : claimants.size();

        if (claimantCount > 1) {
            contestedByReason
                .computeIfAbsent(reason, $ -> new HashSet<>())
                .add(pos);
        } else {
            var contested = contestedByReason.get(reason);

            if (contested != null) {
                contested.remove(pos);

                if (contested.isEmpty()) {
                    contestedByReason.remove(reason);
                }
            }
        }
    }

    public Set<ChunkPos> getChunks(Claimant claimant) {
        return Collections.unmodifiableSet(claimantToChunks.getOrDefault(claimant, Set.of()));
    }

    public Set<ChunkPos> getContestedChunks(ResourceLocation reason) {
        return Collections.unmodifiableSet(contestedByReason.getOrDefault(reason, Set.of()));
    }

    public Set<ChunkPos> getAllContestedChunks() {
        var result = new HashSet<ChunkPos>();

        for (var chunks : contestedByReason.values()) {
            result.addAll(chunks);
        }

        return Collections.unmodifiableSet(result);
    }

    public Set<ChunkPos> getAllClaimedChunks() {
        return Collections.unmodifiableSet(allClaimedChunks);
    }

    public boolean isClaimed(ChunkPos pos) {
        return allClaimedChunks.contains(pos);
    }

    public boolean isContested(ChunkPos pos) {
        var claimants = chunkToClaimants.get(pos);
        return claimants != null && claimants.size() > 1;
    }

    public int getClaimCount(ChunkPos pos) {
        var claimants = chunkToClaimants.get(pos);
        return claimants == null ? 0 : claimants.size();
    }

    public Set<ChunkPos> getUnclaimedChunks(ChunkPos center, int radius) {
        var result = new HashSet<ChunkPos>();

        for (var x = center.x - radius; x <= center.x + radius; x++) {
            for (var z = center.z - radius; z <= center.z + radius; z++) {
                var pos = new ChunkPos(x, z);

                if (!allClaimedChunks.contains(pos)) {
                    result.add(pos);
                }
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public void clear() {
        claimantToChunks.clear();
        contestedByReason.clear();
        chunkToClaimants.clear();
        allClaimedChunks.clear();
    }
}
