package com.lib.common.gameplay.entity.ai.sensor.inventory;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.entity.ai.util.ItemType;
import com.lib.common.gameplay.goap.GOAPSensor;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

import com.avp.common.model.inventory.AVPInventoryBearer;
import com.avp.common.registry.tag.AVPItemTags;

public class InventorySensor<T extends LivingEntity & AVPInventoryBearer> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var itemTypesInInventory = new HashSet<ItemType>();

        context.getInventory().forEach(itemStack -> {
            if (itemStack.getComponents().has(DataComponents.FOOD)) {
                itemTypesInInventory.add(ItemType.food());
            }

            if (itemStack.is(AVPItemTags.MELEE_WEAPONS)) {
                itemTypesInInventory.add(ItemType.meleeWeapon());
            }

            if (itemStack.is(AVPItemTags.RANGED_WEAPONS)) {
                itemTypesInInventory.add(ItemType.rangedWeapon());
            }
        });

        worldState.set(GOAPConstants.HAS_FREE_INVENTORY_SLOT, context.getInventory().canAddItem(ItemStack.EMPTY));
        worldState.set(GOAPConstants.ITEM_TYPES_IN_INVENTORY, itemTypesInInventory);
        worldState.set(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.getForItem(context.getMainHandItem()));
        worldState.set(GOAPConstants.OFF_HAND_ITEM_TYPE, ItemType.getForItem(context.getOffhandItem()));
    }
}
