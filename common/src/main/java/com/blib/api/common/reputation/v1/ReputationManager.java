package com.blib.api.common.reputation.v1;

public interface ReputationManager {

    ReputationData getOrCreate(ReputationSubject reputationSubject);

    int getReputation(ReputationSubject from, ReputationSubject to);

    void setReputation(ReputationSubject from, ReputationSubject to, int value);

    void adjustReputation(ReputationSubject from, ReputationSubject to, int delta);

    void removeReputation(ReputationSubject from, ReputationSubject to);

    void removeSubject(ReputationSubject reputationSubject);

    boolean exists(ReputationSubject reputationSubject);
}
