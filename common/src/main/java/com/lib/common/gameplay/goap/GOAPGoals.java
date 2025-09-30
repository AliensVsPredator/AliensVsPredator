package com.lib.common.gameplay.goap;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expression;

public class GOAPGoals {

    public static final Goal KEEP_HEALTH_UP = Goal.builder("KeepHealthUpGoal")
        .addPrecondition(GOAPKeys.IS_FULL_HEALTH, Expression.isFalse())
        .addDesiredCondition(GOAPKeys.IS_FULL_HEALTH, Expression.isTrue())
        .build();

    public static final Goal PREVENT_DROWNING_DAMAGE = Goal.builder("PreventDrowningDamageGoal")
        .addPrecondition(GOAPKeys.IS_UNDERWATER, Expression.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_DROWNING, Expression.isTrue())
        .build();

    public static final Goal PREVENT_FIRE_DAMAGE = Goal.builder("PreventFireDamageGoal")
        .addPrecondition(GOAPKeys.IS_ON_FIRE, Expression.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_FIRE, Expression.isTrue())
        .build();

    public static final Goal PREVENT_RADIATION_DAMAGE = Goal.builder("PreventRadiationDamageGoal")
        .addPrecondition(GOAPKeys.IS_NEAR_RADIOACTIVE_BIOME, Expression.isTrue())
        .addDesiredCondition(GOAPKeys.IS_PROTECTED_FROM_RADIATION, Expression.isTrue())
        .build();
}
