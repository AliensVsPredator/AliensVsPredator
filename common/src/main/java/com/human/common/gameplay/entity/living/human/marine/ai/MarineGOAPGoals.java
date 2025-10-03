package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPStateKeys;

public class MarineGOAPGoals {

    public static final Goal EQUIP_BEST_WATER_BREATHING_ARMOR = Goal.builder("EquipAirbreathingArmorGoal")
        .addPrecondition(GOAPStateKeys.IS_UNDERWATER, Expressions.Boolean.isTrue())
        .addDesiredCondition(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET.asDerived(), Expressions.Option.isNone())
        .build();

    public static final Goal EQUIP_BEST_ARMOR = Goal.builder("EquipBestArmorGoal")
        .addPrecondition(GOAPStateKeys.IS_UNDERWATER, Expressions.Boolean.isFalse())
        .addDesiredCondition(MarineGOAPKeys.BEST_ARMOR_SET.asDerived(), Expressions.Option.isNone())
        .build();

    private MarineGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
