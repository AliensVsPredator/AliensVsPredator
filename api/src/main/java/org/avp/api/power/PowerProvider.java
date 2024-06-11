package org.avp.api.power;

public interface PowerProvider {
    PowerGraph getPowerGraph();
    int providePowerPerTick();
}
