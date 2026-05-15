package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.blib.api.common.faction.v1.Faction;
import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionDataType;
import com.blib.api.common.faction.v1.FactionManager;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.FactionMembership;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.common.entityreference.BLibEntityReferenceManager;
import com.blib.internal.common.entityreference.EntityReferenceOwner;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.faction.io.FactionDataIO;
import com.blib.internal.common.faction.io.FactionIO;
import com.blib.internal.common.faction.io.FactionMembershipIO;
import com.blib.internal.common.faction.serializer.FactionRelationshipTableSerializer;
import com.blib.internal.common.util.ShardManager;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CFactionDirectoryPayload;
import com.blib.mod.common.network.packet.S2CFactionInspectionPayload;
import com.blib.mod.common.network.packet.S2CFactionMembersPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;

@ApiStatus.Internal
public class BLibFactionManager implements FactionManager, EntityReferenceOwner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibFactionManager.class);

    public static final BLibFactionManager INSTANCE = new BLibFactionManager();

    private static final int SHARD_SIZE = 1000;

    private final Map<ResourceLocation, Faction<?>> factions;

    private final FactionMemberIndex memberIndex;

    private final FactionRelationshipTable relationshipTable;

    private final ShardManager<ResourceLocation> shardManager;

    /**
     * Set by every mutation that changes what the directory snapshot would carry — create / remove / relationship
     * change / member add or remove (member count is in the directory). Flushed by {@link #flushPendingPushes} on the
     * next server tick, which broadcasts the fresh snapshot and clears the flag. This is the catch-all for live updates
     * regardless of which code path triggered the mutation: C2S handler, mod-side {@code BLibFactionAccess}, undo/redo
     * via {@link com.blib.mod.common.gameplay.history.FactionEdit}, or anything else that goes through this class.
     */
    private volatile boolean directoryDirty;

    /**
     * Per-faction set of member rosters that need a fresh broadcast. Mutations to {@code FactionMembership.members}
     * route through {@link #onMemberChanged}, which adds the affected id here; the tick flush pushes a members snapshot
     * for each entry and clears the set.
     */
    private final Set<ResourceLocation> pendingMemberPushes = new HashSet<>();

    private BLibFactionManager() {
        this.factions = new HashMap<>();
        this.memberIndex = new FactionMemberIndex();
        this.relationshipTable = new FactionRelationshipTable();
        this.shardManager = new ShardManager<>(SHARD_SIZE);
    }

    /** Set the directory-dirty flag. Tick flush will see this and broadcast next tick. */
    public void markDirectoryDirty() {
        directoryDirty = true;
    }

    /**
     * Mark this faction's members roster as needing a broadcast. Also flips the directory-dirty flag because the member
     * count surfaces in the directory entry — clients reading either the Browser or the Members panel get a fresh view
     * in one tick.
     */
    public void markMembersDirty(ResourceLocation factionId) {
        pendingMemberPushes.add(factionId);
        directoryDirty = true;
    }

    /**
     * End-of-server-tick coalesced broadcast. Wired into the per-tick lifecycle by the BLib bootstrap. Skips when no
     * one's connected — building a snapshot only to drop it is wasted work. Members pushes pop the id out before the
     * push so a re-mark mid-broadcast just queues for the next tick.
     */
    public void flushPendingPushes(MinecraftServer server) {
        if (server == null || server.getPlayerCount() == 0) {
            return;
        }
        if (directoryDirty) {
            directoryDirty = false;
            pushDirectoryToAllClients(server);
        }
        if (!pendingMemberPushes.isEmpty()) {
            var ids = new ArrayList<>(pendingMemberPushes);
            pendingMemberPushes.clear();
            for (var id : ids) {
                pushMembersToAllClients(server, id);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FactionData> Faction<T> getOrCreate(ResourceLocation id, BLibHolder<FactionDataType<T>> type) {
        var existing = factions.get(id);

        if (existing != null) {
            return (Faction<T>) existing;
        }

        var typeId = type.getResourceLocation();
        var factionDataType = type.value();

        var relationships = new FactionMembership(id);
        shardManager.assignShardIndex(id);
        relationships.markDirty();

        var modData = factionDataType.createInstance();
        var defaultName = id.getPath();
        var internalData = new BLibFactionData(defaultName, BLibFactionData.randomColor(), modData);
        internalData.markDirty();

        @SuppressWarnings("unchecked")
        var faction = (Faction<T>) new Faction<>(id, typeId, relationships, internalData);
        factions.put(id, faction);
        markDirectoryDirty();

        return faction;
    }

    public @Nullable Faction<?> getOrCreateByTypeId(ResourceLocation id, ResourceLocation typeId) {
        var existing = factions.get(id);

        if (existing != null) {
            return existing;
        }

        var factionDataType = BLibBuiltInRegistries.FACTION_DATA_TYPES.get(typeId);

        if (factionDataType == null) {
            return null;
        }

        var relationships = new FactionMembership(id);
        shardManager.assignShardIndex(id);
        relationships.markDirty();

        var modData = factionDataType.createInstance();
        var defaultName = id.getPath();
        var internalData = new BLibFactionData(defaultName, BLibFactionData.randomColor(), modData);
        internalData.markDirty();

        var faction = new Faction<>(id, typeId, relationships, internalData);
        factions.put(id, faction);
        markDirectoryDirty();

        return faction;
    }

    @Override
    public @Nullable Faction<?> get(ResourceLocation id) {
        return factions.get(id);
    }

    @Override
    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return memberIndex.getFactionIds(entityUuid);
    }

    @Override
    public Set<ResourceLocation> getFactionsByTag(TagKey<FactionDataType<?>> tag) {
        return factions.values()
            .stream()
            .filter(faction -> faction.isType(tag))
            .map(Faction::id)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public RelationshipState getRelationship(ResourceLocation factionA, ResourceLocation factionB) {
        return relationshipTable.getRelationship(factionA, factionB);
    }

    @Override
    public void setRelationship(ResourceLocation factionA, ResourceLocation factionB, RelationshipState state) {
        relationshipTable.setRelationship(factionA, factionB, state);
        markDirectoryDirty();
    }

    @Override
    public Set<ResourceLocation> getFactionsWithState(ResourceLocation factionId, RelationshipState state) {
        return relationshipTable.getFactionsWithState(factionId, state);
    }

    @Override
    public boolean remove(ResourceLocation id) {
        var faction = factions.remove(id);

        if (faction == null) {
            return false;
        }

        memberIndex.removeFaction(id, faction.membership());
        relationshipTable.removeFaction(id);
        // shardManager.remove auto-marks the shard dirty so the next save rewrites the shard file without the
        // deleted entry. Otherwise the on-disk shard would keep the deleted faction's row and re-resurrect it on
        // the next world load.
        shardManager.remove(id);

        for (var listener : BLibGlobalEvents.FACTION_REMOVE.listeners()) {
            listener.invoke(id);
        }

        markDirectoryDirty();
        return true;
    }

    @Override
    public Collection<ResourceLocation> getAllIds() {
        return Collections.unmodifiableCollection(factions.keySet());
    }

    @Override
    public boolean exists(ResourceLocation id) {
        return factions.containsKey(id);
    }

    public void load(MinecraftServer server) {
        factions.clear();
        shardManager.clear();

        var relationships = new HashMap<ResourceLocation, FactionMembership>();
        var internalDataMap = new HashMap<ResourceLocation, BLibFactionData>();
        var factionIdToTypeId = new HashMap<ResourceLocation, ResourceLocation>();

        FactionMembershipIO.loadAll(server, relationships, shardManager);
        FactionDataIO.loadAll(server, relationships.keySet(), internalDataMap, factionIdToTypeId);

        for (var entry : relationships.entrySet()) {
            var factionId = entry.getKey();
            var rel = entry.getValue();
            var typeId = factionIdToTypeId.get(factionId);

            if (typeId == null) {
                LOGGER.warn("Faction '{}' has no type ID, skipping", factionId);
                continue;
            }

            var internalData = internalDataMap.getOrDefault(
                factionId,
                new BLibFactionData(factionId.getPath(), BLibFactionData.randomColor(), null)
            );

            factions.put(factionId, new Faction<>(factionId, typeId, rel, internalData));
        }

        memberIndex.rebuild(relationships);

        relationshipTable.clear();
        loadRelationshipTable(server);

        LOGGER.info("Loaded {} factions", factions.size());
    }

    public void save(MinecraftServer server) {
        // Don't short-circuit on empty factions — pending deletion-shards still need to be rewritten if the last
        // faction was just removed. Save sub-methods no-op when there's nothing to do.
        saveMemberships(server);
        saveData(server);
        saveRelationshipTable(server);
        shardManager.clearDirty();
    }

    public void clear(MinecraftServer minecraftServer) {
        factions.clear();
        memberIndex.clear();
        relationshipTable.clear();
        shardManager.clear();
    }

    public void syncAllFactionMetadataToPlayer(ServerPlayer player) {
        for (var faction : factions.values()) {
            var payload = new S2CFactionMetadataSyncPayload(faction.id(), faction.name(), faction.color());
            BLib.MOD.networking().sendToClient(player, payload);
        }
    }

    public void syncFactionMetadataToAllClients(MinecraftServer server, Faction<?> faction) {
        var payload = new S2CFactionMetadataSyncPayload(faction.id(), faction.name(), faction.color());
        BLib.MOD.networking().sendToAllClients(server, payload);
    }

    /**
     * Build the workspace directory snapshot — every faction's id/name/color/memberCount/typeId plus the full pairwise
     * relationship table. Consumed by {@code ClientFactionDirectoryCache} to drive the Faction Browser and Diplomacy
     * Matrix.
     */
    public S2CFactionDirectoryPayload buildDirectorySnapshot() {
        var entries = new ArrayList<S2CFactionDirectoryPayload.FactionEntry>(factions.size());
        for (var faction : factions.values()) {
            entries.add(
                new S2CFactionDirectoryPayload.FactionEntry(
                    faction.id(),
                    faction.name(),
                    faction.color(),
                    faction.membership().getMembers().size(),
                    faction.typeId()
                )
            );
        }
        var relEntries = new ArrayList<S2CFactionDirectoryPayload.RelationshipEntry>();
        for (var entry : relationshipTable.getAllEdges().entrySet()) {
            relEntries.add(
                new S2CFactionDirectoryPayload.RelationshipEntry(
                    entry.getKey().first(),
                    entry.getKey().second(),
                    entry.getValue().ordinal()
                )
            );
        }
        return new S2CFactionDirectoryPayload(entries, relEntries);
    }

    /**
     * Build the inspector snapshot for one faction — every editable scalar. Returns {@code null} if the faction id
     * doesn't resolve.
     */
    public @Nullable S2CFactionInspectionPayload buildInspectionSnapshot(ResourceLocation factionId) {
        var faction = factions.get(factionId);
        if (faction == null) {
            return null;
        }
        return new S2CFactionInspectionPayload(
            faction.id(),
            faction.name(),
            faction.color(),
            faction.typeId(),
            faction.claimVisibility(),
            faction.blockBreakProtection(),
            faction.blockInteractProtection(),
            faction.entityInteractProtection(),
            faction.nonLivingEntityAttackProtection(),
            faction.allowPvp(),
            faction.allowExplosions(),
            faction.allowMobGriefing()
        );
    }

    /**
     * Build the member roster for one faction. Player display names are resolved from the live {@code PlayerList};
     * non-player entities surface as empty-string display names (the client renders the UUID prefix instead). Returns
     * {@code null} when the faction id doesn't resolve.
     */
    public @Nullable S2CFactionMembersPayload buildMembersSnapshot(MinecraftServer server, ResourceLocation factionId) {
        var faction = factions.get(factionId);
        if (faction == null) {
            return null;
        }
        var entries = new ArrayList<S2CFactionMembersPayload.MemberEntry>();
        for (var member : faction.membership().getMembers()) {
            if (member instanceof FactionMember.Entity entityMember) {
                var uuid = entityMember.uuid();
                var displayName = "";
                var player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    displayName = player.getName().getString();
                }
                entries.add(new S2CFactionMembersPayload.MemberEntry(uuid, displayName));
            }
        }
        return new S2CFactionMembersPayload(factionId, entries);
    }

    public void pushDirectoryToAllClients(MinecraftServer server) {
        BLib.MOD.networking().sendToAllClients(server, buildDirectorySnapshot());
    }

    public void pushInspectionToAllClients(MinecraftServer server, ResourceLocation factionId) {
        var snapshot = buildInspectionSnapshot(factionId);
        if (snapshot != null) {
            BLib.MOD.networking().sendToAllClients(server, snapshot);
        }
    }

    public void pushMembersToAllClients(MinecraftServer server, ResourceLocation factionId) {
        var snapshot = buildMembersSnapshot(server, factionId);
        if (snapshot != null) {
            BLib.MOD.networking().sendToAllClients(server, snapshot);
        }
    }

    public @Nullable FactionData getRawModData(ResourceLocation factionId) {
        var faction = factions.get(factionId);

        return faction != null ? faction.data() : null;
    }

    public @Nullable FactionMembership getMembership(ResourceLocation id) {
        var faction = factions.get(id);

        return faction != null ? faction.membership() : null;
    }

    @Override
    public String id() {
        return "factions";
    }

    @Override
    public boolean referencesEntityUuid(UUID uuid) {
        return !memberIndex.getFactionIds(uuid).isEmpty();
    }

    @Override
    public Set<UUID> referencedEntityUuids() {
        var uuids = new HashSet<UUID>();
        for (var faction : factions.values()) {
            for (var member : faction.membership().getMembers()) {
                if (member instanceof FactionMember.Entity entityMember) {
                    uuids.add(entityMember.uuid());
                }
            }
        }
        return Set.copyOf(uuids);
    }

    @Override
    public void removeEntityReference(UUID uuid) {
        var factionIds = Set.copyOf(memberIndex.getFactionIds(uuid));
        if (factionIds.isEmpty()) {
            return;
        }

        var member = FactionMember.entity(uuid);
        for (var factionId : factionIds) {
            var faction = factions.get(factionId);
            if (faction != null) {
                faction.membership().removeMember(member);
            }
        }
    }

    public void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added) {
        memberIndex.onMemberChanged(factionId, member, added);

        var faction = factions.get(factionId);

        if (faction != null && faction.data() != null) {
            if (added) {
                faction.data().onMemberAdded(member);
            } else {
                faction.data().onMemberRemoved(member);
            }
        }

        if (!added && member instanceof FactionMember.Entity entityMember && memberIndex.getFactionIds(entityMember.uuid()).isEmpty()) {
            BLibEntityReferenceManager.INSTANCE.onEntityReferenceRemoved(entityMember.uuid());
        }

        markMembersDirty(factionId);
    }

    public void onEntityMemberAdded(
        ResourceLocation factionId,
        FactionMember member,
        Entity entity
    ) {
        memberIndex.onMemberChanged(factionId, member, true);
        BLibEntityReferenceManager.INSTANCE.onEntityReferenceAdded(entity);

        var faction = factions.get(factionId);

        if (faction != null && faction.data() != null) {
            faction.data().onMemberAdded(member, entity);
        }

        markMembersDirty(factionId);
    }

    private void saveMemberships(MinecraftServer server) {
        Map<Integer, List<FactionMembership>> shardToEntries = new HashMap<>();

        // Propagate per-membership dirty into shard-level dirty; build the per-shard survivor list along the way.
        for (var faction : factions.values()) {
            var factionId = faction.id();
            var relationships = faction.membership();
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToEntries.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(relationships);

            if (relationships.isDirty()) {
                shardManager.markDirty(factionId);
            }
        }

        var dirtyShards = List.copyOf(shardManager.dirtyShards());

        for (var shardIndex : dirtyShards) {
            // A shard with no surviving entries is still rewritten — the resulting empty file (or its deletion via
            // FactionIO.writeCompressed's empty-tag branch) is the whole point of the dirty mark on deletion.
            var entriesInShard = shardToEntries.getOrDefault(shardIndex, List.of());
            FactionMembershipIO.saveShard(server, entriesInShard, shardIndex);
        }

        for (var faction : factions.values()) {
            faction.membership().clearDirty();
        }
    }

    private void saveData(MinecraftServer server) {
        Map<Integer, List<ResourceLocation>> shardToFactionIds = new HashMap<>();
        Map<ResourceLocation, BLibFactionData> internalDataMap = new HashMap<>();
        Map<ResourceLocation, ResourceLocation> typeIdMap = new HashMap<>();

        for (var faction : factions.values()) {
            var factionId = faction.id();
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToFactionIds.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(factionId);

            var internalData = faction.internalData();
            internalDataMap.put(factionId, internalData);
            typeIdMap.put(factionId, faction.typeId());

            if (internalData.isDirty()) {
                shardManager.markDirty(factionId);
            }
        }

        var dirtyShards = List.copyOf(shardManager.dirtyShards());

        for (var shardIndex : dirtyShards) {
            // Shards with no surviving factions in any namespace are effectively skipped — FactionDataIO writes one
            // file per (namespace, shard) and iterates only namespaces with content. The stale on-disk file remains
            // for those, but the load filter (knownFactionIds from memberships) skips its entries, so they don't
            // resurrect.
            var factionIdsInShard = shardToFactionIds.getOrDefault(shardIndex, List.of());
            FactionDataIO.saveShard(server, factionIdsInShard, internalDataMap, typeIdMap, shardIndex);
        }

        for (var faction : factions.values()) {
            faction.internalData().clearDirty();
        }
    }

    private void loadRelationshipTable(MinecraftServer server) {
        var path = FactionIO.getRelationshipsPath(server);

        if (!Files.exists(path)) {
            return;
        }

        var tag = FactionIO.readCompressed(path);
        FactionRelationshipTableSerializer.deserialize(tag, relationshipTable);
    }

    private void saveRelationshipTable(MinecraftServer server) {
        if (!relationshipTable.isDirty()) {
            return;
        }

        var tag = FactionRelationshipTableSerializer.serialize(relationshipTable);
        FactionIO.writeCompressed(FactionIO.getRelationshipsPath(server), tag);
        relationshipTable.clearDirty();
    }
}
