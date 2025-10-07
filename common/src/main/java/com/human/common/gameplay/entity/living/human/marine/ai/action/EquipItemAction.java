package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.just.goap.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.avp.common.model.inventory.AVPInventory;
import com.avp.common.model.inventory.AVPInventoryHolder;

public class EquipItemAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        Item item,
        InteractionHand interactionHand
    ) {
        return perform(livingEntityWithInventory, new ItemStack(item), interactionHand);
    }

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        ItemStack itemStack,
        InteractionHand interactionHand
    ) {
        var targetHandItemStack = livingEntityWithInventory.getItemInHand(interactionHand);

        // Remove the item from the entity's inventory.
        var removeResult = livingEntityWithInventory.getInventory().removeItemStack(itemStack);

        return switch (removeResult) {
            case AVPInventory.RemoveResult.InventoryEmpty inventoryEmpty -> Action.Signal.ABORT;
            case AVPInventory.RemoveResult.Partial partial -> Action.Signal.ABORT;
            case AVPInventory.RemoveResult.Success success -> {
                // Put target hand item in inventory.
                livingEntityWithInventory.getInventory().addItemStack(targetHandItemStack);
                livingEntityWithInventory.setItemInHand(interactionHand, ItemStack.EMPTY);
                // Equip item.
                livingEntityWithInventory.setItemInHand(interactionHand, itemStack);

                yield Action.Signal.CONTINUE;
            }
        };
    }

    private EquipItemAction() {
        throw new UnsupportedOperationException();
    }
}
