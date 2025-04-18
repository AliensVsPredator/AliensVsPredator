package com.avp.common.entity.ai.sensor.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class HasFoodInInventorySensor<T extends LivingEntity & InventoryCarrier> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(
            GOAPConstants.HAS_FOOD_IN_INVENTORY,
            context.getInventory().hasAnyMatching(itemStack -> itemStack.getComponents().has(DataComponents.FOOD))
        );
    }
}
