package com.blib.internal.client.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.client.territory.compat.XaeroWorldMapCompat;
import com.blib.internal.client.territory.compat.xaero.BLibChunkHighlighter;

@ApiStatus.Internal
public class ClientTerritoryCache {

    public static final ClientTerritoryCache INSTANCE = new ClientTerritoryCache();

    private final Map<ResourceLocation, Map<ChunkPos, List<ResourceLocation>>> factionsByDimension;

    private ClientTerritoryCache() {
        this.factionsByDimension = new HashMap<>();
    }

    public void updateChunk(ResourceLocation dimension, int chunkX, int chunkZ, List<ResourceLocation> factionIds) {
        var pos = new ChunkPos(chunkX, chunkZ);
        var factionsByChunk = factionsByDimension.computeIfAbsent(dimension, $ -> new HashMap<>());

        if (factionIds.isEmpty()) {
            factionsByChunk.remove(pos);

            if (factionsByChunk.isEmpty()) {
                factionsByDimension.remove(dimension);
            }
        } else {
            factionsByChunk.put(pos, List.copyOf(factionIds));
        }

        if (XaeroWorldMapCompat.isLoaded()) {
            BLibChunkHighlighter.invalidateChunk(chunkX, chunkZ);
        }
    }

    public void updateChunks(ResourceLocation dimension, List<com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload.Entry> entries) {
        for (var entry : entries) {
            updateChunk(dimension, entry.chunkX(), entry.chunkZ(), entry.factionIds());
        }
    }

    public List<ResourceLocation> getFactionIds(ResourceLocation dimension, ChunkPos pos) {
        return chunksForDimension(dimension).getOrDefault(pos, List.of());
    }

    public boolean isClaimed(ResourceLocation dimension, ChunkPos pos) {
        return chunksForDimension(dimension).containsKey(pos);
    }

    public boolean isContested(ResourceLocation dimension, ChunkPos pos) {
        var factions = chunksForDimension(dimension).get(pos);
        return factions != null && factions.size() > 1;
    }

    /**
     * Read-only view of the underlying chunk → claimants map. Renderers iterate this each frame to draw claim overlays
     * / map cells. Mutations go through {@link #updateChunk}; the view doesn't support direct edits.
     */
    public Map<ChunkPos, List<ResourceLocation>> factionsByChunk(ResourceLocation dimension) {
        return Collections.unmodifiableMap(chunksForDimension(dimension));
    }

    /** Counts how many chunks in the cache list {@code factionId} as a claimant. Used by the inspector. */
    public int chunkCountForFaction(ResourceLocation dimension, ResourceLocation factionId) {
        var count = 0;
        for (var ids : chunksForDimension(dimension).values()) {
            if (ids.contains(factionId)) {
                count++;
            }
        }
        return count;
    }

    public void clear() {
        factionsByDimension.clear();
    }

    private Map<ChunkPos, List<ResourceLocation>> chunksForDimension(ResourceLocation dimension) {
        return factionsByDimension.getOrDefault(dimension, Map.of());
    }
}
