package com.xlib.goap.state;

import com.xlib.goap.TypedIdentifier;
import com.xlib.goap.condition.GOAPConditionContainer;
import com.xlib.goap.effect.GOAPEffectContainer;

import java.util.Map;

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

    public boolean satisfies(GOAPEffectContainer effectContainer) {
        return effectContainer.toWorldState()
            .satisfiedBy(this);
    }

    public boolean satisfies(GOAPConditionContainer conditionContainer) {
        return conditionContainer.satisfiedBy(this);
    }
}
