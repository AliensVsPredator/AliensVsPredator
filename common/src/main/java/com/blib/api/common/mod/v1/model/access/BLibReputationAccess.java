package com.blib.api.common.mod.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationManager;
import com.blib.api.common.reputation.v1.ReputationSubject;
import com.blib.internal.common.reputation.BLibReputationManager;

public class BLibReputationAccess implements ReputationManager {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibReputationAccess(BLibMod mod) {
        this.mod = mod;
    }

    @Override
    public ReputationData getOrCreate(ReputationSubject subject) {
        return BLibReputationManager.INSTANCE.getOrCreate(subject);
    }

    @Override
    public int getReputation(ReputationSubject from, ReputationSubject to) {
        return BLibReputationManager.INSTANCE.getReputation(from, to);
    }

    @Override
    public void setReputation(ReputationSubject from, ReputationSubject to, int value) {
        BLibReputationManager.INSTANCE.setReputation(from, to, value);
    }

    @Override
    public void adjustReputation(ReputationSubject from, ReputationSubject to, int delta) {
        BLibReputationManager.INSTANCE.adjustReputation(from, to, delta);
    }

    @Override
    public void removeReputation(ReputationSubject from, ReputationSubject to) {
        BLibReputationManager.INSTANCE.removeReputation(from, to);
    }

    @Override
    public void removeSubject(ReputationSubject subject) {
        BLibReputationManager.INSTANCE.removeSubject(subject);
    }

    @Override
    public boolean exists(ReputationSubject subject) {
        return BLibReputationManager.INSTANCE.exists(subject);
    }
}
