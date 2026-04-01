package com.blib.internal.common.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class BLibTerritoryIndex {

    private final Map<ResourceLocation, Set<ChunkPos>> factionToChunks;

    private final Map<ChunkPos, Set<ResourceLocation>> chunkToFactions;

    private final Set<ChunkPos> allClaimedChunks;

    public BLibTerritoryIndex() {
        this.factionToChunks = new HashMap<>();
        this.chunkToFactions = new HashMap<>();
        this.allClaimedChunks = new HashSet<>();
    }

    public void onChunkLoaded(ChunkPos pos, Set<ResourceLocation> factionIds) {
        for (var factionId : factionIds) {
            onClaimAdded(pos, factionId);
        }
    }

    public void onChunkUnloaded(ChunkPos pos) {
        var factions = chunkToFactions.remove(pos);

        if (factions == null) {
            return;
        }

        allClaimedChunks.remove(pos);

        for (var factionId : factions) {
            var chunks = factionToChunks.get(factionId);

            if (chunks != null) {
                chunks.remove(pos);

                if (chunks.isEmpty()) {
                    factionToChunks.remove(factionId);
                }
            }
        }
    }

    public void onClaimAdded(ChunkPos pos, ResourceLocation factionId) {
        factionToChunks
            .computeIfAbsent(factionId, $ -> new HashSet<>())
            .add(pos);

        chunkToFactions
            .computeIfAbsent(pos, $ -> new HashSet<>())
            .add(factionId);

        allClaimedChunks.add(pos);
    }

    public void onClaimRemoved(ChunkPos pos, ResourceLocation factionId) {
        var chunks = factionToChunks.get(factionId);

        if (chunks != null) {
            chunks.remove(pos);

            if (chunks.isEmpty()) {
                factionToChunks.remove(factionId);
            }
        }

        var factions = chunkToFactions.get(pos);

        if (factions != null) {
            factions.remove(factionId);

            if (factions.isEmpty()) {
                chunkToFactions.remove(pos);
                allClaimedChunks.remove(pos);
            }
        }
    }

    public Set<ChunkPos> getChunks(ResourceLocation factionId) {
        return Collections.unmodifiableSet(factionToChunks.getOrDefault(factionId, Set.of()));
    }

    public Set<ChunkPos> getAllContestedChunks() {
        var result = new HashSet<ChunkPos>();

        for (var entry : chunkToFactions.entrySet()) {
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

    public boolean isClaimedBy(ChunkPos pos, ResourceLocation factionId) {
        var factions = chunkToFactions.get(pos);
        return factions != null && factions.contains(factionId);
    }

    public boolean isContested(ChunkPos pos) {
        var factions = chunkToFactions.get(pos);
        return factions != null && factions.size() > 1;
    }

    public int getClaimCount(ChunkPos pos) {
        var factions = chunkToFactions.get(pos);
        return factions == null ? 0 : factions.size();
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
        factionToChunks.clear();
        chunkToFactions.clear();
        allClaimedChunks.clear();
    }
}
