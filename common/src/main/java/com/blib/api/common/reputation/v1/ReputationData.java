package com.blib.api.common.reputation.v1;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.blib.api.common.util.v1.Dirty;

public class ReputationData implements Dirty {

    private final ReputationSubject reputationSubject;

    private final Map<ReputationSubject, Integer> reputations;

    private boolean dirty;

    public ReputationData(ReputationSubject reputationSubject) {
        this.reputationSubject = reputationSubject;
        this.reputations = new HashMap<>();
    }

    public ReputationSubject getSubject() {
        return reputationSubject;
    }

    public int get(ReputationSubject target) {
        return reputations.getOrDefault(target, 0);
    }

    public void set(ReputationSubject target, int value) {
        if (value == 0) {
            remove(target);
        } else {
            reputations.put(target, value);
            markDirty();
        }
    }

    public void adjust(ReputationSubject target, int delta) {
        int current = get(target);
        set(target, current + delta);
    }

    public void remove(ReputationSubject target) {
        if (reputations.remove(target) != null) {
            markDirty();
        }
    }

    public Map<ReputationSubject, Integer> getAll() {
        return Collections.unmodifiableMap(reputations);
    }

    @ApiStatus.Internal
    public Map<ReputationSubject, Integer> getReputationsInternal() {
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
