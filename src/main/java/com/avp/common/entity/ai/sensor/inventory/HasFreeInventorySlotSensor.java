package com.avp.common.entity.ai.sensor.inventory;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;

public class HasFreeInventorySlotSensor<T extends LivingEntity & InventoryCarrier> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(GOAPConstants.HAS_FREE_INVENTORY_SLOT, context.getInventory().canAddItem(ItemStack.EMPTY));
    }
}
