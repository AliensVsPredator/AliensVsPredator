package com.avp.common.entity.ai.util;

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
            return new ItemType.Weapon(CombatResponse.FightType.MELEE);
        }

        // Ranged weapon checks
        if (itemStack.is(AVPItemTags.RANGED_WEAPONS)) {
            return new ItemType.Weapon(CombatResponse.FightType.RANGED);
        }

        return new Other(itemStack.getItem());
    }

    static ItemType food() {
        return Food.INSTANCE;
    }

    static ItemType none() {
        return None.INSTANCE;
    }

    final class Food implements ItemType {

        private static final Food INSTANCE = new Food();

        private Food() {}
    }

    final class None implements ItemType {

        private static final None INSTANCE = new None();

        private None() {}
    }

    record Weapon(CombatResponse.FightType fightType) implements ItemType {}

    record Other(Item item) implements ItemType {}
}
