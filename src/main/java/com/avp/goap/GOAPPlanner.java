package com.avp.goap;

import com.bvanseg.just.functional.option.Option;

import java.util.Collection;

public class GOAPPlanner<T> {

    public Option<GOAPPlan<T>> createPlan(GOAPWorldState worldState, Collection<GOAPGoal> goals) {
        return Option.none();
    }
}
