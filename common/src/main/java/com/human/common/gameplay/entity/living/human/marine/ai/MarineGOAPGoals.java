package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expression;
import com.lib.common.gameplay.goap.GOAPKeys;

public class MarineGOAPGoals {

    public static final Goal EQUIP_BEST_WATER_BREATHING_ARMOR = Goal.builder("EquipAirbreathingArmorGoal")
        .addPrecondition(GOAPKeys.IS_UNDERWATER, Expression.isTrue())
        .addDesiredCondition(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Expression.isNone())
        .build();

    public static final Goal EQUIP_BEST_ARMOR = Goal.builder("EquipBestArmorGoal")
        .addPrecondition(GOAPKeys.IS_UNDERWATER, Expression.isFalse())
        .addDesiredCondition(MarineGOAPKeys.BEST_ARMOR_SET, Expression.isNone())
        .build();

    private MarineGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
