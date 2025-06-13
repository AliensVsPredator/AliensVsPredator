package com.avp.fabric.data.advancement;

import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.item.AlienArmorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class AdvancementProvider extends FabricAdvancementProvider {

    // Yes we have to do this manually.
    // No, a tag will not work (because tags only work with OR conditions, not AND).
    // No, a filter on the entity types using a tag won't work (because tags aren't loaded yet when this provider runs).
    // Yes, I was very annoyed with Mojang while writing this list out.
    private static final List<EntityType<?>> ALIENS_TO_KILL = List.of(
        // Normal Aliens
        AlienEntityTypes.CHESTBURSTER.get(),
        AlienEntityTypes.CRUSHER.get(),
        AlienEntityTypes.FACEHUGGER.get(),
        AlienEntityTypes.DRONE.get(),
        AlienEntityTypes.OVOMORPH.get(),
        AlienEntityTypes.PRAETORIAN.get(),
        AlienEntityTypes.PROWLER.get(),
        AlienEntityTypes.QUEEN.get(),
        AlienEntityTypes.RUNNER.get(),
        AlienEntityTypes.WARRIOR.get(),

        // Aberrant Aliens
        AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
        AlienEntityTypes.ABERRANT_CRUSHER.get(),
        AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
        AlienEntityTypes.ABERRANT_DRONE.get(),
        AlienEntityTypes.ABERRANT_OVOMORPH.get(),
        AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
        AlienEntityTypes.ABERRANT_PROWLER.get(),
        AlienEntityTypes.ABERRANT_QUEEN.get(),
        AlienEntityTypes.ABERRANT_RUNNER.get(),
        AlienEntityTypes.ABERRANT_WARRIOR.get(),

        // Nether Aliens
        AlienEntityTypes.NETHER_CHESTBURSTER.get(),
        AlienEntityTypes.NETHER_CRUSHER.get(),
        AlienEntityTypes.NETHER_FACEHUGGER.get(),
        AlienEntityTypes.NETHER_DRONE.get(),
        AlienEntityTypes.NETHER_OVOMORPH.get(),
        AlienEntityTypes.NETHER_PRAETORIAN.get(),
        AlienEntityTypes.NETHER_PROWLER.get(),
        AlienEntityTypes.NETHER_QUEEN.get(),
        AlienEntityTypes.NETHER_RUNNER.get(),
        AlienEntityTypes.NETHER_WARRIOR.get(),

        // Royal Normal Aliens
        AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_OVOMORPH.get(),

        // Royal Aberrant Aliens
        AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get(),

        // Royal nether Aliens
        AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
        AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
        AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get()
    );

    public AdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var root = Advancement.Builder.advancement()
            .display(
                AlienResinBlocks.RESIN.get(),
                Component.translatable("advancements.aliens.root.title"),
                Component.translatable("advancements.aliens.root.description"),
                AVPResources.location("textures/gui/advancements/backgrounds/resin.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
            .save(consumer, AVP.MOD_ID + ":aliens/root");

        var alienKillerAdvancement = addAlienKillerAdvancement(root, consumer);
        var royalAlienKillerAdvancement = addRoyalAlienKillerAdvancement(alienKillerAdvancement, consumer);
        var xenocideAdvancement = addXenocideAdvancement(royalAlienKillerAdvancement, consumer);

        var shearAnOvomorphAdvancement = Advancement.Builder.advancement()
            .parent(root)
            .display(
                Items.SHEARS,
                Component.translatable("advancements.aliens.shear_an_ovomorph.title"),
                Component.translatable("advancements.aliens.shear_an_ovomorph.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "shear_an_ovomorph",
                PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                    ItemPredicate.Builder.item().of(Items.SHEARS),
                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(AVPEntityTypeTags.OVOMORPHS)))
                )
            )
            .save(consumer, AVP.MOD_ID + ":aliens/shear_an_ovomorph");

        var addChitinArmorAdvancement = addChitinArmorAdvancements(alienKillerAdvancement, consumer);

        var addPlatedChitinArmorAdvancement = addPlatedChitinArmorAdvancement(royalAlienKillerAdvancement, consumer);
    }

    private AdvancementHolder addChitinArmorAdvancements(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                AlienArmorItems.CHITIN_HELMET.get(),
                Component.translatable("advancements.aliens.chitin_armor.title"),
                Component.translatable("advancements.aliens.chitin_armor.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
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
            .save(consumer, AVP.MOD_ID + ":aliens/chitin_armor");
    }

    private AdvancementHolder addPlatedChitinArmorAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                Component.translatable("advancements.aliens.plated_chitin_armor.title"),
                Component.translatable("advancements.aliens.plated_chitin_armor.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
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
            .save(consumer, AVP.MOD_ID + ":aliens/plated_chitin_armor");
    }

    private AdvancementHolder addAlienKillerAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), "kill_an_alien", AVPEntityTypeTags.ALIENS)
            .parent(parent)
            .display(
                AlienItems.CHITIN.get(),
                Component.translatable("advancements.aliens.kill_an_alien.title"),
                Component.translatable("advancements.aliens.kill_an_alien.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AVP.MOD_ID + ":aliens/kill_an_alien");
    }

    private AdvancementHolder addRoyalAlienKillerAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), "kill_a_royal_alien", AVPEntityTypeTags.ROYAL_XENOMORPHS)
            .parent(parent)
            .display(
                AlienItems.PLATED_CHITIN.get(),
                Component.translatable("advancements.aliens.kill_a_royal_alien.title"),
                Component.translatable("advancements.aliens.kill_a_royal_alien.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AVP.MOD_ID + ":aliens/kill_a_royal_alien");
    }

    private AdvancementHolder addXenocideAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), ALIENS_TO_KILL)
            .parent(parent)
            .display(
                AlienItems.RAW_ROYAL_JELLY.get(),
                Component.translatable("advancements.aliens.kill_all_aliens.title"),
                Component.translatable("advancements.aliens.kill_all_aliens.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.AND)
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AVP.MOD_ID + ":aliens/kill_all_aliens");
    }

    private Advancement.Builder addMobsToKill(Advancement.Builder builder, String criterionKey, TagKey<EntityType<?>> entityTypeTagKey) {
        builder.addCriterion(
            criterionKey,
            KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypeTagKey))
        );
        return builder;
    }

    private Advancement.Builder addMobsToKill(Advancement.Builder builder, List<EntityType<?>> list) {
        list.forEach(
            entityType -> builder.addCriterion(
                BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString(),
                KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType))
            )
        );
        return builder;
    }
}
