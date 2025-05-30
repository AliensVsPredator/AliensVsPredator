package com.lib.common.gameplay.entity.ai.action;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.entity.ai.util.ItemType;
import com.lib.common.gameplay.goap.GOAPAction;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffect;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.model.inventory.AVPInventoryBearer;

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
