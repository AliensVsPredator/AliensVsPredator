package com.avp.goap.expression;

import com.avp.goap.TypedIdentifier;
import com.avp.goap.state.GOAPWorldState;

public record GOAPCondition<T>(
    TypedIdentifier<? super T> identifier,
    GOAPExpression<? super T> expression
) {

    public boolean satisfiedBy(GOAPWorldState worldState) {
        @SuppressWarnings("unchecked")
        var value = (T) worldState.get(identifier);
        return value != null && expression.evaluate(value);
    }
}
