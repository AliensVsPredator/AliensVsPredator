package com.lib.common.gameplay.goap;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;

public class GOAPGoals {

    public static final Goal KEEP_HEALTH_UP = Goal.builder("KeepHealthUpGoal")
        .addPrecondition(GOAPKeys.IS_FULL_HEALTH, Expressions.Boolean.isFalse())
        .addDesiredCondition(GOAPKeys.IS_FULL_HEALTH, Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_DROWNING_DAMAGE = Goal.builder("PreventDrowningDamageGoal")
        .addPrecondition(GOAPKeys.IS_UNDERWATER, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_DROWNING, Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_FIRE_DAMAGE = Goal.builder("PreventFireDamageGoal")
        .addPrecondition(GOAPKeys.IS_ON_FIRE, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_FIRE, Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_RADIATION_DAMAGE = Goal.builder("PreventRadiationDamageGoal")
        .addPrecondition(GOAPKeys.IS_NEAR_RADIOACTIVE_BIOME, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_RADIATION, Expressions.Boolean.isTrue())
        .build();
}
