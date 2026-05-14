package com.blib.internal.common.entityreference;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ApiStatus.Internal
public final class BLibEntityReferenceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibEntityReferenceManager.class);

    public static final BLibEntityReferenceManager INSTANCE = new BLibEntityReferenceManager();

    private static final long VALIDATION_DELAY_TICKS = 40L;

    private static final long LOADED_ENTITY_REFRESH_INTERVAL_TICKS = 20L;

    private static final long REFERENCE_RETENTION_INTERVAL_TICKS = 100L;

    private static final int REQUIRED_MISSING_CONFIRMATIONS = 2;

    private final Map<String, EntityReferenceOwner> owners = new LinkedHashMap<>();

    private final Map<UUID, EntityLastSeen> lastSeenByUuid = new HashMap<>();

    private final Map<ChunkKey, Set<UUID>> uuidsByChunk = new HashMap<>();

    private final Map<ChunkKey, Long> pendingValidationTicks = new HashMap<>();

    private final Map<UUID, Integer> missingConfirmations = new HashMap<>();

    private final Set<UUID> loadedTrackedEntities = new HashSet<>();

    private long nextLoadedEntityRefreshTick;

    private long nextReferenceRetentionTick;

    private boolean dirty;

    private BLibEntityReferenceManager() {}

    public void registerOwner(EntityReferenceOwner owner) {
        owners.put(owner.id(), owner);
    }

    public void load(MinecraftServer server) {
        load(EntityReferenceIO.load(server));
        retainReferencedEntities();
        if (EntityReferenceIO.hasLegacyData(server)) {
            dirty = true;
        }
    }

    public void save(MinecraftServer server) {
        if (!dirty) {
            return;
        }

        EntityReferenceIO.save(server, snapshot());
        dirty = false;
    }

    public void clear(MinecraftServer server) {
        clearTracking();
    }

    public void tick(MinecraftServer server) {
        if (server == null) {
            return;
        }

        var currentTick = server.getTickCount();
        if (currentTick >= nextReferenceRetentionTick) {
            retainReferencedEntities();
            nextReferenceRetentionTick = currentTick + REFERENCE_RETENTION_INTERVAL_TICKS;
        }

        refreshLoadedEntityPositions(server, currentTick);
        validateQueuedChunks(server, currentTick);
    }

    public void onEntityReferenceAdded(Entity entity) {
        if (entity instanceof Player || !isReferenced(entity.getUUID())) {
            return;
        }

        recordLoadedEntity(entity, currentServerTick(entity));
    }

    public void onEntityReferenceRemoved(UUID uuid) {
        if (!isReferenced(uuid)) {
            forget(uuid);
        }
    }

    public void removeEntityReferences(UUID uuid) {
        removeFromOwners(uuid, false);
        forget(uuid);
    }

    public void onEntityLoaded(Entity entity) {
        if (entity instanceof Player || !isReferenced(entity.getUUID())) {
            return;
        }

        recordLoadedEntity(entity, currentServerTick(entity));
    }

    public void onEntityUnloaded(Entity entity) {
        if (entity instanceof Player) {
            return;
        }

        if (isReferenced(entity.getUUID())) {
            recordUnloadedEntity(entity, currentServerTick(entity));
        } else {
            loadedTrackedEntities.remove(entity.getUUID());
        }
    }

    public void onChunkLoaded(ServerLevel level, LevelChunk chunk) {
        var key = ChunkKey.of(level.dimension(), chunk.getPos());
        if (!uuidsByChunk.containsKey(key)) {
            return;
        }

        pendingValidationTicks.merge(key, level.getServer().getTickCount() + VALIDATION_DELAY_TICKS, Math::min);
    }

    private void load(Collection<EntityLastSeen> entries) {
        clearTracking();
        for (var entry : entries) {
            putLastSeen(entry);
        }
        dirty = false;
    }

    private List<EntityLastSeen> snapshot() {
        return List.copyOf(lastSeenByUuid.values());
    }

    private void retainReferencedEntities() {
        var references = collectReferencedEntityUuids();
        for (var uuid : new ArrayList<>(lastSeenByUuid.keySet())) {
            if (!references.contains(uuid)) {
                forget(uuid);
            }
        }
    }

    private void recordLoadedEntity(Entity entity, long tick) {
        loadedTrackedEntities.add(entity.getUUID());
        recordLastSeen(entity, tick);
    }

    private void recordUnloadedEntity(Entity entity, long tick) {
        loadedTrackedEntities.remove(entity.getUUID());
        recordLastSeen(entity, tick);
    }

    private void forget(UUID uuid) {
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
        loadedTrackedEntities.remove(uuid);
        missingConfirmations.remove(uuid);
    }

    private void validateQueuedChunks(MinecraftServer server, long currentTick) {
        for (var entry : new ArrayList<>(pendingValidationTicks.entrySet())) {
            if (entry.getValue() > currentTick) {
                continue;
            }

            pendingValidationTicks.remove(entry.getKey());
            validateChunk(server, currentTick, entry.getKey());
        }
    }

    private void refreshLoadedEntityPositions(MinecraftServer server, long currentTick) {
        if (currentTick < nextLoadedEntityRefreshTick) {
            return;
        }
        nextLoadedEntityRefreshTick = currentTick + LOADED_ENTITY_REFRESH_INTERVAL_TICKS;

        for (var uuid : new ArrayList<>(loadedTrackedEntities)) {
            if (!isReferenced(uuid)) {
                forget(uuid);
                continue;
            }

            var entity = findLoadedEntity(server, uuid);
            if (entity == null || entity instanceof Player) {
                loadedTrackedEntities.remove(uuid);
                continue;
            }

            recordLastSeen(entity, currentTick);
        }
    }

    private void validateChunk(MinecraftServer server, long currentTick, ChunkKey key) {
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

            if (!isReferenced(uuid)) {
                forget(uuid);
                continue;
            }

            var entity = findLoadedEntity(server, uuid);
            if (entity != null) {
                if (!(entity instanceof Player)) {
                    recordLoadedEntity(entity, currentTick);
                }
                missingConfirmations.remove(uuid);
                continue;
            }

            var confirmations = missingConfirmations.merge(uuid, 1, Integer::sum);
            if (confirmations >= REQUIRED_MISSING_CONFIRMATIONS) {
                pruneStaleReference(uuid);
            } else {
                pendingValidationTicks.put(key, currentTick + VALIDATION_DELAY_TICKS);
            }
        }
    }

    private void pruneStaleReference(UUID uuid) {
        var ownerIds = removeFromOwners(uuid, true);
        forget(uuid);

        if (!ownerIds.isEmpty()) {
            LOGGER.warn(
                "Pruned stale entity reference {} from owner(s) {} after its last-seen chunk loaded without the entity.",
                uuid,
                String.join(", ", ownerIds)
            );
        }
    }

    private List<String> removeFromOwners(UUID uuid, boolean stale) {
        var ownerIds = new ArrayList<String>();
        for (var owner : owners.values()) {
            if (!owner.referencesEntityUuid(uuid)) {
                continue;
            }

            owner.removeEntityReference(uuid);
            ownerIds.add(owner.id());
        }

        if (!stale && !ownerIds.isEmpty()) {
            LOGGER.debug("Removed entity reference {} from owner(s) {}", uuid, String.join(", ", ownerIds));
        }

        return ownerIds;
    }

    private boolean isReferenced(UUID uuid) {
        for (var owner : owners.values()) {
            if (owner.referencesEntityUuid(uuid)) {
                return true;
            }
        }
        return false;
    }

    private Set<UUID> collectReferencedEntityUuids() {
        var uuids = new HashSet<UUID>();
        for (var owner : owners.values()) {
            uuids.addAll(owner.referencedEntityUuids());
        }
        return uuids;
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
        putLastSeen(new EntityLastSeen(uuid, dimension, chunk.x, chunk.z, tick));
        missingConfirmations.remove(uuid);
        dirty = true;
    }

    private void putLastSeen(EntityLastSeen entry) {
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

    private void clearTracking() {
        lastSeenByUuid.clear();
        uuidsByChunk.clear();
        pendingValidationTicks.clear();
        missingConfirmations.clear();
        loadedTrackedEntities.clear();
        nextLoadedEntityRefreshTick = 0L;
        nextReferenceRetentionTick = 0L;
        dirty = false;
    }

    private static long currentServerTick(Entity entity) {
        var server = entity.getServer();
        return server != null ? server.getTickCount() : 0L;
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

        static ChunkKey of(EntityLastSeen lastSeen) {
            return new ChunkKey(lastSeen.dimension(), lastSeen.chunkX(), lastSeen.chunkZ());
        }

        static ChunkKey of(ResourceKey<Level> dimension, ChunkPos chunkPos) {
            return new ChunkKey(dimension, chunkPos.x, chunkPos.z);
        }
    }
}
