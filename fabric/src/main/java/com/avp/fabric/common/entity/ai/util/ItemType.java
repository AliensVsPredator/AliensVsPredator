package com.avp.fabric.common.entity.ai.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.avp.common.item.AVPItemTags;

public sealed interface ItemType {

    static ItemType getForItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return none();
        }

        if (itemStack.getItem().components().has(DataComponents.FOOD)) {
            return food();
        }

        // Melee weapon checks
        if (itemStack.is(AVPItemTags.MELEE_WEAPONS)) {
            return MeleeWeapon.INSTANCE;
        }

        // Ranged weapon checks
        if (itemStack.is(AVPItemTags.RANGED_WEAPONS)) {
            return RangedWeapon.INSTANCE;
        }

        return new Other(itemStack.getItem());
    }

    static ItemType food() {
        return Food.INSTANCE;
    }

    static ItemType meleeWeapon() {
        return MeleeWeapon.INSTANCE;
    }

    static ItemType none() {
        return None.INSTANCE;
    }

    static ItemType rangedWeapon() {
        return RangedWeapon.INSTANCE;
    }

    enum Food implements ItemType {
        INSTANCE
    }

    enum None implements ItemType {
        INSTANCE
    }

    enum MeleeWeapon implements ItemType {
        INSTANCE
    }

    enum RangedWeapon implements ItemType {
        INSTANCE
    }

    record Other(Item item) implements ItemType {}
}
