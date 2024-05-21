package org.avp.api.entity;

import java.util.UUID;

public interface HiveMember {
    boolean hasHivemind();
    UUID getHivemindSignature();
    void setHivemindSignature(UUID uuid);
}
