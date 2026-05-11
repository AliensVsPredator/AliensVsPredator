package com.blib.internal.client.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.client.territory.compat.XaeroWorldMapCompat;
import com.blib.internal.client.territory.compat.xaero.BLibChunkHighlighter;

@ApiStatus.Internal
public class ClientTerritoryCache {

    public static final ClientTerritoryCache INSTANCE = new ClientTerritoryCache();

    private final Map<ChunkPos, List<ResourceLocation>> factionsByChunk;

    private ClientTerritoryCache() {
        this.factionsByChunk = new HashMap<>();
    }

    public void updateChunk(int chunkX, int chunkZ, List<ResourceLocation> factionIds) {
        var pos = new ChunkPos(chunkX, chunkZ);

        if (factionIds.isEmpty()) {
            factionsByChunk.remove(pos);
        } else {
            factionsByChunk.put(pos, List.copyOf(factionIds));
        }

        if (XaeroWorldMapCompat.isLoaded()) {
            BLibChunkHighlighter.invalidateChunk(chunkX, chunkZ);
        }
    }

    public List<ResourceLocation> getFactionIds(ChunkPos pos) {
        return factionsByChunk.getOrDefault(pos, List.of());
    }

    public boolean isClaimed(ChunkPos pos) {
        return factionsByChunk.containsKey(pos);
    }

    public boolean isContested(ChunkPos pos) {
        var factions = factionsByChunk.get(pos);
        return factions != null && factions.size() > 1;
    }

    /**
     * Read-only view of the underlying chunk → claimants map. Renderers iterate this each frame to draw claim overlays
     * / map cells. Mutations go through {@link #updateChunk}; the view doesn't support direct edits.
     */
    public Map<ChunkPos, List<ResourceLocation>> factionsByChunk() {
        return java.util.Collections.unmodifiableMap(factionsByChunk);
    }

    /** Counts how many chunks in the cache list {@code factionId} as a claimant. Used by the inspector. */
    public int chunkCountForFaction(ResourceLocation factionId) {
        var count = 0;
        for (var ids : factionsByChunk.values()) {
            if (ids.contains(factionId)) {
                count++;
            }
        }
        return count;
    }

    public void clear() {
        factionsByChunk.clear();
    }
}
