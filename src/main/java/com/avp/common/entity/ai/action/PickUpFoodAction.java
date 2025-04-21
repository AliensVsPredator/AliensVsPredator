package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.Mob;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.util.AVPInventoryBearer;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class PickUpFoodAction<T extends Mob & AVPInventoryBearer> extends GOAPAction<T> {

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            // Entity must be out of combat.
            new GOAPCondition<>(GOAPConstants.COMBAT_RESPONSE, GOAPExpression.equalTo(CombatResponse.rest())),
            // Must be a food item nearby.
            new GOAPCondition<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, GOAPExpression.isSome()),
            // Must have a free inventory slot to pick up the food item.
            new GOAPCondition<>(GOAPConstants.HAS_FREE_INVENTORY_SLOT, GOAPExpression.isTrue()),
            // Food item must be close to pick it up.
            new GOAPCondition<>(GOAPConstants.IS_FOOD_TARGET_ITEM_ENTITY_IN_RANGE, GOAPExpression.isTrue())
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none())
        );
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var nearestFoodItemEntityOption = worldState.getOrDefault(GOAPConstants.NEAREST_FOOD_ITEM_ENTITY, Option.none());

        nearestFoodItemEntityOption.ifSome(
            nearestFoodItemEntity -> context.getInventory().pickUpItem(nearestFoodItemEntityOption.unwrap())
        );

        return true;
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return context.getHealth() / context.getMaxHealth();
    }
}
