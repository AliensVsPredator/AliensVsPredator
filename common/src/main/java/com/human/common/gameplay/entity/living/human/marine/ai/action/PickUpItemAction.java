package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.avp.common.model.inventory.AVPInventoryHolder;
import com.just.goap.Action;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

public class PickUpItemAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.Signal perform(
        T livingEntityWithInventory,
        ItemEntity itemEntity
    ) {
        if (itemEntity.isRemoved()) {
            return Action.Signal.ABORT;
        }

        // Put target item entity in inventory.
        livingEntityWithInventory.getInventory().addItemStack(itemEntity.getItem());
        // Discard the item entity since it was picked up.
        itemEntity.discard();

        return Action.Signal.CONTINUE;
    }

    private PickUpItemAction() {
        throw new UnsupportedOperationException();
    }
}
