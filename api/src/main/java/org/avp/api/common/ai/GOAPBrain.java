package org.avp.api.common.ai;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.common.ai.plan.Planner;
import org.avp.api.common.ai.sensor.Sensor;

public abstract class GOAPBrain {

    protected final GOAPBrainCache goapBrainCache;

    private boolean isInitialized;

    private final Planner planner;

    private final List<Sensor> sensors;

    protected GOAPBrain() {
        this.goapBrainCache = new GOAPBrainCache();
        this.planner = new Planner();
        this.sensors = new ArrayList<>();
    }

    protected abstract void addSensors(List<Sensor> sensors);

    protected abstract void addGoals(Planner planner);

    public void init() {
        if (isInitialized) {
            throw new IllegalStateException("GOAPBrain is already initialized.");
        }

        addSensors(sensors);
        addGoals(planner);

        isInitialized = true;
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
