package org.avp.api.entity;

import java.util.UUID;

public interface HiveMember {
    boolean hasHivemind();
    void setHivemindSignature(UUID uuid);
}
