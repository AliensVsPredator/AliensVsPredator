package com.avp.goap;

public abstract class GOAPAction<T> {

    protected final GOAPWorldState effects;

    protected final GOAPWorldState preconditions;

    protected float cost;

    public GOAPAction() {
        this.effects = new GOAPWorldState();
        this.preconditions = new GOAPWorldState();
        this.cost = 1.0f;
    }

    public abstract boolean perform(T context, GOAPBlackboard blackboard);

    public abstract String getName();

    public GOAPWorldState getPreconditions() {
        return preconditions;
    }

    public GOAPWorldState getEffects() {
        return effects;
    }

    public float getCost() {
        return cost;
    }
}
