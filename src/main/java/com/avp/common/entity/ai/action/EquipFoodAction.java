package com.avp.common.entity.ai.action;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.util.AVPInventoryBearer;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EquipFoodAction<T extends LivingEntity & AVPInventoryBearer> extends GOAPAction<T> {

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(GOAPConstants.ITEM_TYPES_IN_INVENTORY, GOAPExpression.contains(ItemType.food())),
            new GOAPCondition<>(
                GOAPConstants.OFF_HAND_ITEM_TYPE,
                GOAPExpression.where(
                    offHandItemType -> offHandItemType != ItemType.food(),
                    "off hand item is not food"
                )
            )
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.OFF_HAND_ITEM_TYPE, ItemType.food())
        );
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var food = context.getInventory()
            .stream()
            .filter(item -> item.getItem().components().has(DataComponents.FOOD))
            .findFirst();

        food.ifPresent(itemStack -> context.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(itemStack.getItem(), 1)));

        // Sanity check to make sure the offhand has a food item.
        return context.getItemBySlot(EquipmentSlot.OFFHAND).getItem().components().has(DataComponents.FOOD);
    }
}
