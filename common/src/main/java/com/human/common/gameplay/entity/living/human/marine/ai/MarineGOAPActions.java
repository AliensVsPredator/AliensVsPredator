package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.DrinkPotionAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestArmorAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipBestWaterbreathingArmorAction;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expression;
import com.lib.common.gameplay.goap.GOAPKeys;

import java.util.List;
import java.util.function.Predicate;

import com.avp.common.model.inventory.AVPInventory;

public class MarineGOAPActions {

    public static final Action<Marine> DRINK_FIRE_RESISTANCE_POTION = Action.<Marine>builder("DrinkFireResistancePotionAction").<List<AVPInventory
            .Entry>>addPrecondition(
            GOAPKeys.FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY,
            Expression.where(Predicate.not(List::isEmpty), "has fire resistance potion")
        )
        .addEffect(GOAPKeys.IS_PROTECTED_FROM_FIRE, true)
        .withPerformPredicate((a, b, c) -> DrinkPotionAction.perform(GOAPKeys.FIRE_RESISTANCE_POTION_ENTRIES_IN_INVENTORY, a, b, c))
        .build();

    public static final Action<Marine> DRINK_WATER_BREATHING_POTION = Action.<Marine>builder("DrinkWaterBreathingPotionAction").<List<AVPInventory
        .Entry>>addPrecondition(
            GOAPKeys.WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY,
            Expression.where(Predicate.not(List::isEmpty), "has water breathing potion")
        )
        .addEffect(GOAPKeys.IS_PROTECTED_FROM_DROWNING, true)
        .withPerformPredicate((a, b, c) -> DrinkPotionAction.perform(GOAPKeys.WATER_BREATHING_POTION_ENTRIES_IN_INVENTORY, a, b, c))
        .build();

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
