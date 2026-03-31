package com.blib.internal.client.territory;

import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.client.territory.compat.XaeroWorldMapCompat;
import com.blib.internal.client.territory.compat.xaero.BLibChunkHighlighter;

@ApiStatus.Internal
public class ClientTerritoryCache {

    public static final ClientTerritoryCache INSTANCE = new ClientTerritoryCache();

    private final Map<ChunkPos, List<Claimant>> claimantsByChunk;

    private ClientTerritoryCache() {
        this.claimantsByChunk = new HashMap<>();
    }

    public void updateChunk(int chunkX, int chunkZ, List<Claimant> claimants) {
        var pos = new ChunkPos(chunkX, chunkZ);

        if (claimants.isEmpty()) {
            claimantsByChunk.remove(pos);
        } else {
            claimantsByChunk.put(pos, List.copyOf(claimants));
        }

        if (XaeroWorldMapCompat.isLoaded()) {
            BLibChunkHighlighter.invalidateChunk(chunkX, chunkZ);
        }
    }

    public List<Claimant> getClaimants(ChunkPos pos) {
        return claimantsByChunk.getOrDefault(pos, List.of());
    }

    public boolean isClaimed(ChunkPos pos) {
        return claimantsByChunk.containsKey(pos);
    }

    public boolean isContested(ChunkPos pos) {
        var claimants = claimantsByChunk.get(pos);
        return claimants != null && claimants.size() > 1;
    }

    public void clear() {
        claimantsByChunk.clear();
    }
}
