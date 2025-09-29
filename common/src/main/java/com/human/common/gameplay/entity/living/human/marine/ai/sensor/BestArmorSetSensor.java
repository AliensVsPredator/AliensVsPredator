package com.human.common.gameplay.entity.living.human.marine.ai.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.PartialArmorSet;
import com.just.core.functional.option.Option;
import com.lib.common.gameplay.util.EnchantmentUtil;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class BestArmorSetSensor {

    public static @NotNull Option<PartialArmorSet> sense(Marine marine, PartialArmorSet currentPartialArmorSet) {
        // TODO: This shouldn't be here.
        if (marine.isUnderWater()) {
            return Option.none();
        }

        Option<ItemStack> bestHead = currentPartialArmorSet.head();
        Option<ItemStack> bestChest = currentPartialArmorSet.chest();
        Option<ItemStack> bestLegs = currentPartialArmorSet.legs();
        Option<ItemStack> bestFeet = currentPartialArmorSet.feet();

        var entries = marine.getInventory()
            .filterEntriesByItem(item -> item instanceof ArmorItem);

        for (var entry : entries) {
            var armorItem = (ArmorItem) entry.getItem();
            var itemStack = entry.copyItemStack();
            var equipmentSlot = armorItem.getEquipmentSlot();

            var current = switch (equipmentSlot) {
                case HEAD -> bestHead;
                case CHEST -> bestChest;
                case LEGS -> bestLegs;
                case FEET -> bestFeet;
                // Skip if not one of the four armor slots.
                default -> Option.<ItemStack>none();
            };

            if (isBetter(marine, itemStack, current)) {
                switch (equipmentSlot) {
                    case HEAD -> bestHead = Option.some(itemStack);
                    case CHEST -> bestChest = Option.some(itemStack);
                    case LEGS -> bestLegs = Option.some(itemStack);
                    case FEET -> bestFeet = Option.some(itemStack);
                }
            }
        }

        var newArmorSet = new PartialArmorSet(bestHead, bestChest, bestLegs, bestFeet);

        if (newArmorSet.equals(currentPartialArmorSet)) {
            return Option.none();
        }

        return Option.some(newArmorSet);
    }

    private static boolean isBetter(Marine marine, ItemStack candidate, Option<ItemStack> currentItemStackOption) {
        if (!(candidate.getItem() instanceof ArmorItem)) {
            return false;
        }

        if (currentItemStackOption.isNone()) {
            return true;
        }

        var current = currentItemStackOption.unwrap();

        if (!(current.getItem() instanceof ArmorItem)) {
            return true;
        }

        var candidateScore = armorScore(marine, candidate);
        var currentScore = armorScore(marine, current);

        return candidateScore > currentScore;
    }

    private static double armorScore(Marine marine, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ArmorItem armor)) {
            return 0;
        }

        var defense = armor.getDefense();
        var toughness = armor.getToughness();

        var prot = EnchantmentUtil.getLevel(marine.level(), itemStack, Enchantments.PROTECTION);
        var fireProt = EnchantmentUtil.getLevel(marine.level(), itemStack, Enchantments.FIRE_PROTECTION);
        var blastProt = EnchantmentUtil.getLevel(marine.level(), itemStack, Enchantments.BLAST_PROTECTION);
        var projProt = EnchantmentUtil.getLevel(marine.level(), itemStack, Enchantments.PROJECTILE_PROTECTION);
        var feather = EnchantmentUtil.getLevel(marine.level(), itemStack, Enchantments.FEATHER_FALLING);

        return defense * 1.0 +
            toughness * 0.5 +
            prot * 0.75 +
            fireProt * 0.5 +
            blastProt * 0.5 +
            projProt * 0.5 +
            feather * 0.5;
    }
}
