package org.avp.api.entity.ai.sensor;

import org.avp.api.entity.ai.GOAPBrainCache;

public abstract class Sensor {

    protected final GOAPBrainCache goapBrainCache;

    protected Sensor(GOAPBrainCache goapBrainCache) {
        this.goapBrainCache = goapBrainCache;
    }

    public abstract void sense();
}
