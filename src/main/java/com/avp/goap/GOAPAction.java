package com.avp.goap;

import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.GOAPMutableConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.effect.GOAPMutableEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

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
