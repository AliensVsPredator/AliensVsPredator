package com.lib.common.gameplay.goap.state;

import com.lib.common.gameplay.goap.TypedIdentifier;
import com.lib.common.gameplay.goap.condition.GOAPConditionContainer;
import com.lib.common.gameplay.goap.effect.GOAPEffectContainer;

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
