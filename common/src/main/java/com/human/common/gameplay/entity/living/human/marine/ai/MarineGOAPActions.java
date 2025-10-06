package com.human.common.gameplay.entity.living.human.marine.ai;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipConsumableFireResistanceItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.EquipThrowableResistanceItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.PlaceWaterAtFeetAction;
import com.human.common.gameplay.entity.living.human.marine.ai.action.UseFireResistanceItemAction;
import com.human.common.gameplay.entity.living.human.marine.ai.model.ArmorIntent;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ConsumableFireResistanceItemStrategies;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.ThrowableFireResistanceItemStrategies;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;
import com.lib.common.gameplay.goap.GOAPSensors;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class MarineGOAPActions {

    public static final Action<Marine> EQUIP_FIRE_RESISTANT_ARMOR = Action.<Marine>builder("EquipFireResistantArmorAction")
        .addPrecondition(MarineGOAPSensors.ARMOR_INTENT.key(), Expressions.Compare.equalTo(ArmorIntent.FIRE_PROTECTION))
        // TODO: Add a precondition where a "BETTER_ARMOR" state is not empty.
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback((a, b, c) -> {
            // TODO: Take off current armor pieces and put them back into the inventory.
            // TODO: Equip the parts of the "BETTER_ARMOR" state that are present.
            return Action.Result.CONTINUE;
        })
        .build();

    public static final Action<Marine> EQUIP_WATER_BUCKET_ACTION = Action.<Marine>builder("EquipWaterBucketAction")
        .addPrecondition(MarineGOAPSensors.HAS_WATER_BUCKET_IN_INVENTORY.key(), Expressions.Boolean.isTrue())
        .addEffect(MarineGOAPSensors.HAS_WATER_BUCKET_EQUIPPED.key().asDerived(), true)
        .withPerformCallback((marine, $2, $3) -> EquipItemAction.perform(marine, Items.WATER_BUCKET, InteractionHand.MAIN_HAND))
        .build();

    public static final Action<Marine> PLACE_WATER_AT_FEET_ACTION = Action.<Marine>builder("PlaceWaterAtFeetAction")
        .addPrecondition(GOAPSensors.IS_ON_GROUND.key(), Expressions.Boolean.isTrue())
        .addPrecondition(MarineGOAPSensors.HAS_WATER_BUCKET_EQUIPPED.key(), Expressions.Boolean.isTrue())
        .addPrecondition(MarineGOAPSensors.IS_CURRENT_BLOCK_POS_REPLACEABLE.key(), Expressions.Boolean.isTrue())
        .addEffect(GOAPSensors.IS_ON_FIRE.key().asDerived(), false)
        .withPerformCallback((marine, $2, blackboard) -> PlaceWaterAtFeetAction.perform(marine, blackboard))
        .withFinishCallback((marine, $2, blackboard) -> PlaceWaterAtFeetAction.onFinish(marine, blackboard))
        .build();

    public static final Action<Marine> EQUIP_CONSUMABLE_FIRE_RESISTANCE_ITEM = Action.<Marine>builder(
        "EquipConsumableFireResistanceItemAction"
    )
        .addPrecondition(MarineGOAPSensors.HAS_CONSUMABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(), Expressions.Boolean.isTrue())
        .addEffect(MarineGOAPSensors.HAS_CONSUMABLE_FIRE_RESISTANCE_ITEM_EQUIPPED.key().asDerived(), true)
        .withPerformCallback(EquipConsumableFireResistanceItemAction::perform)
        .build();

    public static final Action<Marine> CONSUME_FIRE_RESISTANCE_ITEM = Action.<Marine>builder("ConsumeFireResistanceItemAction")
        .addPrecondition(MarineGOAPSensors.HAS_CONSUMABLE_FIRE_RESISTANCE_ITEM_EQUIPPED.key(), Expressions.Boolean.isTrue())
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback(
            (marine, worldState, blackboard) -> UseFireResistanceItemAction.perform(
                ConsumableFireResistanceItemStrategies::strategyFor,
                marine,
                worldState,
                blackboard
            )
        )
        .build();

    public static final Action<Marine> EQUIP_THROWABLE_FIRE_RESISTANCE_ITEM = Action.<Marine>builder(
        "EquipThrowableFireResistanceItemAction"
    )
        .addPrecondition(MarineGOAPSensors.HAS_THROWABLE_FIRE_RESISTANCE_ITEMS_IN_INVENTORY.key(), Expressions.Boolean.isTrue())
        .addEffect(MarineGOAPSensors.HAS_THROWABLE_FIRE_RESISTANCE_ITEM_EQUIPPED.key().asDerived(), true)
        .withPerformCallback(EquipThrowableResistanceItemAction::perform)
        .build();

    public static final Action<Marine> THROW_FIRE_RESISTANCE_ITEM = Action.<Marine>builder("ThrowFireResistanceItemAction")
        .addPrecondition(MarineGOAPSensors.HAS_THROWABLE_FIRE_RESISTANCE_ITEM_EQUIPPED.key(), Expressions.Boolean.isTrue())
        .addPrecondition(GOAPSensors.IS_ON_GROUND.key(), Expressions.Boolean.isTrue())
        .addEffect(GOAPSensors.HAS_FIRE_RESISTANCE.key().asDerived(), true)
        .withPerformCallback(
            (marine, worldState, blackboard) -> UseFireResistanceItemAction.perform(
                ThrowableFireResistanceItemStrategies::strategyFor,
                marine,
                worldState,
                blackboard
            )
        )
        .build();

    private MarineGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
