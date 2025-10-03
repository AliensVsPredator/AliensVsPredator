package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.PlaceWaterAtFeetAction;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPStateKeys;

public class MarineGOAPActions {

    public static final Action<Marine> EQUIP_FIRE_RESISTANT_ARMOR = Action.<Marine>builder("EquipFireResistantArmorAction")
        .addPrecondition(MarineGOAPStateKeys.ARMOR_INTENT, Expressions.Compare.equalTo(ArmorIntent.FIRE_PROTECTION))
        // TODO: Add a precondition where a "BETTER_ARMOR" state is not empty.
        .addEffect(GOAPStateKeys.HAS_FIRE_RESISTANCE.asDerived(), true)
        .withPerformCallback((a, b, c) -> {
            // TODO: Take off current armor pieces and put them back into the inventory.
            // TODO: Equip the parts of the "BETTER_ARMOR" state that are present.
            return Action.Result.CONTINUE;
        })
        .build();

    public static final Action<Marine> PLACE_WATER_AT_FEET_ACTION = Action.<Marine>builder("PlaceWaterAtFeetAction")
        .addPrecondition(GOAPStateKeys.IS_ON_GROUND, Expressions.Boolean.isTrue())
        .addPrecondition(MarineGOAPStateKeys.HAS_WATER_BUCKET, Expressions.Boolean.isTrue())
        .addPrecondition(MarineGOAPStateKeys.IS_CURRENT_BLOCK_POS_REPLACEABLE, Expressions.Boolean.isTrue())
        .addEffect(GOAPStateKeys.IS_ON_FIRE.asDerived(), false)
        .withPerformCallback((marine, $2, blackboard) -> PlaceWaterAtFeetAction.perform(marine, blackboard))
        .withFinishCallback((marine, $2, blackboard) -> PlaceWaterAtFeetAction.onFinish(marine, blackboard))
        .build();

    public static final Action<Marine> USE_FIRE_RESISTANCE_ITEM = Action.<Marine>builder("UseFireResistanceItemAction")
        // TODO: Add a precondition where a "FIRE_RESISTANCE_ITEMS" state is not empty.
        .addEffect(GOAPStateKeys.HAS_FIRE_RESISTANCE.asDerived(), true)
        .withPerformCallback((a, b, c) -> {
            // TODO: Determine the best FIRE_RESISTANCE_ITEM to use and use it.
            return Action.Result.CONTINUE;
        })
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
