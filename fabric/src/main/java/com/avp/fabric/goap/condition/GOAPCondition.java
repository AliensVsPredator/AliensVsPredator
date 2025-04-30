package com.avp.fabric.goap.condition;

import com.avp.fabric.goap.TypedIdentifier;
import com.avp.fabric.goap.condition.expression.GOAPExpression;
import com.avp.fabric.goap.effect.GOAPEffectContainer;
import com.avp.fabric.goap.state.GOAPWorldState;

public record GOAPCondition<T>(
    TypedIdentifier<? extends T> identifier,
    GOAPExpression<? super T> expression
) {

    public boolean satisfiedBy(GOAPEffectContainer effectContainer) {
        return satisfiedBy(effectContainer.toWorldState());
    }

    public boolean satisfiedBy(GOAPWorldState worldState) {
        var value = (T) worldState.get(identifier);
        return value != null && expression.evaluate(value);
    }
}
