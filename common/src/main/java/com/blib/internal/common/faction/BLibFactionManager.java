package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.blib.api.common.faction.v1.FactionRelationships;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.common.event.BLibGlobalEvents;
import com.blib.internal.common.faction.io.FactionDataIO;
import com.blib.internal.common.faction.io.FactionRelationshipsIO;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public class BLibFactionManager implements FactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibFactionManager.class);

    public static final BLibFactionManager INSTANCE = new BLibFactionManager();

    private static final int SHARD_SIZE = 1000;

    private final Map<ResourceLocation, Faction<?>> factions;

    private final FactionMemberIndex memberIndex;

    private final ShardManager<ResourceLocation> shardManager;

    private BLibFactionManager() {
        this.factions = new HashMap<>();
        this.memberIndex = new FactionMemberIndex();
        this.shardManager = new ShardManager<>(SHARD_SIZE);
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

        var relationships = new FactionRelationships(id);
        shardManager.assignShardIndex(id);
        relationships.markDirty();

        var factionData = factionDataType.createInstance();
        factionData.markDirty();

        @SuppressWarnings("unchecked")
        var faction = (Faction<T>) new Faction<>(id, typeId, relationships, factionData);
        factions.put(id, faction);

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

        var relationships = new FactionRelationships(id);
        shardManager.assignShardIndex(id);
        relationships.markDirty();

        var factionData = factionDataType.createInstance();
        factionData.markDirty();

        var faction = new Faction<>(id, typeId, relationships, factionData);
        factions.put(id, faction);

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
    public boolean remove(ResourceLocation id) {
        var faction = factions.remove(id);

        if (faction == null) {
            return false;
        }

        memberIndex.removeFaction(id, faction.relationships());
        shardManager.remove(id);

        for (var listener : BLibGlobalEvents.FACTION_REMOVE.listeners()) {
            listener.invoke(id);
        }

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

        var relationships = new HashMap<ResourceLocation, FactionRelationships>();
        var data = new HashMap<ResourceLocation, FactionData>();
        var factionIdToTypeId = new HashMap<ResourceLocation, ResourceLocation>();

        FactionRelationshipsIO.loadAll(server, relationships, shardManager);
        FactionDataIO.loadAll(server, relationships.keySet(), data, factionIdToTypeId);

        for (var entry : relationships.entrySet()) {
            var factionId = entry.getKey();
            var rel = entry.getValue();
            var typeId = factionIdToTypeId.get(factionId);
            var factionData = data.get(factionId);

            if (typeId == null) {
                LOGGER.warn("Faction '{}' has no type ID, skipping", factionId);
                continue;
            }

            factions.put(factionId, new Faction<>(factionId, typeId, rel, factionData));
        }

        memberIndex.rebuild(relationships);

        LOGGER.info("Loaded {} factions", factions.size());
    }

    public void save(MinecraftServer server) {
        if (factions.isEmpty()) {
            return;
        }

        saveRelationships(server);
        saveData(server);
    }

    public void clear(MinecraftServer minecraftServer) {
        factions.clear();
        memberIndex.clear();
        shardManager.clear();
    }

    public @Nullable FactionData getRawData(ResourceLocation factionId) {
        var faction = factions.get(factionId);

        return faction != null ? faction.data() : null;
    }

    public FactionRelationships getRelationships(ResourceLocation id) {
        var faction = factions.get(id);

        if (faction != null) {
            return faction.relationships();
        }

        var relationships = new FactionRelationships(id);
        shardManager.assignShardIndex(id);

        var newFaction = new Faction<>(id, null, relationships, null);
        factions.put(id, newFaction);

        return relationships;
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
    }

    public void onEntityMemberAdded(
        ResourceLocation factionId,
        FactionMember member,
        net.minecraft.world.entity.Entity entity
    ) {
        memberIndex.onMemberChanged(factionId, member, true);

        var faction = factions.get(factionId);

        if (faction != null && faction.data() != null) {
            faction.data().onMemberAdded(member, entity);
        }
    }

    private void saveRelationships(MinecraftServer server) {
        Map<Integer, List<FactionRelationships>> shardToEntries = new HashMap<>();
        Set<Integer> dirtyShards = new HashSet<>();

        for (var faction : factions.values()) {
            var factionId = faction.id();
            var relationships = faction.relationships();
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToEntries.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(relationships);

            if (relationships.isDirty()) {
                dirtyShards.add(shardIndex);
            }
        }

        for (var shardIndex : dirtyShards) {
            var entriesInShard = shardToEntries.get(shardIndex);
            FactionRelationshipsIO.saveShard(server, entriesInShard, shardIndex);
        }

        for (var faction : factions.values()) {
            faction.relationships().clearDirty();
        }
    }

    private void saveData(MinecraftServer server) {
        Map<Integer, List<ResourceLocation>> shardToFactionIds = new HashMap<>();
        Map<ResourceLocation, FactionData> dataMap = new HashMap<>();
        Map<ResourceLocation, ResourceLocation> typeIdMap = new HashMap<>();
        Set<Integer> dirtyShards = new HashSet<>();

        for (var faction : factions.values()) {
            var factionId = faction.id();
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToFactionIds.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(factionId);

            if (faction.data() != null) {
                dataMap.put(factionId, faction.data());
                typeIdMap.put(factionId, faction.typeId());

                if (faction.data().isDirty()) {
                    dirtyShards.add(shardIndex);
                }
            }
        }

        for (var shardIndex : dirtyShards) {
            var factionIdsInShard = shardToFactionIds.get(shardIndex);
            FactionDataIO.saveShard(server, factionIdsInShard, dataMap, typeIdMap, shardIndex);
        }

        for (var faction : factions.values()) {
            if (faction.data() != null) {
                faction.data().clearDirty();
            }
        }
    }
}
