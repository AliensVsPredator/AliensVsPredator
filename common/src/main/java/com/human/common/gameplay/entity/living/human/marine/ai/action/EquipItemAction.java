package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.just.goap.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryHolder;

public class EquipItemAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        AVPInventory.Entry entry,
        InteractionHand interactionHand
    ) {
        var targetHandItemStack = livingEntityWithInventory.getItemInHand(interactionHand);

        // Remove the item from the entity's inventory.
        var itemStack = livingEntityWithInventory.getInventory().removeItemStack(entry);

        if (itemStack.isEmpty()) {
            return Action.Signal.ABORT;
        }

        // Put target hand item in inventory.
        livingEntityWithInventory.getInventory().addItemStack(targetHandItemStack);
        // Remove previously held item from hand.
        livingEntityWithInventory.setItemInHand(interactionHand, ItemStack.EMPTY);
        // Equip item.
        livingEntityWithInventory.setItemInHand(interactionHand, itemStack);

        return Action.Signal.CONTINUE;
    }

    private EquipItemAction() {
        throw new UnsupportedOperationException();
    }
}
