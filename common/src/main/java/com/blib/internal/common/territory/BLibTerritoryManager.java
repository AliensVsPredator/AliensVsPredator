package com.blib.internal.common.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.internal.common.faction.BLibFactionManager;
import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.registry.init.BLibTerritoryDataStoreTypes;

@ApiStatus.Internal
public class BLibTerritoryManager {

    public static final BLibTerritoryManager INSTANCE = new BLibTerritoryManager();

    private static final int SYNC_BATCH_SIZE = 512;

    private final Map<ServerLevel, BLibTerritoryIndex> indexes;

    private final Map<ServerLevel, Set<ChunkPos>> pendingClaimStoreSaves;

    private @Nullable MinecraftServer server;

    private BLibTerritoryManager() {
        this.indexes = new HashMap<>();
        this.pendingClaimStoreSaves = new HashMap<>();
    }

    public void onServerStarted(MinecraftServer server) {
        this.server = server;
        rebuildIndexes(server);
    }

    public void onServerStopped(MinecraftServer server) {
        flushPendingClaimStoreSaves(server);
        indexes.clear();
        pendingClaimStoreSaves.clear();
        this.server = null;
    }

    public void onChunkLoaded(ServerLevel level, LevelChunk chunk) {
        var pos = chunk.getPos();
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return;
        }

        var claimants = store.getClaimants();

