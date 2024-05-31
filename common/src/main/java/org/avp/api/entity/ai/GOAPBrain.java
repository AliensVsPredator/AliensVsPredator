package org.avp.api.entity.ai;

import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

public abstract class GOAPBrain {

    protected final GOAPBrainCache goapBrainCache;

    protected final Planner planner;

    protected final List<Sensor> sensors;

    protected GOAPBrain(Planner planner) {
        this.goapBrainCache = new GOAPBrainCache();
        this.planner = planner;
        this.sensors = new ArrayList<>();
    }

    public void tick() {
        sensors.forEach(Sensor::sense);
        planner.tick();
        goapBrainCache.clear();
    }

    public GOAPBrainCache getCache() {
        return goapBrainCache;
    }
}
