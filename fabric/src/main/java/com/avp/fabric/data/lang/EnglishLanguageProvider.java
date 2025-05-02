package com.avp.fabric.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.creative_mode_tab.AVPCreativeModeTabs;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPItems;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.SpawnEggItems;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Villagers
        builder.add("entity.minecraft.villager.commissary", "Commissary Villager");

        // Blocks
        addBlock(builder, TempAVPBlocks.BLUEPRINT_BLOCK, "Blueprint Block");
        addBlock(builder, TempAVPBlocks.REDSTONE_GENERATOR, "Redstone Generator");
        addBlock(builder, TempAVPBlocks.DESK_TERMINAL_BLOCK, "Desk Terminal");
        addBlock(builder, TempAVPBlocks.TRIP_MINE_BLOCK, "Trip Mine");
        addBlock(builder, TempAVPBlocks.RESONATOR_BLOCK, "Resonator");
        addBlock(builder, TempAVPBlocks.AMMO_CHEST, "Ammo Chest");
        addBlock(builder, TempAVPBlocks.SENTRY_TURRET, "Sentry Turret");
        addBlock(builder, TempAVPBlocks.ASH_BLOCK, "Ash Block");
        addBlock(builder, TempAVPBlocks.NUKE_BLOCK, "Nuke Block");
        addBlock(builder, TempAVPBlocks.ROYAL_JELLY_BLOCK, "Royal Jelly Block");
        addBlock(builder, TempAVPBlocks.TRINITITE_BLOCK, "Block of Trinitite");
        addBlock(builder, TempAVPBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        addBlock(builder, TempAVPBlocks.AUTUNITE_BLOCK, "Autunite Block");
        addBlock(builder, TempAVPBlocks.AUTUNITE_ORE, "Autunite Ore");
        addBlock(builder, TempAVPBlocks.BAUXITE_ORE, "Bauxite Ore");
        addBlock(builder, TempAVPBlocks.BRASS_BLOCK, "Block of Brass");
        addBlock(builder, TempAVPBlocks.CHISELED_FERROALUMINUM, "Chiseled Ferroaluminum");
        addBlock(builder, TempAVPBlocks.CHISELED_STEEL, "Chiseled Steel");
        addBlock(builder, TempAVPBlocks.CHISELED_TITANIUM, "Chiseled Titanium");
        addBlock(builder, TempAVPBlocks.CUT_FERROALUMINUM, "Cut Ferroaluminum");
        addBlock(builder, TempAVPBlocks.CUT_FERROALUMINUM_SLAB, "Cut Ferroaluminum Slab");
        addBlock(builder, TempAVPBlocks.CUT_FERROALUMINUM_STAIRS, "Cut Ferroaluminum Stairs");
        addBlock(builder, TempAVPBlocks.CUT_STEEL, "Cut Steel");
        addBlock(builder, TempAVPBlocks.CUT_STEEL_SLAB, "Cut Steel Slab");
        addBlock(builder, TempAVPBlocks.CUT_STEEL_STAIRS, "Cut Steel Stairs");
        addBlock(builder, TempAVPBlocks.CUT_TITANIUM, "Cut Titanium");
        addBlock(builder, TempAVPBlocks.CUT_TITANIUM_SLAB, "Cut Titanium Slab");
        addBlock(builder, TempAVPBlocks.CUT_TITANIUM_STAIRS, "Cut Titanium Stairs");
        addBlock(builder, TempAVPBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(builder, TempAVPBlocks.DEEPSLATE_ZINC_ORE, "Deepslate Zinc Ore");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_BLOCK, "Block of Ferroaluminum");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_BUTTON, "Ferroaluminum Button");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_DOOR, "Ferroaluminum Door");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE, "Ferroaluminum Pressure Plate");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_TRAP_DOOR, "Ferroaluminum Trapdoor");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        addBlock(builder, TempAVPBlocks.GALENA_ORE, "Galena Ore");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS_DOOR, "Industrial Glass Door");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR, "Industrial Glass Trap Door");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS_SLAB, "Industrial Glass Slab");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS, "Industrial Glass Stairs");
        addBlock(builder, TempAVPBlocks.LEAD_BLOCK, "Block of Lead");
        addBlock(builder, TempAVPBlocks.LEAD_CHEST, "Lead Chest");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Ferroaluminum Fastened Standing Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Ferroaluminum Fastened Standing Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_GRATE_SLAB, "Ferroaluminum Grate Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS, "Ferroaluminum Grate Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_PLATING_SLAB, "Ferroaluminum Plating Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS, "Ferroaluminum Plating Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_STANDING_SLAB, "Ferroaluminum Standing Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS, "Ferroaluminum Standing Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_TREAD_SLAB, "Ferroaluminum Tread Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS, "Ferroaluminum Tread Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB, "Steel Fastened Standing Slab");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS, "Steel Fastened Standing Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_GRATE_SLAB, "Steel Grate Slab");
        addBlock(builder, TempAVPBlocks.STEEL_GRATE_STAIRS, "Steel Grate Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_PLATING_SLAB, "Steel Plating Slab");
        addBlock(builder, TempAVPBlocks.STEEL_PLATING_STAIRS, "Steel Plating Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_TREAD_SLAB, "Steel Tread Slab");
        addBlock(builder, TempAVPBlocks.STEEL_TREAD_STAIRS, "Steel Tread Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Titanium Fastened Standing Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Titanium Fastened Standing Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_GRATE_SLAB, "Titanium Grate Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_GRATE_STAIRS, "Titanium Grate Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_PLATING_SLAB, "Titanium Plating Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_PLATING_STAIRS, "Titanium Plating Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_TREAD_SLAB, "Titanium Tread Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_TREAD_STAIRS, "Titanium Tread Stairs");

        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Stairs")
        );

        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete")
        );
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Stairs")
        );
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Wall")
        );
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass")
        );
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass Pane")
        );

        addBlock(builder, TempAVPBlocks.LITHIUM_BLOCK, "Block of Lithium");
        addBlock(builder, TempAVPBlocks.LITHIUM_ORE, "Lithium Ore");
        addBlock(builder, TempAVPBlocks.MONAZITE_ORE, "Monazite Ore");
        addBlock(builder, TempAVPBlocks.NETHER_RESIN, "Nether Resin");
        addBlock(builder, TempAVPBlocks.NETHER_RESIN_NODE, "Nether Resin");
        addBlock(builder, TempAVPBlocks.NETHER_RESIN_VEIN, "Nether Resin Vein");
        addBlock(builder, TempAVPBlocks.NETHER_RESIN_WEB, "Nether Resin Web");
        addBlock(builder, TempAVPBlocks.ABERRANT_RESIN, "Aberrant Resin");
        addBlock(builder, TempAVPBlocks.ABERRANT_RESIN_NODE, "Aberrant Resin");
        addBlock(builder, TempAVPBlocks.ABERRANT_RESIN_VEIN, "Aberrant Resin Vein");
        addBlock(builder, TempAVPBlocks.ABERRANT_RESIN_WEB, "Aberrant Resin Web");
        addBlock(builder, TempAVPBlocks.IRRADIATED_RESIN, "Irradiated Resin");
        addBlock(builder, TempAVPBlocks.IRRADIATED_RESIN_NODE, "Irradiated Resin");
        addBlock(builder, TempAVPBlocks.IRRADIATED_RESIN_VEIN, "Irradiated Resin Vein");
        addBlock(builder, TempAVPBlocks.IRRADIATED_RESIN_WEB, "Irradiated Resin Web");
        addBlock(builder, TempAVPBlocks.RESIN_BRICKS, "Resin Bricks");
        addBlock(builder, TempAVPBlocks.RESIN_O, "Resin O");
        addBlock(builder, TempAVPBlocks.RESIN_RIBBED, "Resin Ribbed");
        addBlock(builder, TempAVPBlocks.RESIN_SMOOTH, "Resin Smooth");

        TempAVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding")
        );
        TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Stairs")
        );

        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding")
        );
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Stairs")
        );

        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding")
        );
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Stairs")
        );

        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic")
        );
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Stairs")
        );

        TempAVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic")
        );
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Slab")
        );
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Stairs")
        );

        addBlock(builder, TempAVPBlocks.RAW_BAUXITE_BLOCK, "Block of Raw Bauxite");
        addBlock(builder, TempAVPBlocks.RAW_GALENA_BLOCK, "Block of Raw Galena");
        addBlock(builder, TempAVPBlocks.RAW_MONAZITE_BLOCK, "Block of Raw Monazite");
        addBlock(builder, TempAVPBlocks.RAW_SILICA_BLOCK, "Block of Raw Silica");
        addBlock(builder, TempAVPBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(builder, TempAVPBlocks.RAW_ZINC_BLOCK, "Block of Raw Zinc");
        addBlock(builder, TempAVPBlocks.RAZOR_WIRE, "Razor Wire");
        addBlock(builder, TempAVPBlocks.RESIN, "Resin");
        addBlock(builder, TempAVPBlocks.RESIN_NODE, "Resin");
        addBlock(builder, TempAVPBlocks.RESIN_VEIN, "Resin Vein");
        addBlock(builder, TempAVPBlocks.RESIN_WEB, "Resin Web");
        addBlock(builder, TempAVPBlocks.SILICA_GRAVEL, "Silica Gravel");
        addBlock(builder, TempAVPBlocks.STEEL_BARS, "Steel Bars");
        addBlock(builder, TempAVPBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(builder, TempAVPBlocks.STEEL_BUTTON, "Steel Button");
        addBlock(builder, TempAVPBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        addBlock(builder, TempAVPBlocks.STEEL_COLUMN, "Steel Column");
        addBlock(builder, TempAVPBlocks.STEEL_DOOR, "Steel Door");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        addBlock(builder, TempAVPBlocks.STEEL_GRATE, "Steel Grate");
        addBlock(builder, TempAVPBlocks.STEEL_PRESSURE_PLATE, "Steel Pressure Plate");
        addBlock(builder, TempAVPBlocks.STEEL_PLATING, "Steel Plating");
        addBlock(builder, TempAVPBlocks.STEEL_SIDING, "Steel Siding");
        addBlock(builder, TempAVPBlocks.STEEL_STANDING, "Steel Standing");
        addBlock(builder, TempAVPBlocks.STEEL_TRAP_DOOR, "Steel Trapdoor");
        addBlock(builder, TempAVPBlocks.STEEL_TREAD, "Steel Tread");
        addBlock(builder, TempAVPBlocks.TITANIUM_BLOCK, "Block of Titanium");
        addBlock(builder, TempAVPBlocks.TITANIUM_BUTTON, "Titanium Button");
        addBlock(builder, TempAVPBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        addBlock(builder, TempAVPBlocks.TITANIUM_COLUMN, "Titanium Column");
        addBlock(builder, TempAVPBlocks.TITANIUM_DOOR, "Titanium Door");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        addBlock(builder, TempAVPBlocks.TITANIUM_GRATE, "Titanium Grate");
        addBlock(builder, TempAVPBlocks.TITANIUM_PRESSURE_PLATE, "Titanium Pressure Plate");
        addBlock(builder, TempAVPBlocks.TITANIUM_PLATING, "Titanium Plating");
        addBlock(builder, TempAVPBlocks.TITANIUM_SIDING, "Titanium Siding");
        addBlock(builder, TempAVPBlocks.TITANIUM_STANDING, "Titanium Standing");
        addBlock(builder, TempAVPBlocks.TITANIUM_TRAP_DOOR, "Titanium Trapdoor");
        addBlock(builder, TempAVPBlocks.TITANIUM_TREAD, "Titanium Tread");
        addBlock(builder, TempAVPBlocks.URANIUM_BLOCK, "Block of Uranium");
        addBlock(builder, TempAVPBlocks.ZINC_BLOCK, "Block of Zinc");
        addBlock(builder, TempAVPBlocks.ZINC_ORE, "Zinc Ore");
        addBlock(builder, TempAVPBlocks.INDUSTRIAL_FURNACE, "Industrial Furnace");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_SLAB, "Ferroaluminum Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_STAIRS, "Ferroaluminum Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_SLAB, "Steel Slab");
        addBlock(builder, TempAVPBlocks.STEEL_STAIRS, "Steel Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_SLAB, "Titanium Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_STAIRS, "Titanium Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_SIDING_SLAB, "Ferroaluminum Siding Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS, "Ferroaluminum Siding Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_SIDING_SLAB, "Steel Siding Slab");
        addBlock(builder, TempAVPBlocks.STEEL_SIDING_STAIRS, "Steel Siding Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_SIDING_SLAB, "Titanium Siding Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_SIDING_STAIRS, "Titanium Siding Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_STANDING_SLAB, "Steel Standing Slab");
        addBlock(builder, TempAVPBlocks.STEEL_STANDING_STAIRS, "Steel Standing Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_STANDING_SLAB, "Titanium Standing Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_STANDING_STAIRS, "Titanium Standing Stairs");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Ferroaluminum Fastened Siding Slab");
        addBlock(builder, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Ferroaluminum Fastened Siding Stairs");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB, "Steel Fastened Siding Slab");
        addBlock(builder, TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS, "Steel Fastened Siding Stairs");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Titanium Fastened Siding Slab");
        addBlock(builder, TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Titanium Fastened Siding Stairs");

        // Creative Mode Tabs
        builder.add(AVPCreativeModeTabs.BLOCKS_KEY, "AVP Blocks");
        builder.add(AVPCreativeModeTabs.COLORED_BLOCKS_KEY, "AVP Colored Blocks");
        builder.add(AVPCreativeModeTabs.COMBAT_KEY, "AVP Combat");
        builder.add(AVPCreativeModeTabs.INGREDIENTS_KEY, "AVP Ingredients");
        builder.add(AVPCreativeModeTabs.SPAWN_EGGS_KEY, "AVP Spawn Eggs");
        builder.add(AVPCreativeModeTabs.TOOLS_AND_UTILITIES_KEY, "AVP Tools & Utilities");

        // Entities
        builder.add(AVPEntityTypes.ACID, "Acid");
        builder.add(AVPEntityTypes.CHESTBURSTER, "Chestburster");
        builder.add(AVPEntityTypes.DRONE, "Drone");
        builder.add(AVPEntityTypes.FACEHUGGER, "Facehugger");
        builder.add(AVPEntityTypes.OVAMORPH, "Ovamorph");
        builder.add(AVPEntityTypes.PRAETORIAN, "Praetorian");
        builder.add(AVPEntityTypes.QUEEN, "Queen");
        builder.add(AVPEntityTypes.WARRIOR, "Warrior");
        builder.add(AVPEntityTypes.YAUTJA, "Yautja");
        builder.add(AVPEntityTypes.ROCKET, "Rocket");
        builder.add(TempAVPEntityTypes.GRENADE_THROWN.get(), "Grenade");
        builder.add(AVPEntityTypes.SHURIKEN, "Shuriken");
        builder.add(AVPEntityTypes.SMART_DISC, "Smart Disc");
        builder.add(AVPEntityTypes.BULLET, "Bullet");
        builder.add(AVPEntityTypes.MARINE, "Marine");

        // Combat Items
        addItem(builder, AVPItems.SHURIKEN, "Shuriken");
        addItem(builder, AVPItems.SMART_DISC, "Smart Disc");
        addItem(builder, TempAVPItems.GRENADE, "Grenade");
        addItem(builder, TempAVPItems.GRENADE_INCENDIARY, "Incendiary Grenade");
        addItem(builder, TempAVPItems.GRENADE_IRRADIATED, "Irradiated Grenade");
        addItem(builder, TempAVPItems.CASELESS_BULLET, "Caseless Bullet");
        addItem(builder, AVPArmorItems.ABERRANT_CHITIN_BOOTS, "Aberrant Chitin Boots");
        addItem(builder, AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE, "Aberrant Chitin Chestplate");
        addItem(builder, AVPArmorItems.ABERRANT_CHITIN_HELMET, "Aberrant Chitin Helmet");
        addItem(builder, AVPArmorItems.ABERRANT_CHITIN_LEGGINGS, "Aberrant Chitin Leggings");
        addItem(builder, AVPArmorItems.CHITIN_BOOTS, "Chitin Boots");
        addItem(builder, AVPArmorItems.CHITIN_CHESTPLATE, "Chitin Chestplate");
        addItem(builder, AVPArmorItems.CHITIN_HELMET, "Chitin Helmet");
        addItem(builder, AVPArmorItems.CHITIN_LEGGINGS, "Chitin Leggings");
        addItem(builder, AVPArmorItems.IRRADIATED_CHITIN_BOOTS, "Irradiated Chitin Boots");
        addItem(builder, AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE, "Irradiated Chitin Chestplate");
        addItem(builder, AVPArmorItems.IRRADIATED_CHITIN_HELMET, "Irradiated Chitin Helmet");
        addItem(builder, AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS, "Irradiated Chitin Leggings");
        addItem(builder, AVPItems.F903WE_RIFLE, "F903WE Rifle");
        addItem(builder, AVPItems.FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol)");
        addItem(builder, TempAVPItems.FUEL_TANK, "Fuel Tank");
        addItem(builder, TempAVPItems.HEAVY_BULLET, "Heavy Bullet");
        addItem(builder, AVPArmorItems.JUNGLE_PREDATOR_BOOTS, "Predator Boots");
        addItem(builder, AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE, "Predator Chestplate");
        addItem(builder, AVPArmorItems.JUNGLE_PREDATOR_HELMET, "Predator Helmet");
        addItem(builder, AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS, "Predator Leggings");
        addItem(builder, AVPItems.M37_12_SHOTGUN, "M37-12 Shotgun");
        addItem(builder, AVPItems.M41A_PULSE_RIFLE, "M41A Pulse Rifle");
        addItem(builder, AVPItems.M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle");
        addItem(builder, AVPItems.M4RA_BATTLE_RIFLE, "M4RA Battle Rifle");
        addItem(builder, AVPItems.M56_SMARTGUN, "M56 Smartgun");
        addItem(builder, AVPItems.M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher");
        addItem(builder, AVPItems.M88MOD4_COMBAT_PISTOL, "88 Mod 4 Combat Pistol");
        addItem(builder, TempAVPItems.MEDIUM_BULLET, "Medium Bullet");
        addItem(builder, AVPArmorItems.MK50_BOOTS, "MK50 Boots");
        addItem(builder, AVPArmorItems.MK50_CHESTPLATE, "MK50 Chestplate");
        addItem(builder, AVPArmorItems.MK50_HELMET, "MK50 Helmet");
        addItem(builder, AVPArmorItems.MK50_LEGGINGS, "MK50 Leggings");
        addItem(builder, AVPArmorItems.NETHER_CHITIN_BOOTS, "Nether Chitin Boots");
        addItem(builder, AVPArmorItems.NETHER_CHITIN_CHESTPLATE, "Nether Chitin Chestplate");
        addItem(builder, AVPArmorItems.NETHER_CHITIN_HELMET, "Nether Chitin Helmet");
        addItem(builder, AVPArmorItems.NETHER_CHITIN_LEGGINGS, "Nether Chitin Leggings");
        addItem(builder, AVPItems.OLD_PAINLESS, "Old Painless");
        addItem(builder, AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS, "Plated Aberrant Chitin Boots");
        addItem(builder, AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE, "Plated Aberrant Chitin Chestplate");
        addItem(builder, AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET, "Plated Aberrant Chitin Helmet");
        addItem(builder, AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS, "Plated Aberrant Chitin Leggings");
        addItem(builder, AVPArmorItems.PLATED_CHITIN_BOOTS, "Plated Chitin Boots");
        addItem(builder, AVPArmorItems.PLATED_CHITIN_CHESTPLATE, "Plated Chitin Chestplate");
        addItem(builder, AVPArmorItems.PLATED_CHITIN_HELMET, "Plated Chitin Helmet");
        addItem(builder, AVPArmorItems.PLATED_CHITIN_LEGGINGS, "Plated Chitin Leggings");
        addItem(builder, AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS, "Plated Irradiated Chitin Boots");
        addItem(builder, AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE, "Plated Irradiated Chitin Chestplate");
        addItem(builder, AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET, "Plated Irradiated Chitin Helmet");
        addItem(builder, AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS, "Plated Irradiated Chitin Leggings");
        addItem(builder, AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Plated Nether Chitin Boots");
        addItem(builder, AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Plated Nether Chitin Chestplate");
        addItem(builder, AVPArmorItems.PLATED_NETHER_CHITIN_HELMET, "Plated Nether Chitin Helmet");
        addItem(builder, AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Plated Nether Chitin Leggings");
        addItem(builder, AVPArmorItems.PRESSURE_BOOTS, "Pressure Boots");
        addItem(builder, AVPArmorItems.PRESSURE_CHESTPLATE, "Pressure Chestplate");
        addItem(builder, AVPArmorItems.PRESSURE_HELMET, "Pressure Helmet");
        addItem(builder, AVPArmorItems.PRESSURE_LEGGINGS, "Pressure Leggings");
        addItem(builder, TempAVPItems.ROCKET, "Rocket");
        addItem(builder, TempAVPItems.SHOTGUN_SHELL, "Shotgun Shell");
        addItem(builder, TempAVPItems.SMALL_BULLET, "Small Bullet");
        addItem(builder, AVPArmorItems.STEEL_BOOTS, "Steel Boots");
        addItem(builder, AVPArmorItems.STEEL_CHESTPLATE, "Steel Chestplate");
        addItem(builder, AVPArmorItems.STEEL_HELMET, "Steel Helmet");
        addItem(builder, AVPArmorItems.STEEL_LEGGINGS, "Steel Leggings");
        addItem(builder, AVPArmorItems.TACTICAL_BOOTS, "Tactical Boots");
        addItem(builder, AVPArmorItems.TACTICAL_CHESTPLATE, "Tactical Chestplate");
        addItem(builder, AVPArmorItems.TACTICAL_HELMET, "Tactical Helmet");
        addItem(builder, AVPArmorItems.TACTICAL_LEGGINGS, "Tactical Leggings");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_BOOTS, "Tactical Camo Boots");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_CHESTPLATE, "Tactical Camo Chestplate");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_HELMET, "Tactical Camo Helmet");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_LEGGINGS, "Tactical Camo Leggings");
        addItem(builder, AVPArmorItems.TITANIUM_BOOTS, "Titanium Boots");
        addItem(builder, AVPArmorItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        addItem(builder, AVPArmorItems.TITANIUM_HELMET, "Titanium Helmet");
        addItem(builder, AVPArmorItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        addItem(builder, AVPItems.ZX_76_SHOTGUN, "ZX-76 Shotgun");

        // Ingredient Items
        addItem(builder, TempAVPItems.NUCLEAR_BATTERY, "Nuclear Battery");
        addItem(builder, TempAVPItems.REDSTONE_CRYSTAL, "Redstone Crystal");
        addItem(builder, TempAVPItems.SERVO, "Servo");
        addItem(builder, TempAVPItems.SPEAKER, "Speaker");
        addItem(builder, TempAVPItems.ALUMINUM_INGOT, "Aluminum Ingot");
        addItem(builder, TempAVPItems.ALIEN_MUSIC_DISC_1, "Music Disc");
        addItem(builder, TempAVPItems.PREDATOR_MUSIC_DISC_1, "Music Disc");
        addItem(builder, TempAVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT, "Disc Fragment");
        addItem(builder, TempAVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT, "Disc Fragment");
        builder.add(TempAVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT.get().getDescriptionId() + ".desc", "Music Disc - Silver Smile");
        builder.add(TempAVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT.get().getDescriptionId() + ".desc", "Music Disc - Hunter");
        addItem(builder, TempAVPItems.AUTUNITE_DUST, "Autunite Dust");
        addItem(builder, TempAVPItems.BARREL, "Barrel");
        addItem(builder, TempAVPItems.BATTERY_PACK, "Battery Pack");
        addItem(builder, TempAVPItems.BLUEPRINT_F903WE_RIFLE, "F903WE Rifle Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol) Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M37_12_SHOTGUN, "M37-12 Shotgun Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "M41A Pulse Rifle Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "M4RA Battle Rifle Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M56_SMARTGUN, "M56 Smartgun Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "M88 Mod 4 Combat Pistol Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_OLD_PAINLESS, "Old Painless Blueprint");
        addItem(builder, TempAVPItems.BLUEPRINT_ZX_76_SHOTGUN, "ZX-76 Shotgun Blueprint");
        addItem(builder, TempAVPItems.BRASS_INGOT, "Brass Ingot");
        addItem(builder, TempAVPItems.BULLET_TIP, "Bullet Tip");
        addItem(builder, TempAVPItems.CAPACITOR, "Capacitor");
        addItem(builder, TempAVPItems.CARBON_DUST, "Carbon Dust");
        addItem(builder, TempAVPItems.CASELESS_CARTRIDGE, "Caseless Cartridge");
        addItem(builder, TempAVPItems.CHITIN, "Chitin");
        addItem(builder, TempAVPItems.CPU, "CPU");
        addItem(builder, TempAVPItems.DIODE, "Diode");
        addItem(builder, TempAVPItems.FERROALUMINUM_INGOT, "Ferroaluminum Ingot");
        addItem(builder, TempAVPItems.GRIP, "Grip");
        addItem(builder, TempAVPItems.HEAVY_CASING, "Heavy Casing");
        addItem(builder, TempAVPItems.INTEGRATED_CIRCUIT, "Integrated Circuit");
        addItem(builder, TempAVPItems.LEAD_INGOT, "Lead Ingot");
        addItem(builder, TempAVPItems.LED, "LED");
        addItem(builder, TempAVPItems.LED_DISPLAY, "LED Display");
        addItem(builder, TempAVPItems.LITHIUM_DUST, "Lithium Dust");
        addItem(builder, TempAVPItems.MEDIUM_CASING, "Rifle Casing");
        addItem(builder, TempAVPItems.MINIGUN_BARREL, "Minigun Barrel");
        addItem(builder, TempAVPItems.NEODYMIUM_MAGNET, "Neodymium Magnet");
        addItem(builder, TempAVPItems.NETHER_CHITIN, "Nether Chitin");
        addItem(builder, TempAVPItems.NETHER_RESIN_BALL, "Nether Resin Ball");
        addItem(builder, TempAVPItems.OVOID_POTTERY_SHERD, "Ovoid Pottery Sherd");
        addItem(builder, TempAVPItems.PARASITE_POTTERY_SHERD, "Parasite Pottery Sherd");
        addItem(builder, TempAVPItems.ROYALTY_POTTERY_SHERD, "Royalty Pottery Sherd");
        addItem(builder, TempAVPItems.PLATED_CHITIN, "Plated Chitin");
        addItem(builder, TempAVPItems.PLATED_NETHER_CHITIN, "Plated Nether Chitin");
        addItem(builder, TempAVPItems.POLYMER, "Polymer");
        addItem(builder, TempAVPItems.RAW_BAUXITE, "Raw Bauxite");
        addItem(builder, TempAVPItems.RAW_BRASS, "Raw Brass");
        addItem(builder, TempAVPItems.RAW_CRUDE_IRON, "Raw Crude Iron");
        addItem(builder, TempAVPItems.RAW_FERROBAUXITE, "Raw Ferrobauxite");
        addItem(builder, TempAVPItems.RAW_GALENA, "Raw Galena");
        addItem(builder, TempAVPItems.RAW_MONAZITE, "Raw Monazite");
        addItem(builder, TempAVPItems.RAW_ROYAL_JELLY, "Raw Royal Jelly");
        addItem(builder, AVPItems.POISON_JELLY, "Poison Jelly");
        addItem(builder, TempAVPItems.RAW_SILICA, "Raw Silica");
        addItem(builder, TempAVPItems.RAW_TITANIUM, "Raw Titanium");
        addItem(builder, TempAVPItems.RAW_ZINC, "Raw Zinc");
        addItem(builder, TempAVPItems.RECEIVER, "Receiver");
        addItem(builder, TempAVPItems.REGULATOR, "Regulator");
        addItem(builder, TempAVPItems.RESIN_BALL, "Resin Ball");
        addItem(builder, TempAVPItems.RESISTOR, "Resistor");
        addItem(builder, TempAVPItems.ROCKET_BARREL, "Rocket Barrel");
        addItem(builder, TempAVPItems.SHOTGUN_CASING, "Shotgun Casing");
        addItem(builder, TempAVPItems.SMALL_CASING, "Pistol Casing");
        addItem(builder, TempAVPItems.SMART_BARREL, "Smart Barrel");
        addItem(builder, TempAVPItems.SMART_RECEIVER, "Smart Receiver");
        addItem(builder, TempAVPItems.STEEL_INGOT, "Steel Ingot");
        addItem(builder, TempAVPItems.STOCK, "Stock");
        addItem(builder, TempAVPItems.TITANIUM_INGOT, "Titanium Ingot");
        addItem(builder, TempAVPItems.TRANSISTOR, "Transistor");
        addItem(builder, TempAVPItems.URANIUM_INGOT, "Uranium Ingot");
        addItem(builder, TempAVPItems.VECTOR_POTTERY_SHERD, "Vector Pottery Sherd");
        addItem(builder, TempAVPItems.VERITANIUM_SHARD, "Veritanium Shard");
        addItem(builder, TempAVPItems.ZINC_INGOT, "Zinc Ingot");
        addItem(builder, TempAVPItems.ALUMINUM_NUGGET, "Aluminum Nugget");
        addItem(builder, TempAVPItems.BRASS_NUGGET, "Brass Nugget");
        addItem(builder, TempAVPItems.FERROALUMINUM_NUGGET, "Ferroaluminum Nugget");
        addItem(builder, TempAVPItems.LEAD_NUGGET, "Lead Nugget");
        addItem(builder, TempAVPItems.STEEL_NUGGET, "Steel Nugget");
        addItem(builder, TempAVPItems.TITANIUM_NUGGET, "Titanium Nugget");
        addItem(builder, TempAVPItems.URANIUM_NUGGET, "Uranium Nugget");
        addItem(builder, TempAVPItems.ZINC_NUGGET, "Zinc Nugget");
        addItem(builder, TempAVPItems.ABERRANT_RESIN_BALL, "Aberrant Resin Ball");
        addItem(builder, TempAVPItems.ABERRANT_CHITIN, "Aberrant Chitin");
        addItem(builder, TempAVPItems.PLATED_ABERRANT_CHITIN, "Plated Aberrant Chitin");
        addItem(builder, TempAVPItems.IRRADIATED_RESIN_BALL, "Irradiated Resin Ball");
        addItem(builder, TempAVPItems.IRRADIATED_CHITIN, "Irradiated Chitin");
        addItem(builder, TempAVPItems.PLATED_IRRADIATED_CHITIN, "Plated Irradiated Chitin");

        // Tools & Utilities Items
        addItem(builder, TempAVPItems.ARMOR_CASE, "Armor Case");
        addItem(builder, TempAVPItems.CANISTER, "Canister");
        addItem(builder, TempAVPItems.WATER_CANISTER, "Water Canister");
        addItem(builder, TempAVPItems.LAVA_CANISTER, "Lava Canister");
        addItem(builder, TempAVPItems.MILK_CANISTER, "Milk Canister");
        addItem(builder, TempAVPItems.POWDER_SNOW_CANISTER, "Powder Snow Canister");
        addItem(builder, TempAVPItems.STEEL_AXE, "Steel Axe");
        addItem(builder, TempAVPItems.STEEL_HOE, "Steel Hoe");
        addItem(builder, TempAVPItems.STEEL_PICKAXE, "Steel Pickaxe");
        addItem(builder, TempAVPItems.STEEL_SHOVEL, "Steel Shovel");
        addItem(builder, TempAVPItems.STEEL_SWORD, "Steel Sword");
        addItem(builder, TempAVPItems.TITANIUM_AXE, "Titanium Axe");
        addItem(builder, TempAVPItems.TITANIUM_HOE, "Titanium Hoe");
        addItem(builder, TempAVPItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        addItem(builder, TempAVPItems.TITANIUM_SHOVEL, "Titanium Shovel");
        addItem(builder, TempAVPItems.TITANIUM_SWORD, "Titanium Sword");
        addItem(builder, TempAVPItems.VERITANIUM_AXE, "Veritanium Axe");
        addItem(builder, TempAVPItems.VERITANIUM_HOE, "Veritanium Hoe");
        addItem(builder, TempAVPItems.VERITANIUM_PICKAXE, "Veritanium Pickaxe");
        addItem(builder, TempAVPItems.VERITANIUM_SHOVEL, "Veritanium Shovel");
        addItem(builder, TempAVPItems.VERITANIUM_SWORD, "Veritanium Sword");

        // Spawn Egg Items
        addItem(builder, SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Aberrant Chestburster Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Aberrant Drone Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Aberrant Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG, "Aberrant Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Aberrant Praetorian Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Aberrant Warrior Spawn Egg");
        addItem(builder, SpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG, "Aberrant Queen Spawn Egg");
        addItem(builder, SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Chestburster Spawn Egg");
        addItem(builder, SpawnEggItems.DRONE_SPAWN_EGG, "Drone Spawn Egg");
        addItem(builder, SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Nether Chestburster Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Nether Drone Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Nether Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Nether Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Nether Praetorian Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Nether Warrior Spawn Egg");
        addItem(builder, SpawnEggItems.NETHER_QUEEN_SPAWN_EGG, "Nether Queen Spawn Egg");
        addItem(builder, SpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG, "Irradiated Drone Spawn Egg");
        addItem(builder, SpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG, "Irradiated Praetorian Spawn Egg");
        addItem(builder, SpawnEggItems.IRRADIATED_QUEEN_SPAWN_EGG, "Irradiated Queen Spawn Egg");
        addItem(builder, SpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG, "Irradiated Warrior Spawn Egg");
        addItem(builder, SpawnEggItems.OVAMORPH_SPAWN_EGG, "Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Praetorian Spawn Egg");
        addItem(builder, SpawnEggItems.QUEEN_SPAWN_EGG, "Queen Spawn Egg");
        addItem(builder, SpawnEggItems.WARRIOR_SPAWN_EGG, "Warrior Spawn Egg");
        addItem(builder, SpawnEggItems.YAUTJA_SPAWN_EGG, "Yautja Spawn Egg");
        addItem(builder, SpawnEggItems.MARINE_SPAWN_EGG, "Marine Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_OVAMORPH_SPAWN_EGG, "Royal Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG, "Royal Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG, "Royal Chestburster Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_NETHER_OVAMORPH_SPAWN_EGG, "Royal Nether Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG, "Royal Nether Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG, "Royal Nether Chestburster Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG, "Royal Aberrant Ovamorph Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG, "Royal Aberrant Facehugger Spawn Egg");
        addItem(builder, SpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG, "Royal Aberrant Chestburster Spawn Egg");

        // Sounds
        addSound(builder, AVPSoundEvents.BLOCK_ACID_BURN, "Acid burns");
        addSound(builder, AVPSoundEvents.BLOCK_RESIN_SPREAD, "Xenomorph spreads resin");

        addSound(builder, AVPSoundEvents.ENTITY_OVAMORPH_HATCH, "Ovamorph hatches");
        addSound(builder, AVPSoundEvents.ENTITY_OVAMORPH_LAID, "Queen lays egg");
        addSound(builder, AVPSoundEvents.ENTITY_OVAMORPH_ROOT, "Ovamorph takes root");
        addSound(builder, AVPSoundEvents.ENTITY_OVAMORPH_SHEAR, "Ovamorph de-roots");

        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_ARM_ATTACK, "Queen attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_BACK_HAND_ATTACK, "Queen back hand attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_RAM_ATTACK, "Queen ram attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_SCREAM, "Queen screams");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_TAIL_ATTACK, "Queen tail attacks");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_DEATH, "Queen dies");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_HURT, "Queen hurts");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_IDLE, "Queen breathes");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_STEP_THUMP, "Queen steps");
        addSound(builder, AVPSoundEvents.ENTITY_QUEEN_STEP_THUMP_ROCK, "Queen steps");

        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, "Xenomorph attacks");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_DEATH, "Xenomorph dies");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_HISS, "Xenomorph hisses");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_HURT, "Xenomorph hurts");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_IDLE, "Xenomorph breathes");
        addSound(builder, AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, "Xenomorph lunges");

        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN, "Chitin armor squishes");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50, "MK50 armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE, "Pressure armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL, "Steel armor clanks");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL, "Tactical armor rustles");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM, "Titanium armor clanks");
        addSound(builder, AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM, "Veritanium armor clanks");

        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH, "Flamethrower finishes reloading");
        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START, "Flamethrower reloads");
        addSound(builder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT, "Flamethrower shoots");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT, "Bullet ricochets off of dirt");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC, "Bullet ricochets");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS, "Bullet ricochets off of glass");
        addSound(builder, AVPSoundEvents.WEAPON_FX_RICOCHET_METAL, "Bullet ricochets off of metal");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_RELOAD, "Gun reloads");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_SHOOT, "Gun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_GENERIC_SHOOT_FAIL, "Gun fails to shoot");
        addSound(builder, AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT, "M37-12 Shotgun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT, "M41A Pulse Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT, "M42A3 Sniper Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT, "M4RA Battle Rifle shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT, "M56 Smartgun shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH, "M6B Rocket Launcher finishes reloading");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START, "M6B Rocket Launcher reloads");
        addSound(builder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT, "M6B Rocket Launcher shoots");
        addSound(builder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD, "M88 Mod 4 Combat Pistol reloads");
        addSound(builder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT, "M88 Mod 4 Combat Pistol shoots");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT, "Old painless shoots");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH, "Old Painless stops shooting");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING, "Old Painless barrel spins");
        addSound(builder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START, "Old Painless barrel starts spinning");
        addSound(builder, AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT, "ZX-76 Shotgun shoots");
        addSound(builder, AVPSoundEvents.JUKEBOX_SOUNDS_ALIEN_MUSIC_1, "Silver Smile plays");
        addSound(builder, AVPSoundEvents.JUKEBOX_SOUNDS_PREDATOR_MUSIC_1, "Hunter plays");

        // Jukebox Sounds
        builder.add("jukebox_song.avp.alien_music_1", "Rotch Gwylt - Silver Smile");
        builder.add("jukebox_song.avp.predator_music_1", "Rotch Gwylt - Hunter");

        // Tooltips
        builder.add("tooltip.avp.accuracy", "Accuracy: ");
        builder.add("tooltip.avp.ammunition", "Ammo: ");
        builder.add("tooltip.avp.ammunition_type", "Fires: ");
        builder.add("tooltip.avp.damage", "Damage: ");
        builder.add("tooltip.avp.fire_mode", "Fire Mode: ");
        builder.add("tooltip.avp.fire_rate", "Fire Rate: ");
        builder.add("tooltip.avp.knockback", "Knockback: ");
        builder.add("tooltip.avp.recoil", "Recoil: ");
        builder.add("tooltip.avp.capacity", "Capacity: ");

        builder.add("tooltip.avp.mk50_suit.full_set_bonus", "Full Set Bonus:");
        builder.add("tooltip.avp.mk50_suit.radiation_resistance", "+ Radiation Resistance");
        builder.add("tooltip.avp.mk50_suit.water_breathing", "+ Water Breathing");
        builder.add("tooltip.avp.mk50_suit.slowness", "- Slowness");

        builder.add("tooltip.avp.pressure_suit.full_set_bonus", "Full Set Bonus:");
        builder.add("tooltip.avp.pressure_suit.water_breathing", "+ Water Breathing");

        builder.add("tooltip.avp.ammo_chest.in_inventory", "When In Inventory:");
        builder.add("tooltip.avp.ammo_chest.reload_from_chest", "+ Guns Auto-Reload Ammo from Chest");
        builder.add("tooltip.avp.ammo_chest.placed", "When Placed:");
        builder.add("tooltip.avp.ammo_chest.turret_load_from_chest", "+ Nearby Turrets use Ammo from Chest");

        builder.add("tooltip.avp.lead_chest.in_inventory", "When In Inventory:");
        builder.add("tooltip.avp.lead_chest.auto_store_irradiated_items", "+ Irradiated Items Auto-Stored in Chest");

        builder.add("tooltip.avp.sentry_turret.requires", "Requires:");
        builder.add("tooltip.avp.sentry_turret.redstone_power_requirement", "- Redstone Power");
        builder.add(
            "tooltip.avp.sentry_turret.nearby_ammo_chest_with_ammo_requirement",
            "- Nearby Ammo Chest with Medium Bullets"
        );

        // Keybinds
        builder.add("key.avp.reload", "Reload");
        builder.add("keybind.category.avp.weapons", "AVP Weapons");

        // Containers
        builder.add("container.lead_chest", "Lead Chest");
        builder.add("container.ammo_chest", "Ammo Chest");

        // Death messages
        builder.add("death.attack.acid", "%1$s vaporized in acid");
        builder.add("death.attack.radiation", "%1$s surrendered to radiation");
        builder.add("death.attack.razor_wire", "%1$s was struck by razor wire");
        builder.add("death.attack.smothering", "%1$s was smothered to death");

        builder.add("advancements.aliens.root.title", "AVP: Aliens");
        builder.add("advancements.aliens.root.description", "In Minecraft, no one can hear you scream");

        builder.add("advancements.aliens.kill_an_alien.title", "Imperfect Organism");
        builder.add("advancements.aliens.kill_an_alien.description", "Kill an alien and live to tell the tale");

        builder.add("advancements.aliens.kill_a_royal_alien.title", "Regicide");
        builder.add("advancements.aliens.kill_a_royal_alien.description", "Kill a royal alien");

        builder.add("advancements.aliens.kill_all_aliens.title", "Xenocide");
        builder.add("advancements.aliens.kill_all_aliens.description", "Kill one of every alien");

        builder.add("advancements.aliens.chitin_armor.title", "Cover Me with... Uh...");
        builder.add("advancements.aliens.chitin_armor.description", "Equip a full set of chitin armor");

        builder.add("advancements.aliens.shear_an_ovamorph.title", "Eggsploration Time");
        builder.add("advancements.aliens.shear_an_ovamorph.description", "Free an ovamorph from its bindings");

        builder.add("advancements.aliens.plated_chitin_armor.title", "Kneel to the Crown");
        builder.add("advancements.aliens.plated_chitin_armor.description", "Equip a full set of plated chitin armor");

        // Hive boss bar
        builder.add("bossbar.avp.hive.title", "Hive");

        builder.add("avp.industrialfurnace.displayName", "Industrial Furnace");
        builder.add("effect.avp.radiation", "Radiation");

        // Configs
        builder.add("config.screen.avp", "AVP Config");

        builder.add("config.avp.option.blockConfigs", "Block Setting Configs");
        builder.add("config.avp.option.RESONATOR_REPLACE_TICKS", "Resonator Replace Ticks");
        builder.add("config.avp.option.RESONATOR_REPLACE_RADIUS", "Resonator Replace Radius");

        builder.add("config.avp.option.hiveConfigs", "Hive Configs");
        builder.add(
            "config.avp.option.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS",
            "Minimum distance between hive centers in blocks"
        );
        builder.add("config.avp.option.HIVE_RADIUS_IN_BLOCKS", "The radius of hives in blocks");
        builder.add(
            "config.avp.option.HIVE_LEASH_RADIUS_IN_BLOCKS",
            "Maximum distance away from a hive that Xenomorphs can join or remain as a member"
        );
        builder.add("config.avp.option.HIVE_MAX_PRAETORIAN_COUNT", "Maximum number of Praetorians allowed within a hive");
        builder.add(
            "config.avp.option.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN",
            "Number of hive members required to spawn a Praetorian"
        );
        builder.add(
            "config.avp.option.HIVE_DARKEN_SCREEN",
            "Determines if the screen should darken when the hive boss bar appears"
        );
        builder.add("config.avp.option.HIVE_DEBUG_ENABLED", "Enables hive debugging");
        builder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_LEADER", "Applies a glow effect to the hive leader");
        builder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS", "Applies a glow effect to all hive members");
        builder.add("config.avp.option.HIVE_DEBUG_MARK_HIVE_CENTER", "Marks the hive center with a block");
        builder.add("config.avp.option.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS", "Chestburster Max Growth Timer Seconds");
        builder.add("config.avp.option.DRONE_MAX_GROWTH_TIMER_SECONDS", "Drone Max Growth Timer Seconds");
        builder.add("config.avp.option.WARRIOR_MAX_GROWTH_TIMER_SECONDS", "Warrior Max Growth Timer Seconds");
        builder.add("config.avp.option.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS", "Praetorian Max Growth Timer Seconds");
        builder.add("config.avp.option.PRAETORIAN_SHORTCUT_TIMER_SECONDS", "Praetorian Shortcut Timer Seconds");

        builder.add("config.avp.option.spawnConfigs", "Mob Spawn Configs");
        builder.add("config.avp.option.NATURAL_SPAWNING_ENABLED", "Enable natural spawning for Xenomorphs in the overworld.");
        builder.add(
            "config.avp.option.ADULT_SPAWNING_ENABLED",
            "Enable natural spawning for adult Xenomorphs in the overworld."
        );
        builder.add(
            "config.avp.option.YOUNG_SPAWNING_ENABLED",
            "Enable natural spawning for young Xenomorphs (eggs, facehuggers, bursters, etc.) in the overworld."
        );
        builder.add(
            "config.avp.option.REMOVE_VANILLA_SPAWNS",
            "Removes certain hostile monster spawns, allowing others like Xenomorphs to spawn more frequently."
        );
        builder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED",
            "Enable separate spawn cap for aliens and predators."
        );
        builder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for aliens in the custom mob category."
        );
        builder.add(
            "config.avp.option.PREDATOR_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for predators in the custom mob category."
        );
        builder.add("config.avp.option.CHESTBURSTER_SPAWN", "Chestburster spawn settings");
        builder.add("config.avp.option.DRONE_SPAWN", "Drone spawn settings");
        builder.add("config.avp.option.NETHER_CHESTBURSTER_SPAWN", "Nether Chestburster spawn settings");
        builder.add("config.avp.option.NETHER_DRONE_SPAWN", "Nether Drone spawn settings");
        builder.add("config.avp.option.NETHER_OVAMORPH_SPAWN", "Nether Ovamorph spawn settings");
        builder.add("config.avp.option.NETHER_PRAETORIAN_SPAWN", "Nether Praetorian spawn settings");
        builder.add("config.avp.option.NETHER_WARRIOR_SPAWN", "Nether Warrior spawn settings");
        builder.add("config.avp.option.NETHER_QUEEN_SPAWN", "Nether Queen spawn settings");
        builder.add("config.avp.option.OVAMORPH_SPAWN", "Ovamorph spawn settings");
        builder.add("config.avp.option.PRAETORIAN_SPAWN", "Praetorian spawn settings");
        builder.add("config.avp.option.QUEEN_SPAWN", "Queen spawn settings");
        builder.add("config.avp.option.WARRIOR_SPAWN", "Warrior spawn settings");
        builder.add("config.avp.option.YAUTJA_SPAWN", "Yautja spawn settings");
        builder.add("config.avp.option.enabled", "Enable spawning");
        builder.add("config.avp.option.maxY", "Maximum Y-level at for spawn");
        builder.add("config.avp.option.minY", "Minimum Y-level at for spawn");
        builder.add("config.avp.option.minGroupSize", "Minimum group size for spawns");
        builder.add("config.avp.option.maxGroupSize", "Maximum group size for spawns");
        builder.add("config.avp.option.weight", "Spawn weight");
        builder.add("config.avp.option.requiresResin", "Requires resin for Nether Ovamorph spawning");

        builder.add("config.avp.option.statsConfigs", "Mob Stat Configs");
        builder.add("config.avp.option.ABERRANT_STATS_MULTIPLIER", "Aberrant Stats Multiplier");
        builder.add("config.avp.option.IRRADIATED_STATS_MULTIPLIER", "Irradiated Stats Multiplier");
        builder.add("config.avp.option.ACID_ATTACK_DAMAGE", "Acid Damage per tick");
        builder.add("config.avp.option.CHESTBURSTER_STATS", "Chestburster stats");
        builder.add("config.avp.option.health", "Health value");
        builder.add("config.avp.option.attackDamage", "Attack damage");
        builder.add("config.avp.option.healthRegenPerSecond", "Health regeneration per second");
        builder.add("config.avp.option.knockbackResistance", "Knockback resistance");
        builder.add("config.avp.option.moveSpeed", "Movement speed");
        builder.add("config.avp.option.armorToughness", "Armor toughness.");
        builder.add("config.avp.option.armor", "Armor value");
        builder.add("config.avp.option.nestTickrate", "Nest tickrate");
        builder.add("config.avp.option.followRange", "Follow range");
        builder.add("config.avp.option.DRONE_STATS", "Drone stats");
        builder.add("config.avp.option.OVAMORPH_STATS", "Ovamorph stats");
        builder.add("config.avp.option.PRAETORIAN_STATS", "Praetorian stats");
        builder.add("config.avp.option.QUEEN_STATS", "Queen stats");
        builder.add("config.avp.option.WARRIOR_STATS", "Warrior stats");
        builder.add("config.avp.option.YAUTJA_STATS", "Yautja stats");
        builder.add("config.avp.option.MARINE_STATS", "Marine stats");
        builder.add("config.avp.option.FACEHUGGER_STATS", "Facehugger stats");

        builder.add("config.avp.option.weaponConfigs", "Weapon Options");
        builder.add("config.avp.option.BULLETS_DAMAGE_BLOCKS_ENABLED", "Enable bullet collision damage to blocks");
        builder.add("config.avp.option.ENABLE_NUKE_BLOCK_MECHS", "Allow nukes to work");
        builder.add("config.avp.option.TURRET_FOV", "Turret FOV");
        builder.add("config.avp.option.TURRET_RANGE", "Turret range");
        builder.add("config.avp.option.TURRET_DAMAGE", "Turret damage");
        builder.add("config.avp.option.TURRET_AMMOCHEST_SEARCH_RANGE", "Turret ammo chest search range");

        builder.add("display.avp.low_ammunition_warning", "Low Ammo");
        builder.add("display.avp.no_ammunition_warning", "Out of Ammo");
    }

    private void addBlock(TranslationBuilder translationBuilder, Supplier<? extends Block> blockSupplier, String value) {
        addBlock(translationBuilder, blockSupplier.get(), value);
    }

    private void addBlock(TranslationBuilder translationBuilder, Block block, String value) {
        translationBuilder.add(block, value);
    }

    private void addItem(TranslationBuilder translationBuilder, Supplier<? extends Item> itemSupplier, String value) {
        addItem(translationBuilder, itemSupplier.get(), value);
    }

    private void addItem(TranslationBuilder translationBuilder, Item item, String value) {
        translationBuilder.add(item, value);
    }

    private void addSound(TranslationBuilder translationBuilder, Supplier<SoundEvent> soundEventSupplier, String value) {
        addSound(translationBuilder, soundEventSupplier.get(), value);
    }

    private void addSound(TranslationBuilder translationBuilder, SoundEvent soundEvent, String value) {
        translationBuilder.add("subtitles." + soundEvent.getLocation().getPath(), value);
    }

    public String format(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return String.join(" ", Arrays.stream(input.split("_")).map(this::capitalize).toList());
    }

    public String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
