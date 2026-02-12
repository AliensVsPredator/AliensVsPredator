package com.blib.api.common.goap.v1.action.impl;

import com.just.goap.action.Action;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.blib.api.common.inventory.v1.BLibInventory;
import com.blib.api.common.inventory.v1.BLibInventoryHolder;

public class UnequipItemAction {

    public static <T extends LivingEntity & BLibInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        EquipmentSlot equipmentSlot
    ) {
        var targetSlotItemStack = livingEntityWithInventory.getItemBySlot(equipmentSlot);

        // Remove the item from the entity's inventory.
        var result = livingEntityWithInventory.getInventory().addItemStack(targetSlotItemStack);

        return switch (result) {
            case BLibInventory.AddResult.InventoryFull inventoryFull -> Action.Signal.ABORT;
            case BLibInventory.AddResult.Partial partial -> {
                var insertionCount = partial.count();
                targetSlotItemStack.shrink(insertionCount);
                yield Action.Signal.ABORT;
            }
            case BLibInventory.AddResult.Success success -> {
                livingEntityWithInventory.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                yield Action.Signal.CONTINUE;
            }
        };
    }

    private UnequipItemAction() {
        throw new UnsupportedOperationException();
    }
}
