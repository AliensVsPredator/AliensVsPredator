package com.avp.goap;

import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public abstract class GOAPAction<T> {

    private final GOAPEffectContainer effects;

    private final String name;

    private final GOAPConditionContainer preconditions;

    protected float cost;

    public GOAPAction() {
        this.effects = createEffects();
        this.name = this.getClass().getSimpleName();
        this.preconditions = createPreconditions();
        this.cost = 1.0f;
    }

    public abstract GOAPConditionContainer createPreconditions();

    public abstract GOAPEffectContainer createEffects();

    public abstract boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard);

    public void onFinish(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {}

    public float getCost(T context, GOAPWorldState worldState) {
        return cost;
    }

    public GOAPEffectContainer getEffects() {
        return effects;
    }

    public String getName() {
        return name;
    }

    public GOAPConditionContainer getPreconditions() {
        return preconditions;
    }
}
