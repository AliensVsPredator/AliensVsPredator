package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPStateKeys;

public class MarineGOAPGoals {

    public static final Goal EXTINGUISH_SELF_GOAL = Goal.builder("ExtinguishSelfGoal")
        .addPrecondition(GOAPStateKeys.HAS_FIRE_RESISTANCE, Expressions.Boolean.isFalse())
        .addDesiredCondition(GOAPStateKeys.IS_ON_FIRE.asDerived(), Expressions.Boolean.isFalse())
        .build();

    public static final Goal ACQUIRE_FIRE_RESISTANCE_GOAL = Goal.builder("AcquireFireResistanceGoal")
        .addPrecondition(GOAPStateKeys.IS_ON_FIRE, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPStateKeys.HAS_FIRE_RESISTANCE.asDerived(), Expressions.Boolean.isTrue())
        .build();

    private MarineGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
