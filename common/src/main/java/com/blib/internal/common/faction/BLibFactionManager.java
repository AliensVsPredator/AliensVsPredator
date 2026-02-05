package com.blib.internal.common.faction;

import com.just.core.functional.result.Result;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
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

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.faction.v1.FactionDataError;
import com.blib.api.common.faction.v1.FactionManager;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.FactionRelationships;
import com.blib.api.common.faction.v1.FactionType;
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

    private final Map<ResourceLocation, FactionData> data;

    private final Map<ResourceLocation, ResourceLocation> factionIdToTypeId;

    private final Map<ResourceLocation, FactionRelationships> relationships;

    private final FactionMemberIndex memberIndex;

    private final ShardManager<ResourceLocation> shardManager;

    private BLibFactionManager() {
        this.data = new HashMap<>();
        this.factionIdToTypeId = new HashMap<>();
        this.relationships = new HashMap<>();
        this.memberIndex = new FactionMemberIndex();
        this.shardManager = new ShardManager<>(SHARD_SIZE);
    }

    @Override
    public <T extends FactionData> Result<Tuple2<FactionRelationships, T>, FactionDataError> getOrCreate(
        ResourceLocation id,
        BLibHolder<FactionType<T>> type
    ) {
        var existingRel = relationships.get(id);

        if (existingRel != null) {
            return getData(id, type).map(data -> new Tuple2<>(existingRel, data));
        }

        var typeId = type.getResourceLocation();
        var factionType = type.value();

        var factionRelationships = new FactionRelationships(id);
        shardManager.assignShardIndex(id);
        factionRelationships.markDirty();

        var factionData = factionType.createInstance();
        factionData.markDirty();

        relationships.put(id, factionRelationships);
        data.put(id, factionData);
        factionIdToTypeId.put(id, typeId);

        return Result.ok(new Tuple2<>(factionRelationships, factionData));
    }

    public Result<FactionRelationships, FactionDataError> getOrCreateByTypeId(ResourceLocation id, ResourceLocation typeId) {
        var existingRel = relationships.get(id);

        if (existingRel != null) {
            return Result.ok(existingRel);
        }

        var factionType = BLibBuiltInRegistries.FACTION_TYPES.get(typeId);

        if (factionType == null) {
            return Result.err(new FactionDataError.UnknownType(id, null));
        }

        var factionRelationships = new FactionRelationships(id);
        shardManager.assignShardIndex(id);
        factionRelationships.markDirty();

        var factionData = factionType.createInstance();
        factionData.markDirty();

        relationships.put(id, factionRelationships);
        data.put(id, factionData);
        factionIdToTypeId.put(id, typeId);

        return Result.ok(factionRelationships);
    }

    @Override
    public FactionRelationships getRelationships(ResourceLocation id) {
        return relationships.computeIfAbsent(id, $ -> {
            var factionRelationships = new FactionRelationships(id);
            shardManager.assignShardIndex(id);
            return factionRelationships;
        });
    }

    @Override
    public <T extends FactionData> Result<T, FactionDataError> getData(ResourceLocation id, BLibHolder<FactionType<T>> type) {
        var storedTypeId = factionIdToTypeId.get(id);
        var factionData = data.get(id);

        if (storedTypeId == null) {
            return Result.err(new FactionDataError.UnknownType(id, factionData));
        }

        if (!storedTypeId.equals(type.getResourceLocation())) {
            return Result.err(new FactionDataError.TypeMismatch(type.getResourceLocation(), storedTypeId, factionData));
        }

        if (factionData == null) {
            factionData = type.get().createInstance();
            data.put(id, factionData);
        }

        @SuppressWarnings("unchecked")
        var typedFactionData = (T) factionData;

        return Result.ok(typedFactionData);
    }

    @Override
    public Set<ResourceLocation> getFactionIds(UUID entityUuid) {
        return memberIndex.getFactionIds(entityUuid);
    }

    @Override
    public Set<ResourceLocation> getParentFactionIds(ResourceLocation subfactionId) {
        return memberIndex.getParentFactionIds(subfactionId);
    }

    @Override
    public boolean remove(ResourceLocation id) {
        var removedRelationships = relationships.remove(id);

        if (removedRelationships != null) {
            memberIndex.removeFaction(id, removedRelationships);
            shardManager.remove(id);
        }

        data.remove(id);
        factionIdToTypeId.remove(id);

        if (removedRelationships != null) {
            for (var listener : BLibGlobalEvents.FACTION_REMOVE.listeners()) {
                listener.invoke(id);
            }
        }

        return removedRelationships != null;
    }

    @Override
    public Collection<ResourceLocation> getAllIds() {
        return Collections.unmodifiableCollection(relationships.keySet());
    }

    @Override
    public boolean exists(ResourceLocation id) {
        return relationships.containsKey(id);
    }

    public void load(MinecraftServer server) {
        relationships.clear();
        data.clear();
        factionIdToTypeId.clear();
        shardManager.clear();

        FactionRelationshipsIO.loadAll(server, relationships, shardManager);
        FactionDataIO.loadAll(server, relationships.keySet(), data, factionIdToTypeId);

        memberIndex.rebuild(relationships);

        LOGGER.info("Loaded {} factions", relationships.size());
    }

    public void save(MinecraftServer server) {
        if (relationships.isEmpty()) {
            return;
        }

        saveRelationships(server);
        saveData(server);
    }

    public void clear(MinecraftServer minecraftServer) {
        relationships.clear();
        data.clear();
        factionIdToTypeId.clear();
        memberIndex.clear();
        shardManager.clear();
    }

    public void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added) {
        memberIndex.onMemberChanged(factionId, member, added);
    }

    private void saveRelationships(MinecraftServer server) {
        Map<Integer, List<FactionRelationships>> shardToEntries = new HashMap<>();
        Set<Integer> dirtyShards = new HashSet<>();

        for (var entry : relationships.entrySet()) {
            var factionId = entry.getKey();
            var factionRelationships = entry.getValue();
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToEntries.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(factionRelationships);

            if (factionRelationships.isDirty()) {
                dirtyShards.add(shardIndex);
            }
        }

        for (var shardIndex : dirtyShards) {
            var entriesInShard = shardToEntries.get(shardIndex);
            FactionRelationshipsIO.saveShard(server, entriesInShard, shardIndex);
        }

        for (var factionRelationships : relationships.values()) {
            factionRelationships.clearDirty();
        }
    }

    private void saveData(MinecraftServer server) {
        Map<Integer, List<ResourceLocation>> shardToFactionIds = new HashMap<>();
        Set<Integer> dirtyShards = new HashSet<>();

        for (var factionId : relationships.keySet()) {
            var shardIndex = shardManager.getShardIndex(factionId);

            shardToFactionIds.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(factionId);

            var factionData = data.get(factionId);

            if (factionData != null && factionData.isDirty()) {
                dirtyShards.add(shardIndex);
            }
        }

        for (int shardIndex : dirtyShards) {
            var factionIdsInShard = shardToFactionIds.get(shardIndex);
            FactionDataIO.saveShard(server, factionIdsInShard, data, factionIdToTypeId, shardIndex);
        }

        for (var factionData : data.values()) {
            factionData.clearDirty();
        }
    }
}
