package com.avp.goap;

import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public abstract class GOAPAction<T> {

    private final GOAPWorldState effects;

    private final String name;

    private final GOAPWorldState preconditions;

    protected float cost;

    public GOAPAction() {
        this.effects = createEffects();
        this.name = this.getClass().getSimpleName();
        this.preconditions = createPreconditions();
        this.cost = 1.0f;
    }

    public abstract GOAPWorldState createPreconditions();

    public abstract GOAPWorldState createEffects();

    public abstract boolean perform(T context, GOAPBlackboard blackboard);

    public float getCost() {
        return cost;
    }

    public GOAPWorldState getEffects() {
        return effects;
    }

    public String getName() {
        return name;
    }

    public GOAPWorldState getPreconditions() {
        return preconditions;
    }
}
