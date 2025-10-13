package com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.action.EquipFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.action.MoveToFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.action.PickUpFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.fire_resistance_item.action.UseFRIAction;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ItemTarget;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;

public class FRIActions {

    public static final Action<Marine> MOVE_TO_BEST_FRI = Action.<Marine>builder("MoveToBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.WORLD))
        .addPrecondition(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key(), Expressions.Boolean.isFalse())
        .addEffect(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key().asDerived(), true)
        .withPerformCallback(MoveToFRIAction::perform)
        .withFinishCallback(MoveToFRIAction::onFinish)
        .build();

    public static final Action<Marine> PICK_UP_BEST_FRI = Action.<Marine>builder("PickUpBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.WORLD))
        .addPrecondition(FRISensors.IS_BEST_WORLD_FRI_IN_RANGE.key(), Expressions.Boolean.isTrue())
        .addEffect(FRISensors.BEST_FRI_LOCATION.key().asDerived(), ItemTarget.Location.INVENTORY)
        .withPerformCallback(PickUpFRIAction::perform)
        .build();

    public static final Action<Marine> EQUIP_BEST_FRI = Action.<Marine>builder("EquipBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.INVENTORY))
        .addEffect(FRISensors.BEST_FRI_LOCATION.key().asDerived(), ItemTarget.Location.HANDS)
        .withPerformCallback(EquipFRIAction::perform)
        .build();

    public static final Action<Marine> USE_BEST_FRI = Action.<Marine>builder("UseBestFRIAction")
        .addPrecondition(FRISensors.BEST_FRI_LOCATION.key(), Expressions.Compare.equalTo(ItemTarget.Location.HANDS))
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback(UseFRIAction::perform)
        .build();
}
