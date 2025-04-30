package com.avp.common.entity.ai.action;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.util.AVPInventoryBearer;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class EquipItemAction<T extends LivingEntity & AVPInventoryBearer> extends GOAPAction<T> {

    private final InteractionHand interactionHand;

    private final ItemType itemType;

    public EquipItemAction(InteractionHand interactionHand, ItemType itemType) {
        this.interactionHand = interactionHand;
        this.itemType = itemType;

        var typedIdentifier = interactionHand == InteractionHand.MAIN_HAND
            ? GOAPConstants.MAIN_HAND_ITEM_TYPE
            : GOAPConstants.OFF_HAND_ITEM_TYPE;

        addPrecondition(GOAPConstants.ITEM_TYPES_IN_INVENTORY, GOAPExpression.contains(itemType));
        addPrecondition(
            typedIdentifier,
            GOAPExpression.where(
                handItemType -> handItemType != itemType,
                typedIdentifier.identifier() + " is not " + itemType
            )
        );

        addEffect(new GOAPEffect.Value<>(typedIdentifier, itemType));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var itemStackOption = context.getInventory()
            .stream()
            .filter(item -> ItemType.getForItem(item) == itemType)
            .findFirst();

        itemStackOption.ifPresent(itemStack -> context.setItemInHand(interactionHand, new ItemStack(itemStack.getItem(), 1)));

        // Sanity check.
        return ItemType.getForItem(context.getItemInHand(interactionHand)) == itemType;
    }
}
