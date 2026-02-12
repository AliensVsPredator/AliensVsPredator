package com.blib.api.common.goap.v1.action.impl;

import com.just.goap.action.Action;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.blib.api.common.inventory.v1.BLibInventory;
import com.blib.api.common.inventory.v1.BLibInventoryHolder;

public class EquipItemAction {

    public static <T extends LivingEntity & BLibInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        BLibInventory.Entry entry,
        EquipmentSlot equipmentSlot
    ) {
        var targetSlotItemStack = livingEntityWithInventory.getItemBySlot(equipmentSlot);

        // Remove the item from the entity's inventory.
        var itemStack = livingEntityWithInventory.getInventory().removeItemStack(entry);

        if (itemStack.isEmpty()) {
            return Action.Signal.ABORT;
        }

        // Put target slot item in inventory.
        livingEntityWithInventory.getInventory().addItemStack(targetSlotItemStack);
        // Remove previously equipped item from slot.
        livingEntityWithInventory.setItemSlot(equipmentSlot, ItemStack.EMPTY);
        // Equip item.
        livingEntityWithInventory.setItemSlot(equipmentSlot, itemStack);

        return Action.Signal.CONTINUE;
    }

    private EquipItemAction() {
        throw new UnsupportedOperationException();
    }
}
