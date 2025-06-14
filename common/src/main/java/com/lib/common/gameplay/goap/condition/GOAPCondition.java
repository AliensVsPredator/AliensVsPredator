package com.lib.common.gameplay.goap.condition;

import com.lib.common.gameplay.goap.TypedIdentifier;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffectContainer;
import com.lib.common.gameplay.goap.state.GOAPWorldState;

public record GOAPCondition<T>(
    TypedIdentifier<? super T> identifier,
    GOAPExpression<? super T> expression
) {

    public boolean satisfiedBy(GOAPEffectContainer effectContainer) {
        return satisfiedBy(effectContainer.toWorldState());
    }

    @SuppressWarnings("unchecked")
    public boolean satisfiedBy(GOAPWorldState worldState) {
        var value = (T) worldState.get(identifier);
        return value != null && expression.evaluate(value);
    }
}
