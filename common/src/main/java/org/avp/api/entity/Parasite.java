package org.avp.api.entity;

public interface Parasite {
    int getTicksAttachedToHost();
    void incrementTicksAttachedToHost();
    boolean isFertile();
    void setFertile(boolean isFertile);
}
