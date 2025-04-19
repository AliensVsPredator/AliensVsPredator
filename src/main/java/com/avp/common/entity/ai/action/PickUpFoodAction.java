package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.InventoryCarrier;

import java.util.Map;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPAction;
import com.avp.goap.expression.GOAPCondition;
import com.avp.goap.expression.GOAPConditionSet;
import com.avp.goap.expression.GOAPExpression;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class PickUpFoodAction<T extends Mob & InventoryCarrier> extends GOAPAction<T> {

    @Override
    public GOAPConditionSet createPreconditions() {
        return GOAPConditionSet.of(
            // Must be a food item nearby.
            new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isSome()),
            // Must have a free inventory slot to pick up the food item.
            new GOAPCondition<>(GOAPConstants.HAS_FREE_INVENTORY_SLOT, GOAPExpression.isTrue()),
            // Food item must be close to pick it up.
            new GOAPCondition<>(GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE, GOAPExpression.isTrue())
        );
    }

    @Override
    public GOAPWorldState createEffects() {
        return new GOAPWorldState(Map.of(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none()));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var nearestFoodItemEntityOption = worldState.getOrDefault(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none());

        nearestFoodItemEntityOption.ifSome(
            nearestFoodItemEntity -> InventoryCarrier.pickUpItem(context, context, nearestFoodItemEntityOption.unwrap())
        );

        return true;
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return context.getHealth() / context.getMaxHealth();
    }
}
