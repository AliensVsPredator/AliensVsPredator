package org.avp.common.game.entity.type;

public interface Parasite {
    int getTicksAttachedToHost();
    void incrementTicksAttachedToHost();
    boolean isFertile();
    void setFertile(boolean isFertile);
}
