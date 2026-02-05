package com.blib.api.common.mod.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.api.common.reputation.v1.ReputationManager;
import com.blib.internal.common.reputation.BLibReputationManager;

public class BLibReputationAccess implements ReputationManager {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibReputationAccess(BLibMod mod) {
        this.mod = mod;
    }

    @Override
    public ReputationData getOrCreate(ReputationKey reputationKey) {
        return BLibReputationManager.INSTANCE.getOrCreate(reputationKey);
    }

    @Override
    public int getReputation(ReputationKey from, ReputationKey to) {
        return BLibReputationManager.INSTANCE.getReputation(from, to);
    }

    @Override
    public void setReputation(ReputationKey from, ReputationKey to, int value) {
        BLibReputationManager.INSTANCE.setReputation(from, to, value);
    }

    @Override
    public void adjustReputation(ReputationKey from, ReputationKey to, int delta) {
        BLibReputationManager.INSTANCE.adjustReputation(from, to, delta);
    }

    @Override
    public void removeReputation(ReputationKey from, ReputationKey to) {
        BLibReputationManager.INSTANCE.removeReputation(from, to);
    }

    @Override
    public void removeSubject(ReputationKey reputationKey) {
        BLibReputationManager.INSTANCE.removeSubject(reputationKey);
    }

    @Override
    public boolean exists(ReputationKey reputationKey) {
        return BLibReputationManager.INSTANCE.exists(reputationKey);
    }
}
