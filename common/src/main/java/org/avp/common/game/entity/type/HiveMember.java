package org.avp.common.game.entity.type;

import java.util.UUID;

public interface HiveMember {

    boolean hasHivemind();

    UUID getHivemindSignature();

    void setHivemindSignature(UUID uuid);
}
