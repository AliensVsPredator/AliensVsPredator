package com.lib.common.gameplay.goap;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;

public class GOAPGoals {

    public static final Goal KEEP_HEALTH_UP = Goal.builder("KeepHealthUpGoal")
        .addPrecondition(GOAPStateKeys.IS_FULL_HEALTH, Expressions.Boolean.isFalse())
        .addDesiredCondition(GOAPStateKeys.IS_FULL_HEALTH.asDerived(), Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_DROWNING_DAMAGE = Goal.builder("PreventDrowningDamageGoal")
        .addPrecondition(GOAPStateKeys.IS_UNDERWATER, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPStateKeys.IS_PROTECTED_FROM_DROWNING.asDerived(), Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_FIRE_DAMAGE = Goal.builder("PreventFireDamageGoal")
        .addPrecondition(GOAPStateKeys.IS_ON_FIRE, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPStateKeys.IS_PROTECTED_FROM_FIRE.asDerived(), Expressions.Boolean.isTrue())
        .build();

    public static final Goal PREVENT_RADIATION_DAMAGE = Goal.builder("PreventRadiationDamageGoal")
        .addPrecondition(GOAPStateKeys.IS_NEAR_RADIOACTIVE_BIOME, Expressions.Boolean.isTrue())
        .addDesiredCondition(GOAPStateKeys.IS_PROTECTED_FROM_RADIATION.asDerived(), Expressions.Boolean.isTrue())
        .build();
}
