package com.blib.internal.common.reputation;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.api.common.reputation.v1.ReputationManager;
import com.blib.internal.common.entityreference.EntityReferenceOwner;
import com.blib.internal.common.reputation.io.ReputationDataIO;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public class BLibReputationManager implements ReputationManager, EntityReferenceOwner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibReputationManager.class);

    public static final BLibReputationManager INSTANCE = new BLibReputationManager();

    private static final int SHARD_SIZE = 1000;

    private final Map<ReputationKey, ReputationData> data;

    private final ReputationIndex reputationIndex;

    private final ShardManager<ReputationKey> shardManager;

    private BLibReputationManager() {
        this.data = new HashMap<>();
        this.reputationIndex = new ReputationIndex();
        this.shardManager = new ShardManager<>(SHARD_SIZE);
    }

    @Override
    public ReputationData getOrCreate(ReputationKey reputationKey) {
        return data.computeIfAbsent(reputationKey, $ -> {
            var reputationData = new ReputationData(reputationKey);
            shardManager.assignShardIndex(reputationKey);
            return reputationData;
        });
    }

    @Override
    public int getReputation(ReputationKey from, ReputationKey to) {
        var reputationData = data.get(from);
        return reputationData != null ? reputationData.get(to) : 0;
    }

    @Override
    public void setReputation(ReputationKey from, ReputationKey to, int value) {
        var reputationData = getOrCreate(from);
        var oldValue = reputationData.get(to);

        reputationData.set(to, value);

        if (oldValue == 0 && value != 0) {
            reputationIndex.add(from, to);
        } else if (oldValue != 0 && value == 0) {
            reputationIndex.remove(from, to);
        }
    }

    @Override
    public void adjustReputation(ReputationKey from, ReputationKey to, int delta) {
        var current = getReputation(from, to);
        setReputation(from, to, current + delta);
    }

    @Override
    public void removeReputation(ReputationKey from, ReputationKey to) {
        var reputationData = data.get(from);

        if (reputationData != null) {
            var oldValue = reputationData.get(to);

            if (oldValue != 0) {
                reputationData.remove(to);
                reputationIndex.remove(from, to);
            }
        }
    }

    @Override
    public void removeReputation(ReputationKey reputationKey) {
        var removedData = data.remove(reputationKey);

        if (removedData != null) {
            for (var target : removedData.getAll().keySet()) {
                reputationIndex.remove(reputationKey, target);
            }

            shardManager.remove(reputationKey);
        }

        var incomingFrom = reputationIndex.removeAll(reputationKey);

        if (incomingFrom != null) {
            for (var from : incomingFrom) {
                var fromData = data.get(from);

                if (fromData != null) {
                    fromData.remove(reputationKey);
                }
            }
        }
    }

    @Override
    public boolean exists(ReputationKey reputationKey) {
        return data.containsKey(reputationKey);
    }

    @Override
    public String id() {
        return "reputations";
    }

    @Override
    public boolean referencesEntityUuid(UUID uuid) {
        var key = ReputationKey.entity(uuid);
        return data.containsKey(key) || reputationIndex.hasIncoming(key);
    }

    @Override
    public Set<UUID> referencedEntityUuids() {
        var uuids = new HashSet<UUID>();
        for (var entry : data.entrySet()) {
            collectEntityUuid(entry.getKey(), uuids);
            for (var target : entry.getValue().getAll().keySet()) {
                collectEntityUuid(target, uuids);
            }
        }
        return Set.copyOf(uuids);
    }

    @Override
    public void removeEntityReference(UUID uuid) {
        removeReputation(ReputationKey.entity(uuid));
    }

    public void load(MinecraftServer server) {
        data.clear();
        reputationIndex.clear();
        shardManager.clear();

        ReputationDataIO.loadAll(server, data, shardManager);
        reputationIndex.rebuild(data);

        LOGGER.info("Loaded {} reputation entries", data.size());
    }

    public void save(MinecraftServer server) {
        // No empty short-circuit: a session that deleted every entry still needs to flush the deletion-dirty shards
        // so their files get rewritten (or removed via ReputationIO's empty-tag → delete path). Otherwise the next
        // load would resurrect everything from disk.
        Map<Integer, List<ReputationData>> shardToEntries = new HashMap<>();

        for (var entry : data.entrySet()) {
            var reputationKey = entry.getKey();
            var reputationData = entry.getValue();
            var shardIndex = shardManager.getShardIndex(reputationKey);

            shardToEntries.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(reputationData);

            if (reputationData.isDirty()) {
                shardManager.markDirty(reputationKey);
            }
        }

        for (var shardIndex : shardManager.dirtyShards()) {
            // Shards with no surviving entries still get written — saveShard produces an empty rootTag in that case
            // and ReputationIO.writeCompressed deletes the file, ensuring deleted entries don't survive on disk.
            var entriesInShard = shardToEntries.getOrDefault(shardIndex, List.of());
            ReputationDataIO.saveShard(server, entriesInShard, shardIndex);
        }

        for (var reputationData : data.values()) {
            reputationData.clearDirty();
        }
        shardManager.clearDirty();
    }

    public void clear(MinecraftServer server) {
        data.clear();
        reputationIndex.clear();
        shardManager.clear();
    }

    private static void collectEntityUuid(ReputationKey key, Set<UUID> uuids) {
        if (key instanceof ReputationKey.Entity entityKey) {
            uuids.add(entityKey.uuid());
        }
    }

}
