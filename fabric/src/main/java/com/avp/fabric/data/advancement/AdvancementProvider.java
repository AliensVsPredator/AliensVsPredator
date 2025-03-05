package com.avp.fabric.data.advancement;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.core.AVP;
import com.avp.core.AVPResources;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;

public class AdvancementProvider extends FabricAdvancementProvider {

    private static final List<Supplier<? extends EntityType<?>>> ALIENS_TO_KILL = List.of(
        AVPEntityTypes.CHESTBURSTER,
        AVPEntityTypes.FACEHUGGER,
        AVPEntityTypes.DRONE,
        AVPEntityTypes.OVAMORPH,
        AVPEntityTypes.PRAETORIAN,
        AVPEntityTypes.QUEEN,
        AVPEntityTypes.WARRIOR
    );

    private static final List<Supplier<? extends EntityType<?>>> ROYAL_ALIENS_TO_KILL = List.of(
        AVPEntityTypes.PRAETORIAN,
        AVPEntityTypes.QUEEN
    );

    public AdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var root = Advancement.Builder.advancement()
            .display(
                AVPBlocks.RESIN.get(),
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

        var shearAnOvamorphAdvancement = Advancement.Builder.advancement()
            .parent(root)
            .display(
                Items.SHEARS,
                Component.translatable("advancements.aliens.shear_an_ovamorph.title"),
                Component.translatable("advancements.aliens.shear_an_ovamorph.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "shear_an_ovamorph",
                PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                    ItemPredicate.Builder.item().of(Items.SHEARS),
                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(AVPEntityTypes.OVAMORPH.get())))
                )
            )
            .save(consumer, AVP.MOD_ID + ":aliens/shear_an_ovamorph");

        var addChitinArmorAdvancement = addChitinArmorAdvancements(alienKillerAdvancement, consumer);

        var addPlatedChitinArmorAdvancement = addPlatedChitinArmorAdvancement(royalAlienKillerAdvancement, consumer);
    }

    private AdvancementHolder addChitinArmorAdvancements(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                ArmorItems.CHITIN_HELMET.get(),
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
                    ArmorItems.CHITIN_HELMET.get(),
                    ArmorItems.CHITIN_CHESTPLATE.get(),
                    ArmorItems.CHITIN_LEGGINGS.get(),
                    ArmorItems.CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "nether_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    ArmorItems.NETHER_CHITIN_HELMET.get(),
                    ArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                    ArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                    ArmorItems.NETHER_CHITIN_BOOTS.get()
                )
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, AVP.MOD_ID + ":aliens/chitin_armor");
    }

    private AdvancementHolder addPlatedChitinArmorAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return Advancement.Builder.advancement()
            .parent(parent)
            .display(
                ArmorItems.PLATED_CHITIN_HELMET.get(),
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
                    ArmorItems.PLATED_CHITIN_HELMET.get(),
                    ArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                    ArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                    ArmorItems.PLATED_CHITIN_BOOTS.get()
                )
            )
            .addCriterion(
                "plated_nether_chitin_armor",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    ArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                    ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                    ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                    ArmorItems.PLATED_NETHER_CHITIN_BOOTS.get()
                )
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AVP.MOD_ID + ":aliens/plated_chitin_armor");
    }

    private AdvancementHolder addAlienKillerAdvancement(AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
        return addMobsToKill(Advancement.Builder.advancement(), ALIENS_TO_KILL)
            .parent(parent)
            .display(
                AVPItems.CHITIN.get(),
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
        return addMobsToKill(Advancement.Builder.advancement(), ROYAL_ALIENS_TO_KILL)
            .parent(parent)
            .display(
                AVPItems.PLATED_CHITIN.get(),
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
                AVPItems.RAW_ROYAL_JELLY.get(),
                Component.translatable("advancements.aliens.kill_all_aliens.title"),
                Component.translatable("advancements.aliens.kill_all_aliens.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(consumer, AVP.MOD_ID + ":aliens/kill_all_aliens");
    }

    private Advancement.Builder addMobsToKill(Advancement.Builder builder, List<Supplier<? extends EntityType<?>>> supplierList) {
        supplierList.stream()
            .map(Supplier::get)
            .forEach(
            entityType -> builder.addCriterion(
                BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString(),
                KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType))
            )
        );

        return builder;
    }
}
