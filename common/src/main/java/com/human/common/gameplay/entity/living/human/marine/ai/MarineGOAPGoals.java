package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAPGoals {

    public static final Goal EXTINGUISH_SELF_GOAL = Goal.builder("ExtinguishSelfGoal")
        .addPrecondition(GOAPSensors.HAS_FIRE_RESISTANCE.key(), Expressions.Boolean.isFalse())
        .addDesiredCondition(GOAPSensors.IS_ON_FIRE.key().asDerived(), Expressions.Boolean.isFalse())
        .build();

    public static final Goal ACQUIRE_FIRE_RESISTANCE_GOAL = Goal.builder("AcquireFireResistanceGoal")
        .addPrecondition(GOAPSensors.IS_ON_FIRE.key(), Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), Expressions.Boolean.isTrue())
        .build();

    private MarineGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
