package com.avp.goap.expression;

import com.avp.goap.TypedIdentifier;
import com.avp.goap.state.GOAPWorldState;

public record GOAPCondition<T>(
    TypedIdentifier<? extends T> identifier,
    GOAPExpression<? super T> expression
) {

    public boolean satisfiedBy(GOAPWorldState worldState) {
        var value = (T) worldState.get(identifier);
        return value != null && expression.evaluate(value);
    }
}
