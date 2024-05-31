package org.avp.api.entity.ai;

import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.Sensor;

import java.util.HashSet;
import java.util.Set;

public abstract class GOAPBrain {

    protected final GOAPBrainCache goapBrainCache;

    protected final Planner planner;

    protected final Set<Sensor> sensors;

    protected GOAPBrain(Planner planner) {
        this.goapBrainCache = new GOAPBrainCache();
        this.planner = planner;
        this.sensors = new HashSet<>();
    }

    public void tick() {
        sensors.forEach(Sensor::sense);
        planner.tick();
    }

    public GOAPBrainCache getCache() {
        return goapBrainCache;
    }
}
