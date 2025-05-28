package com.xlib.goap;

import com.xlib.goap.condition.GOAPCondition;
import com.xlib.goap.condition.GOAPConditionContainer;
import com.xlib.goap.condition.GOAPMutableConditionContainer;
import com.xlib.goap.condition.expression.GOAPExpression;
import com.xlib.goap.effect.GOAPEffect;
import com.xlib.goap.effect.GOAPEffectContainer;
import com.xlib.goap.effect.GOAPMutableEffectContainer;
import com.xlib.goap.state.GOAPBlackboard;
import com.xlib.goap.state.GOAPWorldState;

public abstract class GOAPAction<T> {

    private final GOAPMutableEffectContainer effects;

    private final String name;

    private final GOAPMutableConditionContainer preconditions;

    protected float cost;

    public GOAPAction() {
        this.effects = new GOAPMutableEffectContainer();
        this.name = this.getClass().getSimpleName();
        this.preconditions = new GOAPMutableConditionContainer();
        this.cost = 1.0f;
    }

    protected final <U> void addPrecondition(TypedIdentifier<? extends U> identifier, GOAPExpression<? super U> condition) {
        preconditions.addCondition(new GOAPCondition<>(identifier, condition));
    }

    protected final void addEffect(GOAPEffect<?> effect) {
        effects.addEffect(effect);
    }

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
