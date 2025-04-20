package com.avp.common.entity.ai.sensor.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.common.item.AVPItemTags;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class InventorySensor<T extends LivingEntity & InventoryCarrier> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var itemTypesInInventory = new HashSet<ItemType>();

        for (var itemStack : context.getInventory().items) {
            if (itemStack.getComponents().has(DataComponents.FOOD)) {
                itemTypesInInventory.add(ItemType.food());
            }

            if (itemStack.is(AVPItemTags.MELEE_WEAPONS)) {
                itemTypesInInventory.add(ItemType.meleeWeapon());
            }

            if (itemStack.is(AVPItemTags.RANGED_WEAPONS)) {
                itemTypesInInventory.add(ItemType.rangedWeapon());
            }
        }

        worldState.set(GOAPConstants.HAS_FREE_INVENTORY_SLOT, context.getInventory().canAddItem(ItemStack.EMPTY));
        worldState.set(GOAPConstants.ITEM_TYPES_IN_INVENTORY, itemTypesInInventory);
        worldState.set(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.getForItem(context.getMainHandItem()));
    }
}
