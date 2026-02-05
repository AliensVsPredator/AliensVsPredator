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

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationManager;
import com.blib.api.common.reputation.v1.ReputationSubject;
import com.blib.internal.common.reputation.io.ReputationDataIO;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public class BLibReputationManager implements ReputationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibReputationManager.class);

    public static final BLibReputationManager INSTANCE = new BLibReputationManager();

    private static final int SHARD_SIZE = 1000;

    private final Map<ReputationSubject, ReputationData> data;

    private final Map<ReputationSubject, Set<ReputationSubject>> incomingIndex;

    private final ShardManager<ReputationSubject> shardManager;

    private BLibReputationManager() {
        this.data = new HashMap<>();
        this.incomingIndex = new HashMap<>();
        this.shardManager = new ShardManager<>(SHARD_SIZE);
    }

    @Override
    public ReputationData getOrCreate(ReputationSubject reputationSubject) {
        return data.computeIfAbsent(reputationSubject, $ -> {
            var reputationData = new ReputationData(reputationSubject);
            shardManager.assignShardIndex(reputationSubject);
            return reputationData;
        });
    }

    @Override
    public int getReputation(ReputationSubject from, ReputationSubject to) {
        var reputationData = data.get(from);
        return reputationData != null ? reputationData.get(to) : 0;
    }

    @Override
    public void setReputation(ReputationSubject from, ReputationSubject to, int value) {
        var reputationData = getOrCreate(from);
        var oldValue = reputationData.get(to);

        reputationData.set(to, value);

        if (oldValue == 0 && value != 0) {
            addToIncomingIndex(from, to);
        } else if (oldValue != 0 && value == 0) {
            removeFromIncomingIndex(from, to);
        }
    }

    @Override
    public void adjustReputation(ReputationSubject from, ReputationSubject to, int delta) {
        var current = getReputation(from, to);
        setReputation(from, to, current + delta);
    }

    @Override
    public void removeReputation(ReputationSubject from, ReputationSubject to) {
        var reputationData = data.get(from);

        if (reputationData != null) {
            var oldValue = reputationData.get(to);

            if (oldValue != 0) {
                reputationData.remove(to);
                removeFromIncomingIndex(from, to);
            }
        }
    }

    @Override
    public void removeSubject(ReputationSubject reputationSubject) {
        var removedData = data.remove(reputationSubject);

        if (removedData != null) {
            for (var target : removedData.getAll().keySet()) {
                removeFromIncomingIndex(reputationSubject, target);
            }

            shardManager.remove(reputationSubject);
        }

        var incomingFrom = incomingIndex.remove(reputationSubject);

        if (incomingFrom != null) {
            for (var from : incomingFrom) {
                var fromData = data.get(from);

                if (fromData != null) {
                    fromData.remove(reputationSubject);
                }
            }
        }
    }

    @Override
    public boolean exists(ReputationSubject reputationSubject) {
        return data.containsKey(reputationSubject);
    }

    public void load(MinecraftServer server) {
        data.clear();
        incomingIndex.clear();
        shardManager.clear();

        ReputationDataIO.loadAll(server, data, shardManager);

        for (var entry : data.entrySet()) {
            var from = entry.getKey();
            var reputationData = entry.getValue();

            for (var target : reputationData.getAll().keySet()) {
                addToIncomingIndex(from, target);
            }
        }

        LOGGER.info("Loaded {} reputation entries", data.size());
    }

    public void save(MinecraftServer server) {
        if (data.isEmpty()) {
            return;
        }

        Map<Integer, List<ReputationData>> shardToEntries = new HashMap<>();
        Set<Integer> dirtyShards = new HashSet<>();

        for (var entry : data.entrySet()) {
            var reputationSubject = entry.getKey();
            var reputationData = entry.getValue();
            var shardIndex = shardManager.getShardIndex(reputationSubject);

            shardToEntries.computeIfAbsent(shardIndex, k -> new ArrayList<>()).add(reputationData);

            if (reputationData.isDirty()) {
                dirtyShards.add(shardIndex);
            }
        }

        for (var shardIndex : dirtyShards) {
            var entriesInShard = shardToEntries.get(shardIndex);
            ReputationDataIO.saveShard(server, entriesInShard, shardIndex);
        }

        for (var reputationData : data.values()) {
            reputationData.clearDirty();
        }
    }

    public void clear(MinecraftServer server) {
        data.clear();
        incomingIndex.clear();
        shardManager.clear();
    }

    private void addToIncomingIndex(ReputationSubject from, ReputationSubject to) {
        incomingIndex.computeIfAbsent(to, $ -> new HashSet<>()).add(from);
    }

    private void removeFromIncomingIndex(ReputationSubject from, ReputationSubject to) {
        var set = incomingIndex.get(to);

        if (set != null) {
            set.remove(from);

            if (set.isEmpty()) {
                incomingIndex.remove(to);
            }
        }
    }
}
