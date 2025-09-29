package com.human.common.gameplay.entity.living.human.marine.ai.sensor;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.ai.model.PartialArmorSet;
import com.just.core.functional.option.Option;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avp.common.registry.tag.AVPItemTags;

public class BestWaterbreathingArmorSetSensor {

    private static final List<TagKey<Item>> FULL_SET_WATER_BREATHING_TAGS = List.of(
        AVPItemTags.MK50_ARMOR,
        AVPItemTags.PRESSURE_ARMOR
    );

    public static Option<PartialArmorSet> sense(Marine marine, PartialArmorSet currentArmorSet) {
        // TODO: This shouldn't be here.
        if (!marine.isUnderWater()) {
            return Option.none();
        }

        // Map<tag, slot -> itemStack> from inventory
        Map<TagKey<Item>, EnumMap<EquipmentSlot, ItemStack>> inventoryCandidates = new HashMap<>();

        var entries = marine.getInventory()
            .filterEntriesByItem(item -> item instanceof ArmorItem);

        for (var entry : entries) {
            var stack = entry.copyItemStack();

            var tag = getMatchingWaterbreathingTag(stack);

            if (tag == null) {
                continue;
            }

            var slot = ((ArmorItem) stack.getItem()).getEquipmentSlot();

            inventoryCandidates
                .computeIfAbsent(tag, $ -> new EnumMap<>(EquipmentSlot.class))
                .put(slot, stack);
        }

        for (var tag : FULL_SET_WATER_BREATHING_TAGS) {
            var equipmentSlotToCandidateMap = inventoryCandidates.get(tag);

            if (equipmentSlotToCandidateMap == null) {
                continue;
            }

            // Check that we have a full set between current and inventory
            if (!hasFullSet(currentArmorSet, tag, equipmentSlotToCandidateMap)) {
                continue;
            }

            // Build PartialArmorSet of only items that need to be equipped
            var head = currentArmorSet.head().filter(s -> s.is(tag)).isSome()
                ? Option.<ItemStack>none()
                : Option.ofNullable(equipmentSlotToCandidateMap.get(EquipmentSlot.HEAD));

            var chest = currentArmorSet.chest().filter(s -> s.is(tag)).isSome()
                ? Option.<ItemStack>none()
                : Option.ofNullable(equipmentSlotToCandidateMap.get(EquipmentSlot.CHEST));

            var legs = currentArmorSet.legs().filter(s -> s.is(tag)).isSome()
                ? Option.<ItemStack>none()
                : Option.ofNullable(equipmentSlotToCandidateMap.get(EquipmentSlot.LEGS));

            var feet = currentArmorSet.feet().filter(s -> s.is(tag)).isSome()
                ? Option.<ItemStack>none()
                : Option.ofNullable(equipmentSlotToCandidateMap.get(EquipmentSlot.FEET));

            var toEquip = new PartialArmorSet(head, chest, legs, feet);

            // If all are none, nothing needs to be changed.
            if (toEquip.isEmpty()) {
                return Option.none();
            }

            return Option.some(toEquip);
        }

        return Option.none();
    }

    private static boolean hasFullSet(PartialArmorSet armorSet, TagKey<Item> tag, EnumMap<EquipmentSlot, ItemStack> invMap) {
        return (armorSet.head().filter(itemStack -> itemStack.is(tag)).isSome() || invMap.containsKey(EquipmentSlot.HEAD))
            && (armorSet.chest().filter(itemStack -> itemStack.is(tag)).isSome() || invMap.containsKey(EquipmentSlot.CHEST))
            && (armorSet.legs().filter(itemStack -> itemStack.is(tag)).isSome() || invMap.containsKey(EquipmentSlot.LEGS))
            && (armorSet.feet().filter(itemStack -> itemStack.is(tag)).isSome() || invMap.containsKey(EquipmentSlot.FEET));
    }

    private static TagKey<Item> getMatchingWaterbreathingTag(ItemStack stack) {
        for (var tag : FULL_SET_WATER_BREATHING_TAGS) {
            if (stack.is(tag)) {
                return tag;
            }
        }

        return null;
    }
}
