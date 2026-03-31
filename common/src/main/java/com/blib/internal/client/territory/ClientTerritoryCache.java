package com.blib.internal.client.territory;

import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.client.territory.compat.XaeroWorldMapCompat;
import com.blib.internal.client.territory.compat.xaero.BLibChunkHighlighter;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload.ClaimData;

@ApiStatus.Internal
public class ClientTerritoryCache {

    public static final ClientTerritoryCache INSTANCE = new ClientTerritoryCache();

    private final Map<ChunkPos, List<ClaimData>> claimsByChunk;

    private ClientTerritoryCache() {
        this.claimsByChunk = new HashMap<>();
    }

    public void updateChunk(int chunkX, int chunkZ, List<ClaimData> claims) {
        var pos = new ChunkPos(chunkX, chunkZ);

        if (claims.isEmpty()) {
            claimsByChunk.remove(pos);
        } else {
            claimsByChunk.put(pos, List.copyOf(claims));
        }

        if (XaeroWorldMapCompat.isLoaded()) {
            BLibChunkHighlighter.invalidateChunk(chunkX, chunkZ);
        }
    }

    public List<ClaimData> getClaims(ChunkPos pos) {
        return claimsByChunk.getOrDefault(pos, List.of());
    }

    public boolean isClaimed(ChunkPos pos) {
        return claimsByChunk.containsKey(pos);
    }

    public boolean isContested(ChunkPos pos) {
        var claims = claimsByChunk.get(pos);

        if (claims == null || claims.size() < 2) {
            return false;
        }

        return getDistinctClaimants(claims).size() > 1;
    }

    public void clear() {
        claimsByChunk.clear();
    }

    private Set<Claimant> getDistinctClaimants(List<ClaimData> claims) {
        return claims.stream()
            .map(ClaimData::claimant)
            .collect(Collectors.toSet());
    }
}
