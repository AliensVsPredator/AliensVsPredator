package com.avp.goap;

import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public abstract class GOAPAction<T> {

    private final GOAPWorldState effects;

    private final String name;

    private final GOAPConditionSet preconditions;

    protected float cost;

    public GOAPAction() {
        this.effects = createEffects();
        this.name = this.getClass().getSimpleName();
        this.preconditions = createPreconditions();
        this.cost = 1.0f;
    }

    public abstract GOAPConditionSet createPreconditions();

    public abstract GOAPWorldState createEffects();

    public abstract boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard);

    public void onFinish(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {}

    public float getCost(T context, GOAPWorldState worldState) {
        return cost;
    }

    public GOAPWorldState getEffects() {
        return effects;
    }

    public String getName() {
        return name;
    }

    public GOAPConditionSet getPreconditions() {
        return preconditions;
    }
}
