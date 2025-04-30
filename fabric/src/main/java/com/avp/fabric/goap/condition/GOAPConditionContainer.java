package com.avp.fabric.goap.condition;

import java.util.Arrays;
import java.util.List;

import com.avp.fabric.goap.state.GOAPWorldState;

public class GOAPConditionContainer {

    public static GOAPConditionContainer of(GOAPCondition<?>... conditions) {
        return new GOAPConditionContainer(Arrays.stream(conditions).toList());
    }

    protected final List<GOAPCondition<?>> conditions;

    protected GOAPConditionContainer(List<GOAPCondition<?>> conditions) {
        this.conditions = conditions;
    }

    public List<GOAPCondition<?>> getConditions() {
        return conditions;
    }

    public boolean satisfiedBy(GOAPWorldState worldState) {
        return conditions.stream().allMatch(condition -> condition.satisfiedBy(worldState));
    }

}
