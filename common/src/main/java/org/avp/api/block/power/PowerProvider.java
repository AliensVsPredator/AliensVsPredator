package org.avp.api.block.power;

public interface PowerProvider {
    PowerGraph getPowerGraph();
    int providePowerPerTick();
}
