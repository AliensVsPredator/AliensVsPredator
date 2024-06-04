package org.avp.api.entity.ai;

import org.avp.api.entity.ai.plan.Planner;
import org.avp.api.entity.ai.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

public abstract class GOAPBrain {

    protected final GOAPBrainCache goapBrainCache;

    private final Planner planner;

    private final List<Sensor> sensors;

    protected GOAPBrain() {
        this.goapBrainCache = new GOAPBrainCache();
        this.planner = new Planner();
        this.sensors = new ArrayList<>();
        addSensors(sensors);
        addGoals(planner);
    }

    protected abstract void addSensors(List<Sensor> sensors);
    protected abstract void addGoals(Planner planner);

    public void tick() {
        sensors.forEach(Sensor::sense);
        planner.tick();
        goapBrainCache.clear();
    }

    public GOAPBrainCache getCache() {
        return goapBrainCache;
    }
}
