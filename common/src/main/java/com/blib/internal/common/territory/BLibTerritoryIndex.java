package com.blib.internal.common.territory;

import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.territory.v1.Claimant;

@ApiStatus.Internal
public class BLibTerritoryIndex {

    private final Map<Claimant, Set<ChunkPos>> claimantToChunks;

    private final Map<ChunkPos, Set<Claimant>> chunkToClaimants;

    private final Set<ChunkPos> allClaimedChunks;

    public BLibTerritoryIndex() {
        this.claimantToChunks = new HashMap<>();
        this.chunkToClaimants = new HashMap<>();
        this.allClaimedChunks = new HashSet<>();
    }

    public void onChunkLoaded(ChunkPos pos, Set<Claimant> claimants) {
        for (var claimant : claimants) {
            onClaimAdded(pos, claimant);
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
    }

    public void onClaimAdded(ChunkPos pos, Claimant claimant) {
        claimantToChunks
            .computeIfAbsent(claimant, $ -> new HashSet<>())
            .add(pos);

        chunkToClaimants
            .computeIfAbsent(pos, $ -> new HashSet<>())
            .add(claimant);

        allClaimedChunks.add(pos);
    }

    public void onClaimRemoved(ChunkPos pos, Claimant claimant) {
        var chunks = claimantToChunks.get(claimant);

        if (chunks != null) {
            chunks.remove(pos);

            if (chunks.isEmpty()) {
                claimantToChunks.remove(claimant);
            }
        }

        var claimants = chunkToClaimants.get(pos);

        if (claimants != null) {
            claimants.remove(claimant);

            if (claimants.isEmpty()) {
                chunkToClaimants.remove(pos);
                allClaimedChunks.remove(pos);
            }
        }
    }

    public Set<ChunkPos> getChunks(Claimant claimant) {
        return Collections.unmodifiableSet(claimantToChunks.getOrDefault(claimant, Set.of()));
    }

    public Set<ChunkPos> getAllContestedChunks() {
        var result = new HashSet<ChunkPos>();

        for (var entry : chunkToClaimants.entrySet()) {
            if (entry.getValue().size() > 1) {
                result.add(entry.getKey());
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public Set<ChunkPos> getAllClaimedChunks() {
        return Collections.unmodifiableSet(allClaimedChunks);
    }

    public boolean isClaimed(ChunkPos pos) {
        return allClaimedChunks.contains(pos);
    }

    public boolean isClaimedBy(ChunkPos pos, Claimant claimant) {
        var claimants = chunkToClaimants.get(pos);
        return claimants != null && claimants.contains(claimant);
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
        chunkToClaimants.clear();
        allClaimedChunks.clear();
    }
}
