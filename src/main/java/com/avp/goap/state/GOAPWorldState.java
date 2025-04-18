package com.avp.goap.state;

import java.util.HashMap;
import java.util.Map;

import com.avp.goap.TypedIdentifier;
import com.avp.goap.expression.GOAPConditionSet;

public class GOAPWorldState extends GOAPStateCache {

    public GOAPWorldState(Map<TypedIdentifier<?>, Object> stateMap) {
        super(stateMap);
    }

    public boolean satisfiedBy(GOAPWorldState worldState) {
        for (var entry : stateMap.entrySet()) {
            var otherValue = worldState.get(entry.getKey());

            if (!entry.getValue().equals(otherValue)) {
                return false;
            }
        }

        return true;
    }

    public boolean satisfies(GOAPWorldState other) {
        return other.satisfiedBy(this);
    }

    public boolean satisfies(GOAPConditionSet conditionSet) {
        return conditionSet.satisfiedBy(this);
    }

    public GOAPWorldState applyEffects(GOAPWorldState effects) {
        var newState = new GOAPMutableWorldState(new HashMap<>(stateMap));

        for (var entry : effects.stateMap.entrySet()) {
            @SuppressWarnings("unchecked")
            var key = (TypedIdentifier<Object>) entry.getKey();

            newState.set(key, entry.getValue());
        }

        return newState;
    }
}
