package com.avp.common.entity.ai.sensor.inventory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.item.GunItem;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class HasRangedWeaponInInventorySensor<T extends LivingEntity & InventoryCarrier> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(
            GOAPConstants.HAS_RANGED_WEAPON_IN_INVENTORY,
            context.getInventory()
                .hasAnyMatching(
                    itemStack -> itemStack.getItem() instanceof BowItem
                        || itemStack.getItem() instanceof CrossbowItem
                        || itemStack.getItem() instanceof GunItem
                )
        );
    }
}
