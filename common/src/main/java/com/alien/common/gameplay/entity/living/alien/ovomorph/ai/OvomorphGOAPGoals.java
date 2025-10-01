package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.model.alien.HatchState;
import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;

public class OvomorphGOAPGoals {

    static final Goal HATCH = Goal.builder("HatchGoal")
        .addDesiredCondition(OvomorphGOAPKeys.HATCH_STATE, Expressions.Compare.equalTo(HatchState.HATCHED))
        .build();

    private OvomorphGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
