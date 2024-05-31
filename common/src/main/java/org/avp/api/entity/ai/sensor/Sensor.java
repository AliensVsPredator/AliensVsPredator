package org.avp.api.entity.ai.sensor;

import org.avp.api.entity.ai.GOAPBrainCache;

import java.util.function.BooleanSupplier;

public abstract class Sensor {

    protected BooleanSupplier shouldDisableSupplier;

    protected final GOAPBrainCache goapBrainCache;

    protected Sensor(GOAPBrainCache goapBrainCache) {
        this.goapBrainCache = goapBrainCache;
    }

    public abstract void sense();

    public Sensor disableIf(BooleanSupplier booleanSupplier) {
        this.shouldDisableSupplier = booleanSupplier;
        return this;
    }

    public boolean isDisabled() {
        return shouldDisableSupplier != null && shouldDisableSupplier.getAsBoolean();
    }
}
