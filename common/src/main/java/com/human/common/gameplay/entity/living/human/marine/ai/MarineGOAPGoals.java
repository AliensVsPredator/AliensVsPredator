package com.human.common.gameplay.entity.living.human.marine.ai;

import com.just.goap.Goal;
import com.just.goap.condition.Condition;
import com.just.goap.condition.expression.Expression;

public class MarineGOAPGoals {

    public static final Goal EQUIP_BEST_ARMOR = Goal.builder(
        "EquipBestArmorGoal",
        new Condition<>(MarineGOAPKeys.BEST_ARMOR_SET, Expression.isNone())
    )
        .build();

    private MarineGOAPGoals() {
        throw new UnsupportedOperationException();
    }
}
