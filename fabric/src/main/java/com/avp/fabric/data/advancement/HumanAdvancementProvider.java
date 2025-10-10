package com.avp.fabric.data.advancement;

import com.human.common.data.HumanAdvancements;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import com.avp.AVPResources;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;

public class HumanAdvancementProvider {

    public static void generateAdvancements(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var root = Advancement.Builder.advancement()
            .display(
                HumanSteelBlocks.STEEL_BLOCK.get(),
                HumanAdvancements.ROOT.getTitleComponent(),
                HumanAdvancements.ROOT.getDescriptionComponent(),
                AVPResources.location("textures/gui/advancements/backgrounds/steel.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
            .save(consumer, HumanAdvancements.ROOT.getResourceLocation().toString());

        var blastSteelAdvancement = addBlastSteelAdvancement(root, consumer);

        var smeltPlasticAdvancement = addSmeltPlasticAdvancement(blastSteelAdvancement, consumer);

        var hasGunAdvancement = addHasGunAdvancement(smeltPlasticAdvancement, consumer);

        var equipFullArmorSetWithArmorCaseAdvancement = addEquipFullArmorSetWithArmorCaseAdvancement(smeltPlasticAdvancement, consumer);

        var smeltBrassAdvancement = addSmeltBrassAdvancement(root, consumer);

        var smeltTitaniumAdvancement = addSmeltTitaniumAdvancement(blastSteelAdvancement, consumer);

        var fillCanisterAdvancement = addFillCanisterAdvancement(smeltTitaniumAdvancement, consumer);
    }

    private static AdvancementHolder addBlastSteelAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("steel", InventoryChangeTrigger.TriggerInstance.hasItems(AVPItems.STEEL_INGOT.get()))
            .parent(parent)
            .display(
                AVPItems.STEEL_INGOT.get(),
                HumanAdvancements.BLAST_STEEL.getTitleComponent(),
                HumanAdvancements.BLAST_STEEL.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.BLAST_STEEL.getResourceLocation().toString());
    }

    private static AdvancementHolder addEquipFullArmorSetWithArmorCaseAdvancement(
        AdvancementHolder parent,
        Consumer<AdvancementHolder> consumer
    ) {
        return Advancement.Builder.advancement()
            .addCriterion(
                "equip_full_armor_set_with_armor_case",
                CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance())
            )
            .parent(parent)
            .display(
                AVPItems.ARMOR_CASE.get(),
                HumanAdvancements.EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE.getTitleComponent(),
                HumanAdvancements.EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE.getResourceLocation().toString());
    }

    private static AdvancementHolder addFillCanisterAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("fill_canister", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .parent(parent)
            .display(
                AVPItems.LAVA_CANISTER.get(),
                HumanAdvancements.FILL_CANISTER.getTitleComponent(),
                HumanAdvancements.FILL_CANISTER.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.FILL_CANISTER.getResourceLocation().toString());
    }

    private static AdvancementHolder addSmeltPlasticAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("polymer", InventoryChangeTrigger.TriggerInstance.hasItems(AVPItems.POLYMER.get()))
            .parent(parent)
            .display(
                AVPItems.POLYMER.get(),
                HumanAdvancements.SMELT_PLASTIC.getTitleComponent(),
                HumanAdvancements.SMELT_PLASTIC.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.SMELT_PLASTIC.getResourceLocation().toString());
    }

    private static AdvancementHolder addHasGunAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("gun", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(AVPItemTags.GUNS).build()))
            .parent(parent)
            .display(
                HumanGunItems.M41A_PULSE_RIFLE.get(),
                HumanAdvancements.HAS_GUN.getTitleComponent(),
                HumanAdvancements.HAS_GUN.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.HAS_GUN.getResourceLocation().toString());
    }

    private static AdvancementHolder addSmeltBrassAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("brass", InventoryChangeTrigger.TriggerInstance.hasItems(AVPItems.BRASS_INGOT.get()))
            .parent(parent)
            .display(
                AVPItems.BRASS_INGOT.get(),
                HumanAdvancements.SMELT_BRASS.getTitleComponent(),
                HumanAdvancements.SMELT_BRASS.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.SMELT_BRASS.getResourceLocation().toString());
    }

    private static AdvancementHolder addSmeltTitaniumAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("titanium", InventoryChangeTrigger.TriggerInstance.hasItems(AVPItems.TITANIUM_INGOT.get()))
            .parent(parent)
            .display(
                AVPItems.TITANIUM_INGOT.get(),
                HumanAdvancements.SMELT_TITANIUM.getTitleComponent(),
                HumanAdvancements.SMELT_TITANIUM.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, HumanAdvancements.SMELT_TITANIUM.getResourceLocation().toString());
    }
}
