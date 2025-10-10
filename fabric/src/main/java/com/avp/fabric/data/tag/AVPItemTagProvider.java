package com.avp.fabric.data.tag;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.alien.common.registry.init.item.AlienResinBlockItems;
import com.compat.CommonItemTags;
import com.human.common.gameplay.item.GunItem;
import com.human.common.registry.init.item.HumanFerroaluminumBlockItems;
import com.human.common.registry.init.item.HumanIndustrialGlassBlockItems;
import com.human.common.registry.init.item.HumanSteelBlockItems;
import com.human.common.registry.init.item.HumanTitaniumBlockItems;
import com.predator.common.registry.init.item.PredatorArmorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;

public class AVPItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addArmors();
        addAutomatedTagItems();
        addRadioactiveItems();

        getOrCreateTagBuilder(AVPItemTags.AMMO_ITEMS)
            .add(
                AVPItems.CASELESS_BULLET.get(),
                AVPItems.HEAVY_BULLET.get(),
                AVPItems.SMALL_BULLET.get(),
                AVPItems.MEDIUM_BULLET.get(),
                AVPItems.SHOTGUN_SHELL.get(),
                AVPItems.ROCKET.get(),
                AVPItems.FUEL_TANK.get()
            );

        getOrCreateTagBuilder(AVPItemTags.HOSTILE_WEAPONS)
            .addTag(AVPItemTags.GUNS)
            .addTag(ItemTags.AXES)
            .addTag(ItemTags.SWORDS)
            .add(
                Items.BOW,
                Items.CROSSBOW
            );

        // Acid-resistant items
        getOrCreateTagBuilder(AVPItemTags.ACID_IMMUNE)
            .addTag(AVPItemTags.CHITIN_ARMORS)
            .addTag(AVPItemTags.PLATED_CHITIN_ARMORS)
            .add(
                AlienItems.CHITIN.get(),
                AlienItems.NETHER_CHITIN.get(),
                AlienItems.ABERRANT_CHITIN.get(),
                AlienItems.IRRADIATED_CHITIN.get(),
                AlienItems.PLATED_CHITIN.get(),
                AlienItems.PLATED_NETHER_CHITIN.get(),
                AlienItems.PLATED_ABERRANT_CHITIN.get(),
                AlienItems.PLATED_IRRADIATED_CHITIN.get()
            );

        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS)
            .add(
                AlienItems.OVOID_POTTERY_SHERD.get(),
                AlienItems.PARASITE_POTTERY_SHERD.get(),
                AlienItems.ROYALTY_POTTERY_SHERD.get(),
                AlienItems.VECTOR_POTTERY_SHERD.get()
            );

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.IRON_BLOCK_LIKE)
            .add(Items.IRON_BLOCK)
            .addTag(CommonItemTags.STORAGE_BLOCKS_ALUMINUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_FERROALUMINUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_STEEL)
            .addTag(CommonItemTags.STORAGE_BLOCKS_ZINC);

        getOrCreateTagBuilder(AVPItemTags.IRON_INGOT_LIKE)
            .add(Items.IRON_INGOT)
            .addTag(CommonItemTags.INGOTS_ALUMINUM)
            .addTag(CommonItemTags.INGOTS_FERROALUMINUM)
            .addTag(CommonItemTags.INGOTS_STEEL)
            .addTag(CommonItemTags.INGOTS_ZINC);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPItemTags.PLASTIC);

        TagProviderUtil.getPlasticBlockStream()
            .map(Block::asItem)
            .forEach(plasticTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS.get());
        HumanIndustrialGlassBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            ($, blockItemSupplier) -> industrialGlassBlockTagBuilder.add(blockItemSupplier.get())
        );

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_PANE.get());
        HumanIndustrialGlassBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            ($, blockItemSupplier) -> industrialGlassPaneTagBuilder.add(blockItemSupplier.get())
        );

        getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        getOrCreateTagBuilder(AVPItemTags.LITHIUM)
            .add(
                CoreBlocks.LITHIUM_BLOCK.get().asItem(),
                CoreBlocks.LITHIUM_ORE.get().asItem(),
                AVPItems.LITHIUM_DUST.get()
            );

        getOrCreateTagBuilder(ItemTags.DYEABLE)
            .addTag(AVPItemTags.MK50_ARMOR);

        getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES)
            .addTag(AVPItemTags.PREDATOR_ARMORS);

        getOrCreateTagBuilder(AVPItemTags.MELEE_WEAPONS)
            .addOptionalTag(ItemTags.AXES)
            .addOptionalTag(ItemTags.SWORDS)
            .add(
                Items.MACE
            );

        getOrCreateTagBuilder(AVPItemTags.RANGED_WEAPONS)
            .addTag(AVPItemTags.GUNS)
            .add(
                Items.BOW,
                Items.CROSSBOW
            );

        addCompatibilityTags();
    }

    private void addRadioactiveItems() {
        getOrCreateTagBuilder(AVPItemTags.RADIATION_CURE_ITEMS)
            .add(
                Items.GOLDEN_APPLE,
                Items.ENCHANTED_GOLDEN_APPLE
            );

        getOrCreateTagBuilder(AVPItemTags.RADIOACTIVE_ITEMS)
            .add(
                AVPItems.AUTUNITE_DUST.get(),
                AVPItems.URANIUM_NUGGET.get(),
                AVPItems.URANIUM_INGOT.get(),
                AlienItems.IRRADIATED_CHITIN.get(),
                AlienItems.PLATED_IRRADIATED_CHITIN.get(),
                AlienItems.IRRADIATED_RESIN_BALL.get(),
                AVPBlockItems.AUTUNITE_BLOCK.get(),
                AVPBlockItems.AUTUNITE_ORE.get(),
                AVPBlockItems.URANIUM_BLOCK.get(),
                AVPBlockItems.TRINITITE_BLOCK.get(),
                AlienResinBlockItems.IRRADIATED_RESIN.get(),
                AlienResinBlockItems.IRRADIATED_RESIN_NODE.get(),
                AlienResinBlockItems.IRRADIATED_RESIN_VEIN.get(),
                AlienResinBlockItems.IRRADIATED_RESIN_WEB.get()
            )
            .addTag(CommonItemTags.INGOTS_URANIUM);

        getOrCreateTagBuilder(AVPItemTags.URANIUM_NUGGET_LIKE)
            .addOptionalTag(CommonItemTags.NUGGETS_URANIUM)
            .add(AlienItems.IRRADIATED_CHITIN.get());
    }

    private void addAutomatedTagItems() {
        // Armor
        var headArmorTagProvider = getOrCreateTagBuilder(ItemTags.HEAD_ARMOR);
        var chestArmorTagProvider = getOrCreateTagBuilder(ItemTags.CHEST_ARMOR);
        var legArmorTagProvider = getOrCreateTagBuilder(ItemTags.LEG_ARMOR);
        var footArmorTagProvider = getOrCreateTagBuilder(ItemTags.FOOT_ARMOR);

        // Blocks
        var buttonTagProvider = getOrCreateTagBuilder(ItemTags.BUTTONS);
        var doorTagProvider = getOrCreateTagBuilder(ItemTags.DOORS);
        var fenceTagProvider = getOrCreateTagBuilder(ItemTags.FENCES);
        var slabTagProvider = getOrCreateTagBuilder(ItemTags.SLABS);
        var stairsTagProvider = getOrCreateTagBuilder(ItemTags.STAIRS);
        var trapdoorTagProvider = getOrCreateTagBuilder(ItemTags.TRAPDOORS);
        var wallTagBuilder = getOrCreateTagBuilder(ItemTags.WALLS);

        // Tools
        var axeTagProvider = getOrCreateTagBuilder(ItemTags.AXES);
        var hoeTagProvider = getOrCreateTagBuilder(ItemTags.HOES);
        var pickaxeTagProvider = getOrCreateTagBuilder(ItemTags.PICKAXES);
        var shovelTagProvider = getOrCreateTagBuilder(ItemTags.SHOVELS);

        // Weapons
        var gunTagProvider = getOrCreateTagBuilder(AVPItemTags.GUNS);
        var swordTagProvider = getOrCreateTagBuilder(ItemTags.SWORDS);

        AVPItems.getAll().forEach(deferredHolder -> {
            var item = deferredHolder.get();

            if (item instanceof ArmorItem armorItem) {
                switch (armorItem.getType()) {
                    case HELMET -> headArmorTagProvider.add(item);
                    case CHESTPLATE -> chestArmorTagProvider.add(item);
                    case LEGGINGS -> legArmorTagProvider.add(item);
                    case BOOTS -> footArmorTagProvider.add(item);
                    case BODY -> { /* NO-OP */ }
                }
            }

            if (item instanceof BlockItem blockItem) {
                var block = blockItem.getBlock();

                if (block instanceof ButtonBlock) {
                    buttonTagProvider.add(item);
                }

                if (block instanceof DoorBlock) {
                    doorTagProvider.add(item);
                }

                if (block instanceof FenceBlock) {
                    fenceTagProvider.add(item);
                }

                if (block instanceof SlabBlock) {
                    slabTagProvider.add(item);
                }

                if (block instanceof StairBlock) {
                    stairsTagProvider.add(item);
                }

                if (block instanceof TrapDoorBlock) {
                    trapdoorTagProvider.add(item);
                }

                if (block instanceof WallBlock) {
                    wallTagBuilder.add(item);
                }
            }

            if (item instanceof AxeItem) {
                axeTagProvider.add(item);
            }

            if (item instanceof GunItem) {
                gunTagProvider.add(item);
            }

            if (item instanceof HoeItem) {
                hoeTagProvider.add(item);
            }

            if (item instanceof PickaxeItem) {
                pickaxeTagProvider.add(item);
            }

            if (item instanceof ShovelItem) {
                shovelTagProvider.add(item);
            }

            if (item instanceof SwordItem) {
                swordTagProvider.add(item);
            }
        });
    }

    private void addArmors() {
        getOrCreateTagBuilder(AVPItemTags.RADIATION_RESISTANT_ARMORS)
            .add(
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.MK50_LEGGINGS.get(),
                AVPArmorItems.MK50_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.JUNGLE_PREDATOR_ARMOR)
            .add(
                PredatorArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.MK50_ARMOR)
            .add(
                AVPArmorItems.MK50_BOOTS.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.MK50_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.NETHER_CHITIN_ARMOR)
            .add(
                AlienArmorItems.NETHER_CHITIN_BOOTS.get(),
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.NETHER_CHITIN_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PRESSURE_ARMOR)
            .add(
                AVPArmorItems.PRESSURE_BOOTS.get(),
                AVPArmorItems.PRESSURE_CHESTPLATE.get(),
                AVPArmorItems.PRESSURE_HELMET.get(),
                AVPArmorItems.PRESSURE_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.ABERRANT_CHITIN_ARMOR)
            .add(
                AlienArmorItems.ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.ABERRANT_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.NORMAL_CHITIN_ARMOR)
            .add(
                AlienArmorItems.CHITIN_HELMET.get(),
                AlienArmorItems.CHITIN_CHESTPLATE.get(),
                AlienArmorItems.CHITIN_LEGGINGS.get(),
                AlienArmorItems.CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.IRRADIATED_CHITIN_ARMOR)
            .add(
                AlienArmorItems.IRRADIATED_CHITIN_HELMET.get(),
                AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.IRRADIATED_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.NETHER_CHITIN_ARMOR)
            .add(
                AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                AlienArmorItems.NETHER_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_ABERRANT_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NORMAL_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_IRRADIATED_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get()
            );

        // Start composite tags

        getOrCreateTagBuilder(AVPItemTags.CHITIN_ARMORS)
            .addTag(AVPItemTags.ABERRANT_CHITIN_ARMOR)
            .addTag(AVPItemTags.IRRADIATED_CHITIN_ARMOR)
            .addTag(AVPItemTags.NETHER_CHITIN_ARMOR)
            .addTag(AVPItemTags.NORMAL_CHITIN_ARMOR);

        getOrCreateTagBuilder(AVPItemTags.FIRE_RESISTANT_ARMORS)
            .addTag(AVPItemTags.NETHER_CHITIN_ARMOR)
            .addTag(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR);

        getOrCreateTagBuilder(AVPItemTags.PLATED_CHITIN_ARMORS)
            .addTag(AVPItemTags.PLATED_ABERRANT_CHITIN_ARMOR)
            .addTag(AVPItemTags.PLATED_IRRADIATED_CHITIN_ARMOR)
            .addTag(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .addTag(AVPItemTags.PLATED_NORMAL_CHITIN_ARMOR);

        getOrCreateTagBuilder(AVPItemTags.PREDATOR_ARMORS)
            .addTag(AVPItemTags.JUNGLE_PREDATOR_ARMOR);

        getOrCreateTagBuilder(AVPItemTags.FACEHUGGER_RESISTANT_HELMETS)
            .add(
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get()
            );
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(CommonItemTags.DIAMONDS)
            .setReplace(false)
            .add(Items.DIAMOND);

        getOrCreateTagBuilder(CommonItemTags.DUSTS_AUTUNITE)
            .setReplace(false)
            .add(AVPItems.AUTUNITE_DUST.get());

        getOrCreateTagBuilder(CommonItemTags.DUSTS_COAL)
            .setReplace(false)
            .add(AVPItems.CARBON_DUST.get());

        getOrCreateTagBuilder(CommonItemTags.DUSTS_LITHIUM)
            .setReplace(false)
            .add(AVPItems.LITHIUM_DUST.get());

        getOrCreateTagBuilder(CommonItemTags.DUSTS_REDSTONE)
            .setReplace(false)
            .add(Items.REDSTONE);

        getOrCreateTagBuilder(CommonItemTags.GEMS_DIAMOND)
            .setReplace(false)
            .addTag(CommonItemTags.DIAMONDS);

        getOrCreateTagBuilder(CommonItemTags.INGOTS)
            .setReplace(false)
            .addTag(CommonItemTags.INGOTS_ALUMINUM)
            .addTag(CommonItemTags.INGOTS_BRASS)
            .addTag(CommonItemTags.INGOTS_FERROALUMINUM)
            .addTag(CommonItemTags.INGOTS_LEAD)
            .addTag(CommonItemTags.INGOTS_STEEL)
            .addTag(CommonItemTags.INGOTS_TITANIUM)
            .addTag(CommonItemTags.INGOTS_URANIUM)
            .addTag(CommonItemTags.INGOTS_ZINC);

        getOrCreateTagBuilder(CommonItemTags.INGOTS_ALUMINUM)
            .setReplace(false)
            .add(AVPItems.ALUMINUM_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_BRASS)
            .setReplace(false)
            .add(AVPItems.BRASS_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_COPPER)
            .setReplace(false)
            .add(Items.COPPER_INGOT);

        getOrCreateTagBuilder(CommonItemTags.INGOTS_GOLD)
            .setReplace(false)
            .add(Items.GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.INGOTS_LEAD)
            .setReplace(false)
            .add(AVPItems.LEAD_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_FERROALUMINUM)
            .setReplace(false)
            .add(AVPItems.FERROALUMINUM_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_STEEL)
            .setReplace(false)
            .add(AVPItems.STEEL_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_TITANIUM)
            .setReplace(false)
            .add(AVPItems.TITANIUM_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_URANIUM)
            .setReplace(false)
            .add(AVPItems.URANIUM_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.INGOTS_ZINC)
            .setReplace(false)
            .add(AVPItems.ZINC_INGOT.get());

        getOrCreateTagBuilder(CommonItemTags.MUSIC_DISCS)
            .setReplace(false)
            .add(
                AlienItems.ALIEN_MUSIC_DISC_1.get(),
                AVPItems.PREDATOR_MUSIC_DISC_1.get()
            );

        getOrCreateTagBuilder(CommonItemTags.NUGGETS)
            .setReplace(false)
            .addTag(CommonItemTags.NUGGETS_ALUMINUM)
            .addTag(CommonItemTags.NUGGETS_BRASS)
            .addTag(CommonItemTags.NUGGETS_FERROALUMINUM)
            .addTag(CommonItemTags.NUGGETS_GOLD)
            .addTag(CommonItemTags.NUGGETS_IRON)
            .addTag(CommonItemTags.NUGGETS_LEAD)
            .addTag(CommonItemTags.NUGGETS_STEEL)
            .addTag(CommonItemTags.NUGGETS_TITANIUM)
            .addTag(CommonItemTags.NUGGETS_URANIUM)
            .addTag(CommonItemTags.NUGGETS_ZINC);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_ALUMINUM)
            .setReplace(false)
            .add(AVPItems.ALUMINUM_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_BRASS)
            .setReplace(false)
            .add(AVPItems.BRASS_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_FERROALUMINUM)
            .setReplace(false)
            .add(AVPItems.FERROALUMINUM_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_GOLD)
            .setReplace(false)
            .add(Items.GOLD_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_IRON)
            .setReplace(false)
            .add(Items.IRON_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_LEAD)
            .setReplace(false)
            .add(AVPItems.LEAD_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_STEEL)
            .setReplace(false)
            .add(AVPItems.STEEL_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_TITANIUM)
            .setReplace(false)
            .add(AVPItems.TITANIUM_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_URANIUM)
            .setReplace(false)
            .add(AVPItems.URANIUM_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_ZINC)
            .setReplace(false)
            .add(AVPItems.ZINC_NUGGET.get());

        getOrCreateTagBuilder(CommonItemTags.ORES)
            .setReplace(false)
            .addTag(CommonItemTags.ORES_ALUMINUM)
            .addTag(CommonItemTags.ORES_AUTUNITE)
            .addTag(CommonItemTags.ORES_LEAD)
            .addTag(CommonItemTags.ORES_LITHIUM)
            .addTag(CommonItemTags.ORES_MONAZITE)
            .addTag(CommonItemTags.ORES_TITANIUM)
            .addTag(CommonItemTags.ORES_ZINC);

        getOrCreateTagBuilder(CommonItemTags.ORES_ALUMINUM)
            .setReplace(false)
            .addTag(CommonItemTags.ORES_BAUXITE);

        getOrCreateTagBuilder(CommonItemTags.ORES_AUTUNITE)
            .setReplace(false)
            .add(AVPBlockItems.AUTUNITE_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_BAUXITE)
            .setReplace(false)
            .add(AVPBlockItems.BAUXITE_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_GALENA)
            .setReplace(false)
            .add(AVPBlockItems.GALENA_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_LEAD)
            .setReplace(false)
            .addTag(CommonItemTags.ORES_GALENA);

        getOrCreateTagBuilder(CommonItemTags.ORES_LITHIUM)
            .setReplace(false)
            .add(AVPBlockItems.LITHIUM_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_MONAZITE)
            .setReplace(false)
            .add(AVPBlockItems.MONAZITE_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_TITANIUM)
            .setReplace(false)
            .add(AVPBlockItems.DEEPSLATE_TITANIUM_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.ORES_ZINC)
            .setReplace(false)
            .add(AVPBlockItems.ZINC_ORE.get())
            .add(AVPBlockItems.DEEPSLATE_ZINC_ORE.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS)
            .setReplace(false)
            .addTag(CommonItemTags.RAW_MATERIALS_ALUMINUM)
            .addTag(CommonItemTags.RAW_MATERIALS_COPPER)
            .addTag(CommonItemTags.RAW_MATERIALS_IRON)
            .addTag(CommonItemTags.RAW_MATERIALS_LEAD)
            .addTag(CommonItemTags.RAW_MATERIALS_STEEL)
            .addTag(CommonItemTags.RAW_MATERIALS_TITANIUM)
            .addTag(CommonItemTags.RAW_MATERIALS_ZINC);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_ALUMINUM)
            .setReplace(false)
            .add(AVPItems.RAW_BAUXITE.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_COPPER)
            .setReplace(false)
            .add(Items.RAW_COPPER);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_IRON)
            .setReplace(false)
            .add(Items.RAW_IRON);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_LEAD)
            .setReplace(false)
            .add(AVPItems.RAW_GALENA.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_MONAZITE)
            .setReplace(false)
            .add(AVPItems.RAW_MONAZITE.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_STEEL)
            .setReplace(false)
            .add(AVPItems.RAW_CRUDE_IRON.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_TITANIUM)
            .setReplace(false)
            .add(AVPItems.RAW_TITANIUM.get());

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_ZINC)
            .setReplace(false)
            .add(AVPItems.RAW_ZINC.get());

        getOrCreateTagBuilder(CommonItemTags.RODS_WOODEN)
            .setReplace(false)
            .add(Items.STICK);

        getOrCreateTagBuilder(CommonItemTags.SILICON)
            .setReplace(false)
            .add(AVPItems.SILICON.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS)
            .setReplace(false)
            .addTag(CommonItemTags.STORAGE_BLOCKS_ALUMINUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_BRASS)
            .addTag(CommonItemTags.STORAGE_BLOCKS_FERROALUMINUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_LEAD)
            .addTag(CommonItemTags.STORAGE_BLOCKS_RAW_ALUMINUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_RAW_LEAD)
            .addTag(CommonItemTags.STORAGE_BLOCKS_RAW_TITANIUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_RAW_ZINC)
            .addTag(CommonItemTags.STORAGE_BLOCKS_STEEL)
            .addTag(CommonItemTags.STORAGE_BLOCKS_TITANIUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_URANIUM)
            .addTag(CommonItemTags.STORAGE_BLOCKS_ZINC);

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_ALUMINUM)
            .setReplace(false)
            .add(AVPBlockItems.ALUMINUM_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_BRASS)
            .setReplace(false)
            .add(AVPBlockItems.BRASS_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_FERROALUMINUM)
            .setReplace(false)
            .add(HumanFerroaluminumBlockItems.FERROALUMINUM_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_LEAD)
            .setReplace(false)
            .add(AVPBlockItems.LEAD_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_ALUMINUM)
            .setReplace(false)
            .add(AVPBlockItems.RAW_BAUXITE_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_LEAD)
            .setReplace(false)
            .add(AVPBlockItems.RAW_GALENA_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_TITANIUM)
            .setReplace(false)
            .add(AVPBlockItems.RAW_TITANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_RAW_ZINC)
            .setReplace(false)
            .add(AVPBlockItems.RAW_ZINC_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_STEEL)
            .setReplace(false)
            .add(HumanSteelBlockItems.STEEL_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_TITANIUM)
            .setReplace(false)
            .add(HumanTitaniumBlockItems.TITANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_URANIUM)
            .setReplace(false)
            .add(AVPBlockItems.URANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STORAGE_BLOCKS_ZINC)
            .setReplace(false)
            .add(AVPBlockItems.ZINC_BLOCK.get());

        getOrCreateTagBuilder(CommonItemTags.STRINGS)
            .setReplace(false)
            .add(Items.STRING);
    }
}
