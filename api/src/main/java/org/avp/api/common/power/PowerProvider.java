package org.avp.api.common.power;

public interface PowerProvider {
    PowerGraph getPowerGraph();
    int providePowerPerTick();
}