        if (!claimants.isEmpty()) {
            getOrCreateIndex(level).onChunkLoaded(pos, claimants);
        }
    }

    public void onChunkUnloaded(ServerLevel level, LevelChunk chunk) {
        // Territory claims are persisted independently of chunk load state.
    }

    public boolean addClaim(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return false;
        }

        var changed = store.addClaim(factionId);

        if (changed) {
            markClaimStoreDirty(level, pos);
        }

        return changed;
    }

    public boolean removeClaim(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return false;
        }

        var changed = store.removeClaim(factionId);

        if (changed) {
            markClaimStoreDirty(level, pos);
        }

        return changed;
    }

    public boolean transferClaim(ServerLevel level, ChunkPos pos, ResourceLocation from, ResourceLocation to) {
        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return false;
        }

        var changed = store.transferClaim(from, to);

        if (changed) {
            markClaimStoreDirty(level, pos);
        }

        return changed;
    }

    public Set<ResourceLocation> getClaimants(ServerLevel level, ChunkPos pos) {
        var index = indexes.get(level);

        if (index != null) {
            return index.getClaimants(pos);
        }

        var store = getOrCreateStore(level, pos);

        if (store == null) {
            return Set.of();
        }

        return store.getClaimants();
    }

    public boolean isClaimed(ServerLevel level, ChunkPos pos) {
        return getOrCreateIndex(level).isClaimed(pos);
    }

    public boolean isClaimedBy(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        return getOrCreateIndex(level).isClaimedBy(pos, factionId);
    }

    public boolean isContested(ServerLevel level, ChunkPos pos) {
        return getOrCreateIndex(level).isContested(pos);
    }

    public Set<ChunkPos> getAdjacentClaimedChunks(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        var index = getOrCreateIndex(level);
        var result = new HashSet<ChunkPos>();

        for (var neighbor : getCardinalNeighbors(pos)) {
            if (index.isClaimedBy(neighbor, factionId)) {
                result.add(neighbor);
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public Set<ChunkPos> getChunks(ServerLevel level, ResourceLocation factionId) {
        return getOrCreateIndex(level).getChunks(factionId);
    }

    public Set<ChunkPos> getAllContestedChunks(ServerLevel level) {
        return getOrCreateIndex(level).getAllContestedChunks();
    }

    public Set<ChunkPos> getUnclaimedChunks(ServerLevel level, ChunkPos center, int radius) {
        return getOrCreateIndex(level).getUnclaimedChunks(center, radius);
    }

    public void onClaimAdded(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        getOrCreateIndex(level).onClaimAdded(pos, factionId);
    }

    public void onClaimRemoved(ServerLevel level, ChunkPos pos, ResourceLocation factionId) {
        getOrCreateIndex(level).onClaimRemoved(pos, factionId);
    }

    public void onFactionRemoved(ResourceLocation factionId) {
        if (server == null) {
            return;
        }

        for (var level : server.getAllLevels()) {
            var index = indexes.get(level);

            if (index == null) {
                continue;
            }

            var chunks = Set.copyOf(index.getChunks(factionId));

            for (var pos : chunks) {
                removeClaim(level, pos, factionId);
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

            syncClaimChunksToPlayer(level, index.getAllClaimedChunks(), player);
        }
    }

    public S2CChunkClaimsSyncPayload buildSyncPayload(ServerLevel level, ChunkPos pos) {
        var claimants = getClaimants(level, pos);

        return S2CChunkClaimsSyncPayload.incremental(
            level.dimension().location(),
            List.of(new S2CChunkClaimsSyncPayload.Entry(pos.x, pos.z, new ArrayList<>(claimants)))
        );
    }

    public boolean allowExplosionsAt(ServerLevel level, ChunkPos pos) {
        var claimants = getClaimants(level, pos);

        for (var factionId : claimants) {
            var faction = BLibFactionManager.INSTANCE.get(factionId);

            if (faction != null && !faction.allowExplosions()) {
                return false;
            }
        }

        return true;
    }

    public boolean allowMobGriefingAt(ServerLevel level, ChunkPos pos) {
        var claimants = getClaimants(level, pos);

        for (var factionId : claimants) {
            var faction = BLibFactionManager.INSTANCE.get(factionId);

            if (faction != null && !faction.allowMobGriefing()) {
                return false;
            }
        }

        return true;
    }

    public S2CChunkClaimsSyncPayload buildSyncPayloadForPlayer(ServerLevel level, ChunkPos pos, ServerPlayer player) {
        var claimants = getClaimants(level, pos);

        var visibleFactions = claimants.stream()
            .filter(factionId -> isFactionVisibleToPlayer(factionId, player))
            .toList();

        return S2CChunkClaimsSyncPayload.incremental(
            level.dimension().location(),
            List.of(new S2CChunkClaimsSyncPayload.Entry(pos.x, pos.z, visibleFactions))
        );
    }

    public void syncClaimChunksToPlayer(ServerLevel level, Iterable<ChunkPos> chunks, ServerPlayer player) {
        var entries = new ArrayList<S2CChunkClaimsSyncPayload.Entry>(SYNC_BATCH_SIZE);

        for (var pos : chunks) {
            var claimants = getClaimants(level, pos);
            var visibleFactions = claimants.stream()
                .filter(factionId -> isFactionVisibleToPlayer(factionId, player))
                .toList();

            if (visibleFactions.isEmpty()) {
                continue;
            }

            entries.add(new S2CChunkClaimsSyncPayload.Entry(pos.x, pos.z, visibleFactions));

            if (entries.size() >= SYNC_BATCH_SIZE) {
                BLib.MOD.networking().sendToClient(player, S2CChunkClaimsSyncPayload.incremental(level.dimension().location(), List.copyOf(entries)));
                entries.clear();
            }
        }

        if (!entries.isEmpty()) {
            BLib.MOD.networking().sendToClient(player, S2CChunkClaimsSyncPayload.incremental(level.dimension().location(), List.copyOf(entries)));
        }
    }

    public void syncClaimsInAreaToPlayer(
        ServerLevel level,
        int minChunkX,
        int minChunkZ,
        int maxChunkX,
        int maxChunkZ,
        ServerPlayer player
    ) {
        var minX = Math.min(minChunkX, maxChunkX);
        var minZ = Math.min(minChunkZ, maxChunkZ);
        var maxX = Math.max(minChunkX, maxChunkX);
        var maxZ = Math.max(minChunkZ, maxChunkZ);
        var index = getOrCreateIndex(level);
        var entries = new ArrayList<S2CChunkClaimsSyncPayload.Entry>();

        for (var pos : index.getClaimedChunksInArea(minX, minZ, maxX, maxZ)) {
            var visibleFactions = index.getClaimants(pos)
                .stream()
                .filter(factionId -> isFactionVisibleToPlayer(factionId, player))
                .toList();

            if (!visibleFactions.isEmpty()) {
                entries.add(new S2CChunkClaimsSyncPayload.Entry(pos.x, pos.z, visibleFactions));
            }
        }

        BLib.MOD.networking()
            .sendToClient(
                player,
                S2CChunkClaimsSyncPayload.replaceArea(level.dimension().location(), minX, minZ, maxX, maxZ, entries)
            );
    }

    public void syncClaimsAroundPlayer(ServerPlayer player, int radiusChunks) {
        var center = player.chunkPosition();
        syncClaimsInAreaToPlayer(
            player.serverLevel(),
            center.x - radiusChunks,
            center.z - radiusChunks,
            center.x + radiusChunks,
            center.z + radiusChunks,
            player
        );
    }

    public void flushPendingClaimStoreSaves(MinecraftServer server) {
        if (pendingClaimStoreSaves.isEmpty()) {
            return;
        }

        var pending = new HashMap<ServerLevel, Set<ChunkPos>>();

        for (var entry : pendingClaimStoreSaves.entrySet()) {
            pending.put(entry.getKey(), Set.copyOf(entry.getValue()));
        }

        pendingClaimStoreSaves.clear();

        for (var entry : pending.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                BLibDataStoreManager.INSTANCE.saveChunkData(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean isFactionVisibleToPlayer(ResourceLocation factionId, ServerPlayer player) {
        var faction = BLibFactionManager.INSTANCE.get(factionId);

        if (faction == null) {
            return true;
        }

        var visibility = faction.claimVisibility();

        if (visibility == ClaimVisibility.PUBLIC) {
            return true;
        }

        if (visibility == ClaimVisibility.PRIVATE) {
            return isPlayerInFaction(player, factionId);
        }

        if (visibility == ClaimVisibility.ALLIED) {
            if (isPlayerInFaction(player, factionId)) {
                return true;
            }

            var playerFactionIds = BLibFactionManager.INSTANCE.getFactionIds(player.getUUID());

            for (var playerFactionId : playerFactionIds) {
                var state = BLibFactionManager.INSTANCE.getRelationship(playerFactionId, factionId);

                if (state == RelationshipState.ALLIED) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    private boolean isPlayerInFaction(ServerPlayer player, ResourceLocation factionId) {
        return BLibFactionManager.INSTANCE.getFactionIds(player.getUUID()).contains(factionId);
    }

    private BLibTerritoryIndex getOrCreateIndex(ServerLevel level) {
        return indexes.computeIfAbsent(level, $ -> new BLibTerritoryIndex());
    }

    private void markClaimStoreDirty(ServerLevel level, ChunkPos pos) {
        pendingClaimStoreSaves
            .computeIfAbsent(level, $ -> new HashSet<>())
            .add(pos);
    }

    private void rebuildIndexes(MinecraftServer server) {
        indexes.clear();

        for (var level : server.getAllLevels()) {
            BLibDataStoreManager.INSTANCE.forEachStoredChunk(
                level,
                BLibTerritoryDataStoreTypes.CHUNK_CLAIMS,
                (pos, store) -> {
                    var claimants = store.getClaimants();

                    if (!claimants.isEmpty()) {
                        getOrCreateIndex(level).onChunkLoaded(pos, claimants);
                    }
                }
            );
        }
    }

    private ChunkClaimDataStore getOrCreateStore(ServerLevel level, ChunkPos pos) {
        return BLibDataStoreManager.INSTANCE
            .getOrCreatePersistentChunk(level, pos, BLibTerritoryDataStoreTypes.CHUNK_CLAIMS)
            .inspect(store -> store.setContext(level, pos))
            .unwrapOr(null);
    }

    private static List<ChunkPos> getCardinalNeighbors(ChunkPos pos) {
        return List.of(
            new ChunkPos(pos.x, pos.z - 1),
            new ChunkPos(pos.x + 1, pos.z),
            new ChunkPos(pos.x, pos.z + 1),
            new ChunkPos(pos.x - 1, pos.z)
        );
    }
}
