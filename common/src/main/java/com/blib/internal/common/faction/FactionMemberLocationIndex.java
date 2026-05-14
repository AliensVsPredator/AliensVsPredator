package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Tracks where non-player entity faction members were last observed. This lets BLib reconcile stale persisted UUID
 * memberships after crashes: once the member's last-seen chunk has loaded and the entity is still absent, the membership
 * is a strong orphan candidate.
 */
public final class FactionMemberLocationIndex {

    private static final long VALIDATION_DELAY_TICKS = 40L;

    private static final long LOADED_MEMBER_REFRESH_INTERVAL_TICKS = 20L;

    private static final int REQUIRED_MISSING_CONFIRMATIONS = 2;

    private final Map<UUID, FactionMemberLastSeen> lastSeenByUuid = new HashMap<>();

    private final Map<ChunkKey, Set<UUID>> uuidsByChunk = new HashMap<>();

    private final Map<ChunkKey, Long> pendingValidationTicks = new HashMap<>();

    private final Map<UUID, Integer> missingConfirmations = new HashMap<>();

    private final Set<UUID> loadedTrackedMembers = new HashSet<>();

    private long nextLoadedMemberRefreshTick;

    private boolean dirty;

    public void load(Collection<FactionMemberLastSeen> entries) {
        clear();
        for (var entry : entries) {
            putLastSeen(entry);
        }
        dirty = false;
    }

    public List<FactionMemberLastSeen> snapshot() {
        return List.copyOf(lastSeenByUuid.values());
    }

    public void retainMembers(Set<UUID> memberUuids) {
        for (var uuid : new ArrayList<>(lastSeenByUuid.keySet())) {
            if (!memberUuids.contains(uuid)) {
                forget(uuid);
            }
        }
    }

    public void recordLoadedMember(Entity entity, long tick) {
        if (entity instanceof Player) {
            return;
        }

        loadedTrackedMembers.add(entity.getUUID());
        recordLastSeen(entity, tick);
    }

    public void recordUnloadedMember(Entity entity, long tick) {
        if (entity instanceof Player) {
            return;
        }

        loadedTrackedMembers.remove(entity.getUUID());
        recordLastSeen(entity, tick);
    }

    public void forget(UUID uuid) {
        var previous = lastSeenByUuid.remove(uuid);
        if (previous != null) {
            var previousKey = ChunkKey.of(previous);
            var members = uuidsByChunk.get(previousKey);
            if (members != null) {
                members.remove(uuid);
                if (members.isEmpty()) {
                    uuidsByChunk.remove(previousKey);
                }
            }
            dirty = true;
        }
        loadedTrackedMembers.remove(uuid);
        missingConfirmations.remove(uuid);
    }

    public void queueChunkValidation(ServerLevel level, LevelChunk chunk) {
        var key = ChunkKey.of(level.dimension(), chunk.getPos());
        if (!uuidsByChunk.containsKey(key)) {
            return;
        }

        pendingValidationTicks.merge(key, level.getServer().getTickCount() + VALIDATION_DELAY_TICKS, Math::min);
    }

