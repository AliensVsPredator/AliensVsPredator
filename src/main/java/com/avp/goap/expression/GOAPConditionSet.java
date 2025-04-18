package com.avp.goap.expression;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.avp.goap.state.GOAPWorldState;

public class GOAPConditionSet {

    public static GOAPConditionSet of(GOAPCondition<?>... conditions) {
        return new GOAPConditionSet(Arrays.stream(conditions).collect(Collectors.toSet()));
    }

    private final Set<GOAPCondition<?>> conditions;

    private GOAPConditionSet(Set<GOAPCondition<?>> conditions) {
        this.conditions = conditions;
    }

    public Set<GOAPCondition<?>> getConditions() {
        return conditions;
    }

    public boolean satisfiedBy(GOAPWorldState worldState) {
        return conditions.stream().allMatch(condition -> condition.satisfiedBy(worldState));
    }

}
