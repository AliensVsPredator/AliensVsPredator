package com.avp.goap.condition;

import java.util.Arrays;
import java.util.List;

import com.avp.goap.state.GOAPWorldState;

public class GOAPConditionContainer {

    public static GOAPConditionContainer of(GOAPCondition<?>... conditions) {
        return new GOAPConditionContainer(Arrays.stream(conditions).toList());
    }

    private final List<GOAPCondition<?>> conditions;

    private GOAPConditionContainer(List<GOAPCondition<?>> conditions) {
        this.conditions = conditions;
    }

    public List<GOAPCondition<?>> getConditions() {
        return conditions;
    }

    public boolean satisfiedBy(GOAPWorldState worldState) {
        return conditions.stream().allMatch(condition -> condition.satisfiedBy(worldState));
    }

}
