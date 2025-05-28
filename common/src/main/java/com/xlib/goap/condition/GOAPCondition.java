package com.xlib.goap.condition;

import com.xlib.goap.TypedIdentifier;
import com.xlib.goap.condition.expression.GOAPExpression;
import com.xlib.goap.effect.GOAPEffectContainer;
import com.xlib.goap.state.GOAPWorldState;

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
