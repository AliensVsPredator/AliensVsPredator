package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;

public class MarineGOAPActions {

    public static final Action<Marine> EQUIP_FIRE_RESISTANT_ARMOR = Action.<Marine>builder("EquipFireResistantArmorAction")
        .addPrecondition(MarineGOAPSensors.ARMOR_INTENT.key(), Expressions.Compare.equalTo(ArmorIntent.FIRE_PROTECTION))
        // TODO: Add a precondition where a "BETTER_ARMOR" state is not empty.
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback((a, b, c) -> {
            // TODO: Take off current armor pieces and put them back into the inventory.
            // TODO: Equip the parts of the "BETTER_ARMOR" state that are present.
            return Action.Signal.CONTINUE;
        })
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
