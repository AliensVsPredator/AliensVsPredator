package com.avp.goap.condition;

import com.avp.goap.TypedIdentifier;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPWorldState;

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
