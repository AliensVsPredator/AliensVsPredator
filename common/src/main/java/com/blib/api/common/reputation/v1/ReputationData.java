package com.blib.api.common.reputation.v1;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.blib.api.common.util.v1.Dirty;

public class ReputationData implements Dirty {

    private final ReputationKey reputationKey;

    private final Map<ReputationKey, Integer> reputations;

    private boolean dirty;

    public ReputationData(ReputationKey reputationKey) {
        this.reputationKey = reputationKey;
        this.reputations = new HashMap<>();
    }

    public ReputationKey getSubject() {
        return reputationKey;
    }

    public int get(ReputationKey target) {
        return reputations.getOrDefault(target, 0);
    }

    public void set(ReputationKey target, int value) {
        if (value == 0) {
            remove(target);
        } else {
            reputations.put(target, value);
            markDirty();
        }
    }

    public void adjust(ReputationKey target, int delta) {
        int current = get(target);
        set(target, current + delta);
    }

    public void remove(ReputationKey target) {
        if (reputations.remove(target) != null) {
            markDirty();
        }
    }

    public Map<ReputationKey, Integer> getAll() {
        return Collections.unmodifiableMap(reputations);
    }

    @ApiStatus.Internal
    public Map<ReputationKey, Integer> getReputationsInternal() {
        return reputations;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    @ApiStatus.Internal
    public void clearDirty() {
        dirty = false;
    }
}
