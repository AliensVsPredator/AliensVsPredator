package com.human.common.gameplay.entity.living.human.marine.ai.equip_best_armor;

import com.just.goap.Goal;
import com.just.goap.condition.expression.Expressions;

public class EquipBestArmorGoals {

    public static final Goal EQUIP_BEST_ARMOR_GOAL = Goal.builder("EquipBestArmorGoal")
        .addDesiredCondition(EquipBestArmorSensors.ARE_ALL_BEST_ARMOR_SET_PIECES_EQUIPPED.key().asDerived(), Expressions.Boolean.isTrue())
        .build();

    private EquipBestArmorGoals() {
        throw new UnsupportedOperationException();
    }
}
