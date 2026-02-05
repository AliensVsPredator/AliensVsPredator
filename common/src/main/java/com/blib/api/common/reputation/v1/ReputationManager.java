package com.blib.api.common.reputation.v1;

public interface ReputationManager {

    ReputationData getOrCreate(ReputationKey reputationKey);

    int getReputation(ReputationKey from, ReputationKey to);

    void setReputation(ReputationKey from, ReputationKey to, int value);

    void adjustReputation(ReputationKey from, ReputationKey to, int delta);

    void removeReputation(ReputationKey from, ReputationKey to);

    void removeSubject(ReputationKey reputationKey);

    boolean exists(ReputationKey reputationKey);
}