    public List<UUID> collectStaleMembers(
        MinecraftServer server,
        long currentTick,
        Function<UUID, Set<net.minecraft.resources.ResourceLocation>> factionLookup
    ) {
        refreshLoadedMemberPositions(server, currentTick, factionLookup);

        var stale = new ArrayList<UUID>();
        for (var entry : new ArrayList<>(pendingValidationTicks.entrySet())) {
            if (entry.getValue() > currentTick) {
                continue;
            }

            pendingValidationTicks.remove(entry.getKey());
            validateChunk(server, currentTick, entry.getKey(), factionLookup, stale);
        }
        return stale;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void clear() {
        lastSeenByUuid.clear();
        uuidsByChunk.clear();
        pendingValidationTicks.clear();
        missingConfirmations.clear();
        loadedTrackedMembers.clear();
        nextLoadedMemberRefreshTick = 0L;
        dirty = false;
    }

    private void refreshLoadedMemberPositions(
        MinecraftServer server,
        long currentTick,
        Function<UUID, Set<net.minecraft.resources.ResourceLocation>> factionLookup
    ) {
        if (currentTick < nextLoadedMemberRefreshTick) {
            return;
        }
        nextLoadedMemberRefreshTick = currentTick + LOADED_MEMBER_REFRESH_INTERVAL_TICKS;

        for (var uuid : new ArrayList<>(loadedTrackedMembers)) {
            if (factionLookup.apply(uuid).isEmpty()) {
                forget(uuid);
                continue;
            }

            var entity = findLoadedEntity(server, uuid);
            if (entity == null || entity instanceof Player) {
                loadedTrackedMembers.remove(uuid);
                continue;
            }

            recordLastSeen(entity, currentTick);
        }
    }

    private void validateChunk(
        MinecraftServer server,
        long currentTick,
        ChunkKey key,
        Function<UUID, Set<net.minecraft.resources.ResourceLocation>> factionLookup,
        List<UUID> stale
    ) {
        var level = server.getLevel(key.dimension());
        if (level == null || !level.getChunkSource().hasChunk(key.chunkX(), key.chunkZ())) {
            return;
        }

        var candidates = uuidsByChunk.getOrDefault(key, Collections.emptySet());
        for (var uuid : new ArrayList<>(candidates)) {
            var lastSeen = lastSeenByUuid.get(uuid);
            if (lastSeen == null || !key.equals(ChunkKey.of(lastSeen))) {
                continue;
            }

            if (factionLookup.apply(uuid).isEmpty()) {
                forget(uuid);
                continue;
            }

            var entity = findLoadedEntity(server, uuid);
            if (entity != null) {
                if (!(entity instanceof Player)) {
                    recordLoadedMember(entity, currentTick);
                }
                missingConfirmations.remove(uuid);
                continue;
            }

            var confirmations = missingConfirmations.merge(uuid, 1, Integer::sum);
            if (confirmations >= REQUIRED_MISSING_CONFIRMATIONS) {
                stale.add(uuid);
            } else {
                pendingValidationTicks.put(key, currentTick + VALIDATION_DELAY_TICKS);
            }
        }
    }

    private void recordLastSeen(Entity entity, long tick) {
        recordLastSeen(
            entity.getUUID(),
            entity.level().dimension(),
            new ChunkPos(entity.blockPosition()),
            tick
        );
    }

    private void recordLastSeen(UUID uuid, ResourceKey<Level> dimension, ChunkPos chunk, long tick) {
        putLastSeen(new FactionMemberLastSeen(uuid, dimension, chunk.x, chunk.z, tick));
        missingConfirmations.remove(uuid);
        dirty = true;
    }

    private void putLastSeen(FactionMemberLastSeen entry) {
        var previous = lastSeenByUuid.put(entry.uuid(), entry);
        if (previous != null) {
            var previousKey = ChunkKey.of(previous);
            var previousMembers = uuidsByChunk.get(previousKey);
            if (previousMembers != null) {
                previousMembers.remove(entry.uuid());
                if (previousMembers.isEmpty()) {
                    uuidsByChunk.remove(previousKey);
                }
            }
        }
        uuidsByChunk.computeIfAbsent(ChunkKey.of(entry), $ -> new HashSet<>()).add(entry.uuid());
    }

    private static Entity findLoadedEntity(MinecraftServer server, UUID uuid) {
        for (var level : server.getAllLevels()) {
            var entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    private record ChunkKey(
        ResourceKey<Level> dimension,
        int chunkX,
        int chunkZ
    ) {

        static ChunkKey of(FactionMemberLastSeen lastSeen) {
            return new ChunkKey(lastSeen.dimension(), lastSeen.chunkX(), lastSeen.chunkZ());
        }

        static ChunkKey of(ResourceKey<Level> dimension, ChunkPos chunkPos) {
            return new ChunkKey(dimension, chunkPos.x, chunkPos.z);
        }
    }
}
