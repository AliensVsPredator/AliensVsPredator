package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.model.alien.HatchState;
import com.just.goap.Goal;
import com.just.goap.condition.Condition;
import com.just.goap.condition.expression.Expression;

public class OvomorphGOAPGoals {

    static final Goal HATCH = Goal.builder(
        "HatchGoal",
        new Condition<>(OvomorphGOAPKeys.HATCH_STATE, Expression.equalTo(HatchState.HATCHED))
    )
        .build();

    private OvomorphGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
