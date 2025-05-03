package com.avp.common.goap.state;

import java.util.Map;

import com.avp.common.goap.TypedIdentifier;
import com.avp.common.goap.condition.GOAPConditionContainer;
import com.avp.common.goap.effect.GOAPEffectContainer;

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
