package com.blib.internal.common.faction;

import com.just.core.functional.result.Result;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.internal.common.faction.io.FactionDataIO;
import com.blib.internal.common.faction.io.FactionRelationshipsIO;
import com.blib.internal.common.util.ShardUtil;

@ApiStatus.Internal
public class BLibFactionManager implements FactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibFactionManager.class);

    public static final BLibFactionManager INSTANCE = new BLibFactionManager();

    private static final int SHARD_SIZE = 1000;

    private final Map<ResourceLocation, FactionData> data;

    private final Map<ResourceLocation, ResourceLocation> factionIdToTypeId;

    private final Map<ResourceLocation, FactionRelationships> relationships;

    private final Map<UUID, Set<ResourceLocation>> entityToFactions;

    private BLibFactionManager() {
        this.data = new LinkedHashMap<>();
        this.factionIdToTypeId = new LinkedHashMap<>();
        this.relationships = new LinkedHashMap<>();
        this.entityToFactions = new LinkedHashMap<>();
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
        attachListener(factionRelationships);
        factionRelationships.markDirty();

        var factionData = factionType.createInstance();
        factionData.markDirty();

        relationships.put(id, factionRelationships);
        data.put(id, factionData);
        factionIdToTypeId.put(id, typeId);

        return Result.ok(new Tuple2<>(factionRelationships, factionData));
    }

    @Override
    public FactionRelationships getRelationships(ResourceLocation id) {
        return relationships.computeIfAbsent(id, $ -> {
            var factionRelationships = new FactionRelationships(id);
            attachListener(factionRelationships);
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
        return Collections.unmodifiableSet(entityToFactions.getOrDefault(entityUuid, Set.of()));
    }

    @Override
    public boolean remove(ResourceLocation id) {
        var removedRelationships = relationships.remove(id);

        if (removedRelationships != null) {
            removeFactionFromEntityMembers(id, removedRelationships);
        }

        data.remove(id);
        factionIdToTypeId.remove(id);

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
        entityToFactions.clear();

        FactionRelationshipsIO.loadAll(server, relationships);
        FactionDataIO.loadAll(server, relationships.keySet(), data, factionIdToTypeId);

        for (var factionRelationships : relationships.values()) {
            attachListener(factionRelationships);

            for (var member : factionRelationships.getMembers()) {
                if (member instanceof FactionMember.Entity(var uuid)) {
                    entityToFactions.computeIfAbsent(uuid, $ -> new LinkedHashSet<>()).add(factionRelationships.getId());
                }
            }
        }

        LOGGER.info("Loaded {} factions", relationships.size());
    }

    public void save(MinecraftServer server) {
        if (relationships.isEmpty()) {
            return;
        }

        var idList = List.copyOf(relationships.keySet());

        saveRelationships(server, idList);
        saveData(server, idList);
    }

    public void clear(MinecraftServer minecraftServer) {
        relationships.clear();
        data.clear();
        factionIdToTypeId.clear();
        entityToFactions.clear();
    }

    private void saveRelationships(MinecraftServer server, List<@NotNull ResourceLocation> idList) {
        var dirtyRelShards = ShardUtil.findDirtyShards(idList, SHARD_SIZE, relationships::get);

        for (var shardIndex : dirtyRelShards) {
            FactionRelationshipsIO.saveShard(server, relationships, idList, shardIndex, SHARD_SIZE);
        }

        ShardUtil.clearAllDirty(relationships);
    }

    private void saveData(MinecraftServer server, List<@NotNull ResourceLocation> idList) {
        var dirtyDataShards = ShardUtil.findDirtyShards(idList, SHARD_SIZE, data::get);

        for (var shardIndex : dirtyDataShards) {
            FactionDataIO.saveShard(server, data, factionIdToTypeId, idList, shardIndex, SHARD_SIZE);
        }

        ShardUtil.clearAllDirty(data);
    }

    private void removeFactionFromEntityMembers(ResourceLocation id, FactionRelationships removedRelationships) {
        for (var member : removedRelationships.getMembers()) {
            if (member instanceof FactionMember.Entity(var entityUuid)) {
                var factions = entityToFactions.get(entityUuid);

                if (factions != null) {
                    factions.remove(id);

                    if (factions.isEmpty()) {
                        entityToFactions.remove(entityUuid);
                    }
                }
            }
        }

        removedRelationships.setMembershipListener(null);
    }

    private void onMemberChanged(ResourceLocation factionId, FactionMember member, boolean added) {
        if (member instanceof FactionMember.Entity(var uuid)) {
            if (added) {
                entityToFactions.computeIfAbsent(uuid, $ -> new LinkedHashSet<>()).add(factionId);
            } else {
                var factions = entityToFactions.get(uuid);

                if (factions != null) {
                    factions.remove(factionId);

                    if (factions.isEmpty()) {
                        entityToFactions.remove(uuid);
                    }
                }
            }
        }
    }

    private void attachListener(FactionRelationships factionRelationships) {
        factionRelationships.setMembershipListener(this::onMemberChanged);
    }
}
