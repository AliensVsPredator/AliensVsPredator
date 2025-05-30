package com.lib.common.gameplay.goap;

import com.lib.common.gameplay.goap.condition.GOAPCondition;
import com.lib.common.gameplay.goap.condition.GOAPConditionContainer;
import com.lib.common.gameplay.goap.condition.GOAPMutableConditionContainer;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffect;
import com.lib.common.gameplay.goap.effect.GOAPEffectContainer;
import com.lib.common.gameplay.goap.effect.GOAPMutableEffectContainer;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;

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
