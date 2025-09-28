package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.MarineGOAPKeys;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class EquipBestArmorAction {

    public static boolean perform(Marine marine, ReadableWorldState worldState, Blackboard ignored) {
        var bestArmorSetOption = worldState.getOrNull(MarineGOAPKeys.BEST_ARMOR_SET);

        if (bestArmorSetOption == null || bestArmorSetOption.isNone()) {
            return true;
        }

        var bestArmorSet = bestArmorSetOption.unwrap();

        bestArmorSet.head().ifSome(headStack -> swapEquipSlotIfDifferent(marine, headStack, EquipmentSlot.HEAD));
        bestArmorSet.chest().ifSome(chestStack -> swapEquipSlotIfDifferent(marine, chestStack, EquipmentSlot.CHEST));
        bestArmorSet.legs().ifSome(legsStack -> swapEquipSlotIfDifferent(marine, legsStack, EquipmentSlot.LEGS));
        bestArmorSet.feet().ifSome(feetStack -> swapEquipSlotIfDifferent(marine, feetStack, EquipmentSlot.FEET));

        return true;
    }

    private static void swapEquipSlotIfDifferent(Marine marine, ItemStack desiredStack, EquipmentSlot slot) {
        var currentlyEquipped = marine.getItemBySlot(slot);

        // If the desired item is the same item and has same NBT/enchantments, skip swap.
        if (ItemStack.isSameItemSameComponents(currentlyEquipped, desiredStack)) {
            return;
        }

        // Otherwise swap
        swapEquipSlot(marine, desiredStack, slot);
    }

    private static void swapEquipSlot(Marine marine, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        var copy = itemStack.copy();
        marine.getNeoInventory().removeItem(copy.getItem());
        marine.getNeoInventory().addItemStack(marine.getItemBySlot(equipmentSlot));
        marine.setItemSlot(equipmentSlot, copy);
    }
}
