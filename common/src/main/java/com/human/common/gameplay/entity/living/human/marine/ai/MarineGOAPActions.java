package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestArmorAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestWaterbreathingArmorAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expression;

public class MarineGOAPActions {

    public static final Action<Marine> EQUIP_BEST_ARMOR = Action.<Marine>builder("EquipBestArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_ARMOR_SET, Expression.isSome())
        .addEffect(MarineGOAPKeys.BEST_ARMOR_SET, Option.none())
        .withPerformPredicate(EquipBestArmorAction::perform)
        .build();

    public static final Action<Marine> EQUIP_BEST_WATER_BREATHING_ARMOR = Action.<Marine>builder("EquipBestWaterBreathingArmorAction")
        .addPrecondition(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Expression.isSome())
        .addEffect(MarineGOAPKeys.BEST_WATER_BREATHING_ARMOR_SET, Option.none())
        .withPerformPredicate(EquipBestWaterbreathingArmorAction::perform)
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
