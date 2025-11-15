package com.avp.fabric.data.advancement;

import com.alien.common.data.AlienAdvancements;
import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.item.AlienArmorItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.avp.AVPResources;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class AlienAdvancementProvider {

    // Yes we have to do this manually.
    // No, a tag will not work (because tags only work with OR conditions, not AND).
    // No, a filter on the entity types using a tag won't work (because tags aren't loaded yet when this provider runs).
    // Yes, I was very annoyed with Mojang while writing this list out.
    private static final List<EntityType<?>> ALIENS_TO_KILL = List.of(
        // Normal Aliens
        AlienEntityTypes.ADOLESCENT.get(),
        AlienEntityTypes.BOILER.get(),
        AlienEntityTypes.CHESTBURSTER.get(),
        AlienEntityTypes.CRUSHER.get(),
        AlienEntityTypes.FACEHUGGER.get(),
        AlienEntityTypes.DRONE.get(),
        AlienEntityTypes.OVOMORPH.get(),
        AlienEntityTypes.PRAETORIAN.get(),
        AlienEntityTypes.PREDALIEN.get(),
        AlienEntityTypes.PREDALIEN_CHESTBURSTER.get(),
        AlienEntityTypes.PROWLER.get(),
        AlienEntityTypes.QUEEN.get(),
        AlienEntityTypes.RUNNER.get(),
        AlienEntityTypes.SPITTER.get(),
        AlienEntityTypes.WARRIOR.get(),

        // Aberrant Aliens
        AlienEntityTypes.ABERRANT_ADOLESCENT.get(),
        AlienEntityTypes.ABERRANT_BOILER.get(),
        AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
        AlienEntityTypes.ABERRANT_CRUSHER.get(),
        AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
        AlienEntityTypes.ABERRANT_DRONE.get(),
        AlienEntityTypes.ABERRANT_OVOMORPH.get(),
        AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
        AlienEntityTypes.ABERRANT_PREDALIEN.get(),
        AlienEntityTypes.ABERRANT_PREDALIEN_CHESTBURSTER.get(),
        AlienEntityTypes.ABERRANT_PROWLER.get(),
        AlienEntityTypes.ABERRANT_QUEEN.get(),
        AlienEntityTypes.ABERRANT_RUNNER.get(),
        AlienEntityTypes.ABERRANT_SPITTER.get(),
        AlienEntityTypes.ABERRANT_WARRIOR.get(),

        // Nether Aliens
        AlienEntityTypes.NETHER_ADOLESCENT.get(),
        AlienEntityTypes.NETHER_BOILER.get(),
        AlienEntityTypes.NETHER_CHESTBURSTER.get(),
        AlienEntityTypes.NETHER_CRUSHER.get(),
        AlienEntityTypes.NETHER_FACEHUGGER.get(),
        AlienEntityTypes.NETHER_DRONE.get(),
        AlienEntityTypes.NETHER_OVOMORPH.get(),
        AlienEntityTypes.NETHER_PRAETORIAN.get(),
        AlienEntityTypes.NETHER_PREDALIEN.get(),
        AlienEntityTypes.NETHER_PREDALIEN_CHESTBURSTER.get(),
        AlienEntityTypes.NETHER_PROWLER.get(),
        AlienEntityTypes.NETHER_QUEEN.get(),
        AlienEntityTypes.NETHER_RUNNER.get(),
        AlienEntityTypes.NETHER_SPITTER.get(),
        AlienEntityTypes.NETHER_WARRIOR.get(),

        // Royal Normal Aliens
        AlienEntityTypes.ROYAL_ADOLESCENT.get(),
        AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_OVOMORPH.get(),

        // Royal Aberrant Aliens
        AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get(),
        AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get(),

        // Royal nether Aliens
        AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
        AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get()
    );

    public static void generateAdvancements(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var root = Advancement.Builder.advancement()
            .display(
                AlienResinBlocks.RESIN.get(),
                AlienAdvancements.ROOT.getTitleComponent(),
                AlienAdvancements.ROOT.getDescriptionComponent(),
                AVPResources.location("textures/gui/advancements/backgrounds/resin.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
            .save(consumer, AlienAdvancements.ROOT.getResourceLocation().toString());

        var removeEmbryoWithChorusFruitAdvancement = addRemoveEmbryoWithChorusFruitAdvancement(root, consumer);

        var alienKillerAdvancement = addAlienKillerAdvancement(root, consumer);
        var royalAlienKillerAdvancement = addRoyalAlienKillerAdvancement(alienKillerAdvancement, consumer);
        var hiveBusterAdvancement = addHiveBusterAdvancement(royalAlienKillerAdvancement, consumer);
        var xenocideAdvancement = addXenocideAdvancement(royalAlienKillerAdvancement, consumer);

        var shearAnOvomorphAdvancement = Advancement.Builder.advancement()
            .parent(alienKillerAdvancement)
            .display(
                Items.SHEARS,
                AlienAdvancements.SHEAR_AN_OVOMORPH.getTitleComponent(),
                AlienAdvancements.SHEAR_AN_OVOMORPH.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                AlienAdvancements.SHEAR_AN_OVOMORPH.path(),
                PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                    ItemPredicate.Builder.item().of(Items.SHEARS),
                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(AVPEntityTypeTags.OVOMORPHS)))
                )
            )
            .save(consumer, AlienAdvancements.SHEAR_AN_OVOMORPH.getResourceLocation().toString());

        var addChitinArmorAdvancement = addChitinArmorAdvancements(alienKillerAdvancement, consumer);

        var addPlatedChitinArmorAdvancement = addPlatedChitinArmorAdvancement(royalAlienKillerAdvancement, consumer);
    }

    private static AdvancementHolder addChitinArmorAdvancements(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                AlienArmorItems.CHITIN_HELMET.get(),
                AlienAdvancements.WEAR_CHITIN_ARMOR.getTitleComponent(),
                AlienAdvancements.WEAR_CHITIN_ARMOR.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "aberrant_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.ABERRANT_CHITIN_HELMET.get(),
                    AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                    AlienArmorItems.ABERRANT_CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.CHITIN_HELMET.get(),
                    AlienArmorItems.CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.CHITIN_LEGGINGS.get(),
                    AlienArmorItems.CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "nether_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                    AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                    AlienArmorItems.NETHER_CHITIN_BOOTS.get()
                )
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AlienAdvancements.WEAR_CHITIN_ARMOR.getResourceLocation().toString());
    }

    private static AdvancementHolder addPlatedChitinArmorAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                AlienAdvancements.WEAR_PLATED_CHITIN_ARMOR.getTitleComponent(),
                AlienAdvancements.WEAR_PLATED_CHITIN_ARMOR.getDescriptionComponent(),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .addCriterion(
                "plated_aberrant_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                    AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                    AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "plated_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                    AlienArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                    AlienArmorItems.PLATED_CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "plated_nether_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                    AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                    AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                    AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get()
                )
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AlienAdvancements.WEAR_PLATED_CHITIN_ARMOR.getResourceLocation().toString());
    }

    private static AdvancementHolder addAlienKillerAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), "kill_an_alien", AVPEntityTypeTags.ALIENS)
            .parent(parent)
            .display(
                AlienItems.CHITIN.get(),
                AlienAdvancements.KILL_AN_ALIEN.getTitleComponent(),
                AlienAdvancements.KILL_AN_ALIEN.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AlienAdvancements.KILL_AN_ALIEN.getResourceLocation().toString());
    }

    private static AdvancementHolder addRoyalAlienKillerAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), "kill_a_royal_alien", AVPEntityTypeTags.ROYAL_XENOMORPHS)
            .parent(parent)
            .display(
                AlienItems.PLATED_CHITIN.get(),
                AlienAdvancements.KILL_A_ROYAL_ALIEN.getTitleComponent(),
                AlienAdvancements.KILL_A_ROYAL_ALIEN.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AlienAdvancements.KILL_A_ROYAL_ALIEN.getResourceLocation().toString());
    }

    private static AdvancementHolder addXenocideAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), ALIENS_TO_KILL)
            .parent(parent)
            .display(
                AlienBlocks.ROYAL_JELLY_BLOCK.get(),
                AlienAdvancements.KILL_ALL_ALIENS.getTitleComponent(),
                AlienAdvancements.KILL_ALL_ALIENS.getDescriptionComponent(),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.AND)
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AlienAdvancements.KILL_ALL_ALIENS.getResourceLocation().toString());
    }

    private static AdvancementHolder addHiveBusterAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .addCriterion("kill_a_hive", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .parent(parent)
            .display(
                AlienItems.RAW_ROYAL_JELLY.get(),
                AlienAdvancements.KILL_A_HIVE.getTitleComponent(),
                AlienAdvancements.KILL_A_HIVE.getDescriptionComponent(),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AlienAdvancements.KILL_A_HIVE.getResourceLocation().toString());
    }

    private static AdvancementHolder addRemoveEmbryoWithChorusFruitAdvancement(
        AdvancementHolder parent,
        Consumer<AdvancementHolder> consumer
    ) {
        return Advancement.Builder.advancement()
            .addCriterion(
                "remove_embryo_with_chorus_fruit",
                CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance())
            )
            .parent(parent)
            .display(
                Items.CHORUS_FRUIT,
                AlienAdvancements.REMOVE_EMBRYO_WITH_CHORUS_FRUIT.getTitleComponent(),
                AlienAdvancements.REMOVE_EMBRYO_WITH_CHORUS_FRUIT.getDescriptionComponent(),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(consumer, AlienAdvancements.REMOVE_EMBRYO_WITH_CHORUS_FRUIT.getResourceLocation().toString());
    }

    private static Advancement.Builder addMobsToKill(
        Advancement.Builder builder,
        String criterionKey,
        TagKey<EntityType<?>> entityTypeTagKey
    ) {
        builder.addCriterion(
            criterionKey,
            KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypeTagKey))
        );
        return builder;
    }

    private static Advancement.Builder addMobsToKill(Advancement.Builder builder, List<EntityType<?>> list) {
        list.forEach(
            entityType -> builder.addCriterion(
                BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString(),
                KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType))
            )
        );
        return builder;
    }
}
