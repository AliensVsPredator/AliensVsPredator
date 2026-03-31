package com.blib.internal.common.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.territory.v1.ChunkClaim;
import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.registry.init.BLibTerritoryDataStoreTypes;

@ApiStatus.Internal
public class BLibTerritoryManager {

    public static final BLibTerritoryManager INSTANCE = new BLibTerritoryManager();

    private final Map<ServerLevel, BLibTerritoryIndex> indexes;

    private @Nullable MinecraftServer server;

    private BLibTerritoryManager() {
        this.indexes = new HashMap<>();
    }

    public void onServerStarted(MinecraftServer server) {
        this.server = server;
    }

    public void onServerStopped(MinecraftServer server) {
        indexes.clear();
        this.server = null;
    }

    public void onChunkLoaded(ServerLevel level, LevelChunk chunk) {
        var pos = chunk.getPos();
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return;
        }

        var claims = store.getClaims();

        if (!claims.isEmpty()) {
            getOrCreateIndex(level).onChunkLoaded(pos, claims);
        }
    }

    public void onChunkUnloaded(ServerLevel level, LevelChunk chunk) {
        var index = indexes.get(level);

        if (index != null) {
            index.onChunkUnloaded(chunk.getPos());
        }
    }

    public boolean addClaim(ServerLevel level, ChunkPos pos, ChunkClaim claim) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return false;
        }

        return store.addClaim(claim);
    }

    public boolean removeClaim(ServerLevel level, ChunkPos pos, Claimant claimant, ResourceLocation reason) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return false;
        }

        return store.removeClaim(claimant, reason);
    }

    public void removeAllClaims(ServerLevel level, ChunkPos pos, Claimant claimant) {
        var store = getOrCreateStore(level, pos);

        if (store != null) {
            store.removeAllClaims(claimant);
        }
    }

    public Set<ChunkClaim> getClaims(ServerLevel level, ChunkPos pos) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return Set.of();
        }

        return store.getClaims();
    }

    public boolean isClaimed(ServerLevel level, ChunkPos pos) {
        return getOrCreateIndex(level).isClaimed(pos);
    }

    public boolean isContested(ServerLevel level, ChunkPos pos) {
        return getOrCreateIndex(level).isContested(pos);
    }

    public Set<ChunkPos> getChunks(ServerLevel level, Claimant claimant) {
        return getOrCreateIndex(level).getChunks(claimant);
    }

    public Set<ChunkPos> getContestedChunks(ServerLevel level, ResourceLocation reason) {
        return getOrCreateIndex(level).getContestedChunks(reason);
    }

    public Set<ChunkPos> getAllContestedChunks(ServerLevel level) {
        return getOrCreateIndex(level).getAllContestedChunks();
    }

    public Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius) {
        return getOrCreateIndex(level).getUnclaimedChunks(center, radius);
    }

    public void onClaimAdded(ServerLevel level, ChunkPos pos, ChunkClaim claim) {
        getOrCreateIndex(level).onClaimAdded(pos, claim);
    }

    public void onClaimRemoved(ServerLevel level, ChunkPos pos, ChunkClaim claim) {
        getOrCreateIndex(level).onClaimRemoved(pos, claim);
    }

    public void onEntityRemoved(UUID entityId) {
        removeAllClaimsForClaimant(Claimant.entity(entityId));
    }

    public void onFactionRemoved(ResourceLocation factionId) {
        removeAllClaimsForClaimant(Claimant.faction(factionId));
    }

    private void removeAllClaimsForClaimant(Claimant claimant) {
        if (server == null) {
            return;
        }

        for (var level : server.getAllLevels()) {
            var index = indexes.get(level);

            if (index == null) {
                continue;
            }

            var chunks = Set.copyOf(index.getChunks(claimant));

            for (var pos : chunks) {
                var store = getOrCreateStore(level, pos);

                if (store != null) {
                    store.removeAllClaims(claimant);
                }
            }
        }
    }

    public void syncAllClaimsToPlayer(ServerPlayer player) {
        if (server == null) {
            return;
        }

        for (var level : server.getAllLevels()) {
            var index = indexes.get(level);

            if (index == null) {
                continue;
            }

            for (var pos : index.getAllClaimedChunks()) {
                var payload = buildSyncPayload(level, pos);
                BLib.MOD.networking().sendToClient(player, payload);
            }
        }
    }

    public S2CChunkClaimsSyncPayload buildSyncPayload(ServerLevel level, ChunkPos pos) {
        var claims = getClaims(level, pos);

        var claimDataList = claims.stream()
            .map(claim -> new S2CChunkClaimsSyncPayload.ClaimData(claim.claimant(), claim.reason()))
            .toList();

        return new S2CChunkClaimsSyncPayload(pos.x, pos.z, claimDataList);
    }

    private BLibTerritoryIndex getOrCreateIndex(ServerLevel level) {
        return indexes.computeIfAbsent(level, $ -> new BLibTerritoryIndex());
    }

    private ChunkClaimDataStore getOrCreateStore(ServerLevel level, ChunkPos pos) {
        return BLibDataStoreManager.INSTANCE
            .getChunk(level, pos, BLibTerritoryDataStoreTypes.CHUNK_CLAIMS)
            .inspect(store -> store.setContext(level, pos))
            .unwrapOr(null);
    }
}
