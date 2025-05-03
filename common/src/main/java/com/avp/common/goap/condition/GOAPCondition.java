package com.avp.common.goap.condition;

import com.avp.common.goap.TypedIdentifier;
import com.avp.common.goap.condition.expression.GOAPExpression;
import com.avp.common.goap.effect.GOAPEffectContainer;
import com.avp.common.goap.state.GOAPWorldState;

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
