package com.blib.internal.common.reputation;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;

@ApiStatus.Internal
public class ReputationIndex {

    private final Map<ReputationKey, Set<ReputationKey>> incomingIndex;

    public ReputationIndex() {
        this.incomingIndex = new HashMap<>();
    }

    public void add(ReputationKey from, ReputationKey to) {
        incomingIndex.computeIfAbsent(to, $ -> new HashSet<>()).add(from);
    }

    public void remove(ReputationKey from, ReputationKey to) {
        var set = incomingIndex.get(to);

        if (set != null) {
            set.remove(from);

            if (set.isEmpty()) {
                incomingIndex.remove(to);
            }
        }
    }

    public Set<ReputationKey> removeAll(ReputationKey target) {
        return incomingIndex.remove(target);
    }

    public boolean hasIncoming(ReputationKey target) {
        var set = incomingIndex.get(target);
        return set != null && !set.isEmpty();
    }

    public void rebuild(Map<ReputationKey, ReputationData> data) {
        incomingIndex.clear();

        for (var entry : data.entrySet()) {
            var from = entry.getKey();

            for (var to : entry.getValue().getAll().keySet()) {
                add(from, to);
            }
        }
    }

    public void clear() {
        incomingIndex.clear();
    }
}
