package com.avp.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.avp.common.block.AVPBlocks;
import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.data.AVPDataGenerator;

/**
 * TODO: Making this for any PRs, this will just need registered in {@link AVPDataGenerator#onInitializeDataGenerator}
 */
public class MexicanSpanishLanguageProvider extends FabricLanguageProvider {

    public MexicanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "es_mx", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Villagers
        translationBuilder.add("entity.minecraft.villager.commissary", "Commissary Villager");

        // Blocks
        translationBuilder.add(AVPBlocks.BLUEPRINT_BLOCK, "Blueprint Block");
        translationBuilder.add(AVPBlocks.REDSTONE_GENERATOR, "Redstone Generator");
        translationBuilder.add(AVPBlocks.DESK_TERMINAL_BLOCK, "Desk Terminal");
        translationBuilder.add(AVPBlocks.TRIP_MINE_BLOCK, "Trip Mine");
        translationBuilder.add(AVPBlocks.RESONATOR_BLOCK, "Resonator");
        translationBuilder.add(AVPBlocks.AMMO_CHEST, "Ammo Chest");
        translationBuilder.add(AVPBlocks.SENTRY_TURRET, "Sentry Turret");
        translationBuilder.add(AVPBlocks.ASH_BLOCK, "Ash Block");
        translationBuilder.add(AVPBlocks.NUKE_BLOCK, "Nuke Block");
        translationBuilder.add(AVPBlocks.ROYAL_JELLY_BLOCK, "Royal Jelly Block");
        translationBuilder.add(AVPBlocks.TRINITITE_BLOCK, "Block of Trinitite");
        translationBuilder.add(AVPBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        translationBuilder.add(AVPBlocks.AUTUNITE_BLOCK, "Autunite Block");
        translationBuilder.add(AVPBlocks.AUTUNITE_ORE, "Autunite Ore");
        translationBuilder.add(AVPBlocks.BAUXITE_ORE, "Bauxite Ore");
        translationBuilder.add(AVPBlocks.BRASS_BLOCK, "Block of Brass");
        translationBuilder.add(AVPBlocks.CHISELED_FERROALUMINUM, "Chiseled Ferroaluminum");
        translationBuilder.add(AVPBlocks.CHISELED_STEEL, "Chiseled Steel");
        translationBuilder.add(AVPBlocks.CHISELED_TITANIUM, "Chiseled Titanium");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM, "Cut Ferroaluminum");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_SLAB, "Cut Ferroaluminum Slab");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_STAIRS, "Cut Ferroaluminum Stairs");
        translationBuilder.add(AVPBlocks.CUT_STEEL, "Cut Steel");
        translationBuilder.add(AVPBlocks.CUT_STEEL_SLAB, "Cut Steel Slab");
        translationBuilder.add(AVPBlocks.CUT_STEEL_STAIRS, "Cut Steel Stairs");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM, "Cut Titanium");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_SLAB, "Cut Titanium Slab");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_STAIRS, "Cut Titanium Stairs");
        translationBuilder.add(AVPBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        translationBuilder.add(AVPBlocks.DEEPSLATE_ZINC_ORE, "Deepslate Zinc Ore");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BLOCK, "Block of Ferroaluminum");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BUTTON, "Ferroaluminum Button");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_DOOR, "Ferroaluminum Door");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE, "Ferroaluminum Pressure Plate");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TRAP_DOOR, "Ferroaluminum Trapdoor");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        translationBuilder.add(AVPBlocks.GALENA_ORE, "Galena Ore");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        translationBuilder.add(AVPBlocks.LEAD_BLOCK, "Block of Lead");
        translationBuilder.add(AVPBlocks.LEAD_CHEST, "Lead Chest");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Ferroaluminum Fastened Standing Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Ferroaluminum Fastened Standing Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_SLAB, "Ferroaluminum Grate Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_STAIRS, "Ferroaluminum Grate Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_SLAB, "Ferroaluminum Plating Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_STAIRS, "Ferroaluminum Plating Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_SLAB, "Ferroaluminum Standing Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_STAIRS, "Ferroaluminum Standing Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_SLAB, "Ferroaluminum Tread Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_STAIRS, "Ferroaluminum Tread Stairs");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_SLAB, "Steel Fastened Standing Slab");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS, "Steel Fastened Standing Stairs");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_SLAB, "Steel Grate Slab");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_STAIRS, "Steel Grate Stairs");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_SLAB, "Steel Plating Slab");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_STAIRS, "Steel Plating Stairs");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_SLAB, "Steel Tread Slab");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_STAIRS, "Steel Tread Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Titanium Fastened Standing Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Titanium Fastened Standing Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_SLAB, "Titanium Grate Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_STAIRS, "Titanium Grate Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_SLAB, "Titanium Plating Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_STAIRS, "Titanium Plating Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_SLAB, "Titanium Tread Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_STAIRS, "Titanium Tread Stairs");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Concrete Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Concrete")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Concrete Stairs")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Concrete Wall")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Glass")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Industrial Glass Pane")
        );

        translationBuilder.add(AVPBlocks.LITHIUM_BLOCK, "Block of Lithium");
        translationBuilder.add(AVPBlocks.LITHIUM_ORE, "Lithium Ore");
        translationBuilder.add(AVPBlocks.MONAZITE_ORE, "Monazite Ore");
        translationBuilder.add(AVPBlocks.NETHER_RESIN, "Nether Resin");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_NODE, "Nether Resin");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_VEIN, "Nether Resin Vein");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_WEB, "Nether Resin Web");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN, "Aberrant Resin");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_NODE, "Aberrant Resin");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_VEIN, "Aberrant Resin Vein");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_WEB, "Aberrant Resin Web");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN, "Irradiated Resin");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_NODE, "Irradiated Resin");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_VEIN, "Irradiated Resin Vein");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_WEB, "Irradiated Resin Web");

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Panel Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Panel Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Panel Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Pipe Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Pipe Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Pipe Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Cut Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Cut Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Cut Plastic Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " Plastic Stairs")
        );

        translationBuilder.add(AVPBlocks.RAW_BAUXITE_BLOCK, "Block of Raw Bauxite");
        translationBuilder.add(AVPBlocks.RAW_GALENA_BLOCK, "Block of Raw Galena");
        translationBuilder.add(AVPBlocks.RAW_MONAZITE_BLOCK, "Block of Raw Monazite");
        translationBuilder.add(AVPBlocks.RAW_SILICA_BLOCK, "Block of Raw Silica");
        translationBuilder.add(AVPBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        translationBuilder.add(AVPBlocks.RAW_ZINC_BLOCK, "Block of Raw Zinc");
        translationBuilder.add(AVPBlocks.RAZOR_WIRE, "Razor Wire");
        translationBuilder.add(AVPBlocks.RESIN, "Resin");
        translationBuilder.add(AVPBlocks.RESIN_NODE, "Resin");
        translationBuilder.add(AVPBlocks.RESIN_VEIN, "Resin Vein");
        translationBuilder.add(AVPBlocks.RESIN_WEB, "Resin Web");
        translationBuilder.add(AVPBlocks.SILICA_GRAVEL, "Silica Gravel");
        translationBuilder.add(AVPBlocks.STEEL_BARS, "Steel Bars");
        translationBuilder.add(AVPBlocks.STEEL_BLOCK, "Block of Steel");
        translationBuilder.add(AVPBlocks.STEEL_BUTTON, "Steel Button");
        translationBuilder.add(AVPBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        translationBuilder.add(AVPBlocks.STEEL_COLUMN, "Steel Column");
        translationBuilder.add(AVPBlocks.STEEL_DOOR, "Steel Door");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        translationBuilder.add(AVPBlocks.STEEL_GRATE, "Steel Grate");
        translationBuilder.add(AVPBlocks.STEEL_PRESSURE_PLATE, "Steel Pressure Plate");
        translationBuilder.add(AVPBlocks.STEEL_PLATING, "Steel Plating");
        translationBuilder.add(AVPBlocks.STEEL_SIDING, "Steel Siding");
        translationBuilder.add(AVPBlocks.STEEL_STANDING, "Steel Standing");
        translationBuilder.add(AVPBlocks.STEEL_TRAP_DOOR, "Steel Trapdoor");
        translationBuilder.add(AVPBlocks.STEEL_TREAD, "Steel Tread");
        translationBuilder.add(AVPBlocks.TITANIUM_BLOCK, "Block of Titanium");
        translationBuilder.add(AVPBlocks.TITANIUM_BUTTON, "Titanium Button");
        translationBuilder.add(AVPBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        translationBuilder.add(AVPBlocks.TITANIUM_COLUMN, "Titanium Column");
        translationBuilder.add(AVPBlocks.TITANIUM_DOOR, "Titanium Door");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE, "Titanium Grate");
        translationBuilder.add(AVPBlocks.TITANIUM_PRESSURE_PLATE, "Titanium Pressure Plate");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING, "Titanium Plating");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING, "Titanium Siding");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING, "Titanium Standing");
        translationBuilder.add(AVPBlocks.TITANIUM_TRAP_DOOR, "Titanium Trapdoor");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD, "Titanium Tread");
        translationBuilder.add(AVPBlocks.URANIUM_BLOCK, "Block of Uranium");
        translationBuilder.add(AVPBlocks.ZINC_BLOCK, "Block of Zinc");
        translationBuilder.add(AVPBlocks.ZINC_ORE, "Zinc Ore");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_FURNACE, "Industrial Furnace");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SLAB, "Ferroaluminum Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STAIRS, "Ferroaluminum Stairs");
        translationBuilder.add(AVPBlocks.STEEL_SLAB, "Steel Slab");
        translationBuilder.add(AVPBlocks.STEEL_STAIRS, "Steel Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_SLAB, "Titanium Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_STAIRS, "Titanium Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_SLAB, "Ferroaluminum Siding Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_STAIRS, "Ferroaluminum Siding Stairs");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_SLAB, "Steel Siding Slab");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_STAIRS, "Steel Siding Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_SLAB, "Titanium Siding Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_STAIRS, "Titanium Siding Stairs");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_SLAB, "Steel Standing Slab");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_STAIRS, "Steel Standing Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_SLAB, "Titanium Standing Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_STAIRS, "Titanium Standing Stairs");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Ferroaluminum Fastened Siding Slab");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Ferroaluminum Fastened Siding Stairs");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_SLAB, "Steel Fastened Siding Slab");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS, "Steel Fastened Siding Stairs");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Titanium Fastened Siding Slab");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Titanium Fastened Siding Stairs");

        // Creative Mode Tabs
        translationBuilder.add(CreativeModeTabs.BLOCKS_KEY, "AVP Blocks");
        translationBuilder.add(CreativeModeTabs.COLORED_BLOCKS_KEY, "AVP Colored Blocks");
        translationBuilder.add(CreativeModeTabs.COMBAT_KEY, "AVP Combat");
        translationBuilder.add(CreativeModeTabs.INGREDIENTS_KEY, "AVP Ingredients");
        translationBuilder.add(CreativeModeTabs.SPAWN_EGGS_KEY, "AVP Spawn Eggs");
        translationBuilder.add(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY, "AVP Tools & Utilities");

        // Entities
        translationBuilder.add(AVPEntityTypes.ACID, "Acid");
        translationBuilder.add(AVPEntityTypes.CHESTBURSTER, "Chestburster");
        translationBuilder.add(AVPEntityTypes.DRONE, "Drone");
        translationBuilder.add(AVPEntityTypes.FACEHUGGER, "Facehugger");
        translationBuilder.add(AVPEntityTypes.OVAMORPH, "Ovamorph");
        translationBuilder.add(AVPEntityTypes.PRAETORIAN, "Praetorian");
        translationBuilder.add(AVPEntityTypes.QUEEN, "Queen");
        translationBuilder.add(AVPEntityTypes.WARRIOR, "Warrior");
        translationBuilder.add(AVPEntityTypes.YAUTJA, "Yautja");
        translationBuilder.add(AVPEntityTypes.ROCKET, "Rocket");
        translationBuilder.add(AVPEntityTypes.GRENADE_THROWN, "Grenade");
        translationBuilder.add(AVPEntityTypes.SHURIKEN, "Shuriken");
        translationBuilder.add(AVPEntityTypes.SMART_DISC, "Smart Disc");
        translationBuilder.add(AVPEntityTypes.BULLET, "Bullet");
        translationBuilder.add(AVPEntityTypes.MARINE, "Marine");

        // Combat Items
        translationBuilder.add(AVPItems.SHURIKEN, "Shuriken");
        translationBuilder.add(AVPItems.SMART_DISC, "Smart Disc");
        translationBuilder.add(AVPItems.GRENADE, "Grenade");
        translationBuilder.add(AVPItems.GRENADE_INCENDIARY, "Incendiary Grenade");
        translationBuilder.add(AVPItems.GRENADE_IRRADIATED, "Irradiated Grenade");
        translationBuilder.add(AVPItems.CASELESS_BULLET, "Caseless Bullet");
        translationBuilder.add(ArmorItems.CHITIN_BOOTS, "Chitin Boots");
        translationBuilder.add(ArmorItems.CHITIN_CHESTPLATE, "Chitin Chestplate");
        translationBuilder.add(ArmorItems.CHITIN_HELMET, "Chitin Helmet");
        translationBuilder.add(ArmorItems.CHITIN_LEGGINGS, "Chitin Leggings");
        translationBuilder.add(AVPItems.F903WE_RIFLE, "F903WE Rifle");
        translationBuilder.add(AVPItems.FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol)");
        translationBuilder.add(AVPItems.FUEL_TANK, "Fuel Tank");
        translationBuilder.add(AVPItems.HEAVY_BULLET, "Heavy Bullet");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_BOOTS, "Predator Boots");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE, "Predator Chestplate");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_HELMET, "Predator Helmet");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_LEGGINGS, "Predator Leggings");
        translationBuilder.add(AVPItems.M37_12_SHOTGUN, "M37-12 Shotgun");
        translationBuilder.add(AVPItems.M41A_PULSE_RIFLE, "M41A Pulse Rifle");
        translationBuilder.add(AVPItems.M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle");
        translationBuilder.add(AVPItems.M4RA_BATTLE_RIFLE, "M4RA Battle Rifle");
        translationBuilder.add(AVPItems.M56_SMARTGUN, "M56 Smartgun");
        translationBuilder.add(AVPItems.M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher");
        translationBuilder.add(AVPItems.M88_MOD_4_COMBAT_PISTOL, "88 Mod 4 Combat Pistol");
        translationBuilder.add(AVPItems.MEDIUM_BULLET, "Medium Bullet");
        translationBuilder.add(ArmorItems.MK50_BOOTS, "MK50 Boots");
        translationBuilder.add(ArmorItems.MK50_CHESTPLATE, "MK50 Chestplate");
        translationBuilder.add(ArmorItems.MK50_HELMET, "MK50 Helmet");
        translationBuilder.add(ArmorItems.MK50_LEGGINGS, "MK50 Leggings");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_BOOTS, "Nether Chitin Boots");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_CHESTPLATE, "Nether Chitin Chestplate");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_HELMET, "Nether Chitin Helmet");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_LEGGINGS, "Nether Chitin Leggings");
        translationBuilder.add(AVPItems.OLD_PAINLESS, "Old Painless");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_BOOTS, "Plated Chitin Boots");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_CHESTPLATE, "Plated Chitin Chestplate");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_HELMET, "Plated Chitin Helmet");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_LEGGINGS, "Plated Chitin Leggings");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Plated Nether Chitin Boots");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Plated Nether Chitin Chestplate");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_HELMET, "Plated Nether Chitin Helmet");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Plated Nether Chitin Leggings");
        translationBuilder.add(ArmorItems.PRESSURE_BOOTS, "Pressure Boots");
        translationBuilder.add(ArmorItems.PRESSURE_CHESTPLATE, "Pressure Chestplate");
        translationBuilder.add(ArmorItems.PRESSURE_HELMET, "Pressure Helmet");
        translationBuilder.add(ArmorItems.PRESSURE_LEGGINGS, "Pressure Leggings");
        translationBuilder.add(AVPItems.ROCKET, "Rocket");
        translationBuilder.add(AVPItems.SHOTGUN_BULLET, "Shotgun Bullet");
        translationBuilder.add(AVPItems.SMALL_BULLET, "Small Bullet");
        translationBuilder.add(ArmorItems.STEEL_BOOTS, "Steel Boots");
        translationBuilder.add(ArmorItems.STEEL_CHESTPLATE, "Steel Chestplate");
        translationBuilder.add(ArmorItems.STEEL_HELMET, "Steel Helmet");
        translationBuilder.add(ArmorItems.STEEL_LEGGINGS, "Steel Leggings");
        translationBuilder.add(ArmorItems.TACTICAL_BOOTS, "Tactical Boots");
        translationBuilder.add(ArmorItems.TACTICAL_CHESTPLATE, "Tactical Chestplate");
        translationBuilder.add(ArmorItems.TACTICAL_HELMET, "Tactical Helmet");
        translationBuilder.add(ArmorItems.TACTICAL_LEGGINGS, "Tactical Leggings");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_BOOTS, "Tactical Camo Boots");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_CHESTPLATE, "Tactical Camo Chestplate");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_HELMET, "Tactical Camo Helmet");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_LEGGINGS, "Tactical Camo Leggings");
        translationBuilder.add(ArmorItems.TITANIUM_BOOTS, "Titanium Boots");
        translationBuilder.add(ArmorItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        translationBuilder.add(ArmorItems.TITANIUM_HELMET, "Titanium Helmet");
        translationBuilder.add(ArmorItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        translationBuilder.add(AVPItems.ZX_76_SHOTGUN, "ZX-76 Shotgun");

        // Ingredient Items
        translationBuilder.add(AVPItems.NUCLEAR_BATTERY, "Nuclear Battery");
        translationBuilder.add(AVPItems.REDSTONE_CRYSTAL, "Redstone Crystal");
        translationBuilder.add(AVPItems.SERVO, "Servo");
        translationBuilder.add(AVPItems.SPEAKER, "Speaker");
        translationBuilder.add(AVPItems.ALUMINUM_INGOT, "Aluminum Ingot");
        translationBuilder.add(AVPItems.AUTUNITE_DUST, "Autunite Dust");
        translationBuilder.add(AVPItems.BARREL, "Barrel");
        translationBuilder.add(AVPItems.BATTERY_PACK, "Battery Pack");
        translationBuilder.add(AVPItems.BLUEPRINT_F903WE_RIFLE, "F903WE Rifle Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol) Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M37_12_SHOTGUN, "M37-12 Shotgun Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "M41A Pulse Rifle Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "M4RA Battle Rifle Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M56_SMARTGUN, "M56 Smartgun Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "M88 Mod 4 Combat Pistol Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_OLD_PAINLESS, "Old Painless Blueprint");
        translationBuilder.add(AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "ZX-76 Shotgun Blueprint");
        translationBuilder.add(AVPItems.BRASS_INGOT, "Brass Ingot");
        translationBuilder.add(AVPItems.BULLET_TIP, "Bullet Tip");
        translationBuilder.add(AVPItems.CAPACITOR, "Capacitor");
        translationBuilder.add(AVPItems.CARBON_DUST, "Carbon Dust");
        translationBuilder.add(AVPItems.CASELESS_CASING, "Caseless Casing");
        translationBuilder.add(AVPItems.CHITIN, "Chitin");
        translationBuilder.add(AVPItems.CPU, "CPU");
        translationBuilder.add(AVPItems.DIODE, "Diode");
        translationBuilder.add(AVPItems.FERROALUMINUM_INGOT, "Ferroaluminum Ingot");
        translationBuilder.add(AVPItems.GRIP, "Grip");
        translationBuilder.add(AVPItems.HEAVY_CASING, "Heavy Casing");
        translationBuilder.add(AVPItems.INTEGRATED_CIRCUIT, "Integrated Circuit");
        translationBuilder.add(AVPItems.LEAD_INGOT, "Lead Ingot");
        translationBuilder.add(AVPItems.LED, "LED");
        translationBuilder.add(AVPItems.LED_DISPLAY, "LED Display");
        translationBuilder.add(AVPItems.LITHIUM_DUST, "Lithium Dust");
        translationBuilder.add(AVPItems.MEDIUM_CASING, "Rifle Casing");
        translationBuilder.add(AVPItems.MINIGUN_BARREL, "Minigun Barrel");
        translationBuilder.add(AVPItems.NEODYMIUM_MAGNET, "Neodymium Magnet");
        translationBuilder.add(AVPItems.NETHER_CHITIN, "Nether Chitin");
        translationBuilder.add(AVPItems.NETHER_RESIN_BALL, "Nether Resin Ball");
        translationBuilder.add(AVPItems.OVOID_POTTERY_SHERD, "Ovoid Pottery Sherd");
        translationBuilder.add(AVPItems.PARASITE_POTTERY_SHERD, "Parasite Pottery Sherd");
        translationBuilder.add(AVPItems.ROYALTY_POTTERY_SHERD, "Royalty Pottery Sherd");
        translationBuilder.add(AVPItems.PLATED_CHITIN, "Plated Chitin");
        translationBuilder.add(AVPItems.PLATED_NETHER_CHITIN, "Plated Nether Chitin");
        translationBuilder.add(AVPItems.POLYMER, "Polymer");
        translationBuilder.add(AVPItems.RAW_BAUXITE, "Raw Bauxite");
        translationBuilder.add(AVPItems.RAW_BRASS, "Raw Brass");
        translationBuilder.add(AVPItems.RAW_CRUDE_IRON, "Raw Crude Iron");
        translationBuilder.add(AVPItems.RAW_FERROBAUXITE, "Raw Ferrobauxite");
        translationBuilder.add(AVPItems.RAW_GALENA, "Raw Galena");
        translationBuilder.add(AVPItems.RAW_MONAZITE, "Raw Monazite");
        translationBuilder.add(AVPItems.RAW_ROYAL_JELLY, "Raw Royal Jelly");
        translationBuilder.add(AVPItems.POISON_JELLY, "Poison Jelly");
        translationBuilder.add(AVPItems.RAW_SILICA, "Raw Silica");
        translationBuilder.add(AVPItems.RAW_TITANIUM, "Raw Titanium");
        translationBuilder.add(AVPItems.RAW_ZINC, "Raw Zinc");
        translationBuilder.add(AVPItems.RECEIVER, "Receiver");
        translationBuilder.add(AVPItems.REGULATOR, "Regulator");
        translationBuilder.add(AVPItems.RESIN_BALL, "Resin Ball");
        translationBuilder.add(AVPItems.RESISTOR, "Resistor");
        translationBuilder.add(AVPItems.ROCKET_BARREL, "Rocket Barrel");
        translationBuilder.add(AVPItems.SHOTGUN_CASING, "Shotgun Casing");
        translationBuilder.add(AVPItems.SMALL_CASING, "Pistol Casing");
        translationBuilder.add(AVPItems.SMART_BARREL, "Smart Barrel");
        translationBuilder.add(AVPItems.SMART_RECEIVER, "Smart Receiver");
        translationBuilder.add(AVPItems.STEEL_INGOT, "Steel Ingot");
        translationBuilder.add(AVPItems.STOCK, "Stock");
        translationBuilder.add(AVPItems.TITANIUM_INGOT, "Titanium Ingot");
        translationBuilder.add(AVPItems.TRANSISTOR, "Transistor");
        translationBuilder.add(AVPItems.URANIUM_INGOT, "Uranium Ingot");
        translationBuilder.add(AVPItems.VECTOR_POTTERY_SHERD, "Vector Pottery Sherd");
        translationBuilder.add(AVPItems.VERITANIUM_SHARD, "Veritanium Shard");
        translationBuilder.add(AVPItems.ZINC_INGOT, "Zinc Ingot");
        translationBuilder.add(AVPItems.ALUMINUM_NUGGET, "Aluminum Nugget");
        translationBuilder.add(AVPItems.BRASS_NUGGET, "Brass Nugget");
        translationBuilder.add(AVPItems.FERROALUMINUM_NUGGET, "Ferroaluminum Nugget");
        translationBuilder.add(AVPItems.LEAD_NUGGET, "Lead Nugget");
        translationBuilder.add(AVPItems.STEEL_NUGGET, "Steel Nugget");
        translationBuilder.add(AVPItems.TITANIUM_NUGGET, "Titanium Nugget");
        translationBuilder.add(AVPItems.URANIUM_NUGGET, "Uranium Nugget");
        translationBuilder.add(AVPItems.ZINC_NUGGET, "Zinc Nugget");
        translationBuilder.add(AVPItems.ABERRANT_RESIN_BALL, "Aberant Resin Ball");
        translationBuilder.add(AVPItems.ABERRANT_CHITIN, "Aberant Chitin");
        translationBuilder.add(AVPItems.PLATED_ABERRANT_CHITIN, "Plated Aberant Chitin");
        translationBuilder.add(AVPItems.IRRADIATED_RESIN_BALL, "Irradiated Resin Ball");
        translationBuilder.add(AVPItems.IRRADIATED_CHITIN, "Irradiated Chitin");
        translationBuilder.add(AVPItems.PLATED_IRRADIATED_CHITIN, "Plated Irradiated Chitin");

        // Tools & Utilities Items
        translationBuilder.add(AVPItems.ARMOR_CASE, "Armor Case");
        translationBuilder.add(AVPItems.CANISTER, "Canister");
        translationBuilder.add(AVPItems.ROYAL_JELLY_CANISTER, "Royal Jelly Canister");
        translationBuilder.add(AVPItems.STEEL_AXE, "Steel Axe");
        translationBuilder.add(AVPItems.STEEL_HOE, "Steel Hoe");
        translationBuilder.add(AVPItems.STEEL_PICKAXE, "Steel Pickaxe");
        translationBuilder.add(AVPItems.STEEL_SHOVEL, "Steel Shovel");
        translationBuilder.add(AVPItems.STEEL_SWORD, "Steel Sword");
        translationBuilder.add(AVPItems.TITANIUM_AXE, "Titanium Axe");
        translationBuilder.add(AVPItems.TITANIUM_HOE, "Titanium Hoe");
        translationBuilder.add(AVPItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        translationBuilder.add(AVPItems.TITANIUM_SHOVEL, "Titanium Shovel");
        translationBuilder.add(AVPItems.TITANIUM_SWORD, "Titanium Sword");
        translationBuilder.add(AVPItems.VERITANIUM_AXE, "Veritanium Axe");
        translationBuilder.add(AVPItems.VERITANIUM_HOE, "Veritanium Hoe");
        translationBuilder.add(AVPItems.VERITANIUM_PICKAXE, "Veritanium Pickaxe");
        translationBuilder.add(AVPItems.VERITANIUM_SHOVEL, "Veritanium Shovel");
        translationBuilder.add(AVPItems.VERITANIUM_SWORD, "Veritanium Sword");

        // Spawn Egg Items
        translationBuilder.add(SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Aberrant Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Aberrant Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Aberrant Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG, "Aberrant Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Aberrant Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Aberrant Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG, "Aberrant Queen Spawn Egg");
        translationBuilder.add(SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.DRONE_SPAWN_EGG, "Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Nether Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Nether Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Nether Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Nether Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Nether Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Nether Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_QUEEN_SPAWN_EGG, "Nether Queen Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG, "Irradiated Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG, "Irradiated Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG, "Irraiated Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.OVAMORPH_SPAWN_EGG, "Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.QUEEN_SPAWN_EGG, "Queen Spawn Egg");
        translationBuilder.add(SpawnEggItems.WARRIOR_SPAWN_EGG, "Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.YAUTJA_SPAWN_EGG, "Yautja Spawn Egg");
        translationBuilder.add(SpawnEggItems.MARINE_SPAWN_EGG, "Marine Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_OVAMORPH_SPAWN_EGG, "Royal Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG, "Royal Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG, "Royal Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_NETHER_OVAMORPH_SPAWN_EGG, "Royal Nether Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG, "Royal Nether Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG, "Royal Nether Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG, "Royal Aberrant Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG, "Royal Aberrant Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG, "Royal Aberrant Chestburster Spawn Egg");

        // Sounds
        addSound(translationBuilder, AVPSoundEvents.BLOCK_ACID_BURN, "Acid burns");

        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, "Xenomorph attacks");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_DEATH, "Xenomorph dies");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HISS, "Xenomorph hisses");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HURT, "Xenomorph hurts");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_IDLE, "Xenomorph breathes");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, "Xenomorph lunges");

        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN, "Chitin armor squishes");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50, "MK50 armor rustles");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE, "Pressure armor rustles");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL, "Steel armor clanks");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL, "Tactical armor rustles");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM, "Titanium armor clanks");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM, "Veritanium armor clanks");

        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH, "Flamethrower finishes reloading");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START, "Flamethrower reloads");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT, "Flamethrower shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT, "Bullet ricochets off of dirt");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC, "Bullet ricochets");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS, "Bullet ricochets off of glass");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_METAL, "Bullet ricochets off of metal");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_RELOAD, "Gun reloads");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT, "Gun shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT_FAIL, "Gun fails to shoot");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT, "M37-12 Shotgun shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT, "M41A Pulse Rifle shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT, "M42A3 Sniper Rifle shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT, "M4RA Battle Rifle shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT, "M56 Smartgun shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH, "M6B Rocket Launcher finishes reloading");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START, "M6B Rocket Launcher reloads");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT, "M6B Rocket Launcher shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD, "M88 Mod 4 Combat Pistol reloads");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT, "M88 Mod 4 Combat Pistol shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT, "Old painless shoots");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH, "Old Painless stops shooting");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING, "Old Painless barrel spins");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START, "Old Painless barrel starts spinning");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT, "ZX-76 Shotgun shoots");

        // Tooltips
        translationBuilder.add("tooltip.avp.accuracy", "Accuracy: ");
        translationBuilder.add("tooltip.avp.ammunition", "Ammo: ");
        translationBuilder.add("tooltip.avp.ammunition_type", "Fires: ");
        translationBuilder.add("tooltip.avp.damage", "Damage: ");
        translationBuilder.add("tooltip.avp.fire_mode", "Fire Mode: ");
        translationBuilder.add("tooltip.avp.fire_rate", "Fire Rate: ");
        translationBuilder.add("tooltip.avp.knockback", "Knockback: ");
        translationBuilder.add("tooltip.avp.recoil", "Recoil: ");

        // Keybinds
        translationBuilder.add("key.avp.reload", "Reload");
        translationBuilder.add("keybind.category.avp.weapons", "AVP Weapons");

        // Containers
        translationBuilder.add("container.lead_chest", "Lead Chest");
        translationBuilder.add("container.ammo_chest", "Ammo Chest");

        // Death messages
        translationBuilder.add("death.attack.acid", "%1$s vaporized in acid");
        translationBuilder.add("death.attack.radiation", "%1$s surrendered to radiation");
        translationBuilder.add("death.attack.razor_wire", "%1$s was struck by razor wire");

        translationBuilder.add("advancements.aliens.root.title", "AVP: Aliens");
        translationBuilder.add("advancements.aliens.root.description", "In Minecraft, no one can hear you scream");

        translationBuilder.add("advancements.aliens.kill_an_alien.title", "Imperfect Organism");
        translationBuilder.add("advancements.aliens.kill_an_alien.description", "Kill an alien and live to tell the tale");

        translationBuilder.add("advancements.aliens.kill_a_royal_alien.title", "Regicide");
        translationBuilder.add("advancements.aliens.kill_a_royal_alien.description", "Kill a royal alien");

        translationBuilder.add("advancements.aliens.kill_all_aliens.title", "Xenocide");
        translationBuilder.add("advancements.aliens.kill_all_aliens.description", "Kill one of every alien");

        translationBuilder.add("advancements.aliens.chitin_armor.title", "Cover Me with... Uh...");
        translationBuilder.add("advancements.aliens.chitin_armor.description", "Equip a full set of chitin armor");

        translationBuilder.add("advancements.aliens.shear_an_ovamorph.title", "Eggsploration Time");
        translationBuilder.add("advancements.aliens.shear_an_ovamorph.description", "Free an ovamorph from its bindings");

        translationBuilder.add("advancements.aliens.plated_chitin_armor.title", "Kneel to the Crown");
        translationBuilder.add("advancements.aliens.plated_chitin_armor.description", "Equip a full set of plated chitin armor");

        // Hive boss bar
        translationBuilder.add("bossbar.avp.hive.title", "Hive");

        translationBuilder.add("avp.industrialfurnace.displayName", "Industrial Furnace");
        translationBuilder.add("effect.avp.radiation", "Radiation");

        // Configs
        translationBuilder.add("config.screen.avp", "AVP Config");

        translationBuilder.add("config.avp.option.blockConfigs", "Block Setting Configs");
        translationBuilder.add("config.avp.option.RESONATOR_REPLACE_TICKS", "Resonator Replace Ticks");
        translationBuilder.add("config.avp.option.RESONATOR_REPLACE_RADIUS", "Resonator Replace Radius");

        translationBuilder.add("config.avp.option.hiveConfigs", "Hive Configs");
        translationBuilder.add(
            "config.avp.option.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS",
            "Minimum distance between hive centers in blocks"
        );
        translationBuilder.add("config.avp.option.HIVE_RADIUS_IN_BLOCKS", "The radius of hives in blocks");
        translationBuilder.add(
            "config.avp.option.HIVE_LEASH_RADIUS_IN_BLOCKS",
            "Maximum distance away from a hive that Xenomorphs can join or remain as a member"
        );
        translationBuilder.add("config.avp.option.HIVE_MAX_PRAETORIAN_COUNT", "Maximum number of Praetorians allowed within a hive");
        translationBuilder.add(
            "config.avp.option.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN",
            "Number of hive members required to spawn a Praetorian"
        );
        translationBuilder.add(
            "config.avp.option.HIVE_DARKEN_SCREEN",
            "Determines if the screen should darken when the hive boss bar appears"
        );
        translationBuilder.add("config.avp.option.HIVE_DEBUG_ENABLED", "Enables hive debugging");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_LEADER", "Applies a glow effect to the hive leader");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS", "Applies a glow effect to all hive members");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_MARK_HIVE_CENTER", "Marks the hive center with a block");
        translationBuilder.add("config.avp.option.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS", "Chestburster Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.DRONE_MAX_GROWTH_TIMER_SECONDS", "Drone Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.WARRIOR_MAX_GROWTH_TIMER_SECONDS", "Warrior Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS", "Praetorian Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.PRAETORIAN_SHORTCUT_TIMER_SECONDS", "Praetorian Shortcut Timer Seconds");

        translationBuilder.add("config.avp.option.spawnConfigs", "Mob Spawn Configs");
        translationBuilder.add("config.avp.option.NATURAL_SPAWNING_ENABLED", "Enable natural spawning for Xenomorphs in the overworld.");
        translationBuilder.add(
            "config.avp.option.ADULT_SPAWNING_ENABLED",
            "Enable natural spawning for adult Xenomorphs in the overworld."
        );
        translationBuilder.add(
            "config.avp.option.YOUNG_SPAWNING_ENABLED",
            "Enable natural spawning for young Xenomorphs (eggs, facehuggers, bursters, etc.) in the overworld."
        );
        translationBuilder.add(
            "config.avp.option.REMOVE_VANILLA_SPAWNS",
            "Removes certain hostile monster spawns, allowing others like Xenomorphs to spawn more frequently."
        );
        translationBuilder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED",
            "Enable separate spawn cap for aliens and predators."
        );
        translationBuilder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for aliens in the custom mob category."
        );
        translationBuilder.add(
            "config.avp.option.PREDATOR_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for predators in the custom mob category."
        );
        translationBuilder.add("config.avp.option.CHESTBURSTER_SPAWN", "Chestburster spawn settings");
        translationBuilder.add("config.avp.option.DRONE_SPAWN", "Drone spawn settings");
        translationBuilder.add("config.avp.option.NETHER_CHESTBURSTER_SPAWN", "Nether Chestburster spawn settings");
        translationBuilder.add("config.avp.option.NETHER_DRONE_SPAWN", "Nether Drone spawn settings");
        translationBuilder.add("config.avp.option.NETHER_OVAMORPH_SPAWN", "Nether Ovamorph spawn settings");
        translationBuilder.add("config.avp.option.NETHER_PRAETORIAN_SPAWN", "Nether Praetorian spawn settings");
        translationBuilder.add("config.avp.option.NETHER_WARRIOR_SPAWN", "Nether Warrior spawn settings");
        translationBuilder.add("config.avp.option.NETHER_QUEEN_SPAWN", "Nether Queen spawn settings");
        translationBuilder.add("config.avp.option.OVAMORPH_SPAWN", "Ovamorph spawn settings");
        translationBuilder.add("config.avp.option.PRAETORIAN_SPAWN", "Praetorian spawn settings");
        translationBuilder.add("config.avp.option.QUEEN_SPAWN", "Queen spawn settings");
        translationBuilder.add("config.avp.option.WARRIOR_SPAWN", "Warrior spawn settings");
        translationBuilder.add("config.avp.option.YAUTJA_SPAWN", "Yautja spawn settings");
        translationBuilder.add("config.avp.option.enabled", "Enable spawning");
        translationBuilder.add("config.avp.option.maxY", "Maximum Y-level at for spawn");
        translationBuilder.add("config.avp.option.minY", "Minimum Y-level at for spawn");
        translationBuilder.add("config.avp.option.minGroupSize", "Minimum group size for spawns");
        translationBuilder.add("config.avp.option.maxGroupSize", "Maximum group size for spawns");
        translationBuilder.add("config.avp.option.weight", "Spawn weight");
        translationBuilder.add("config.avp.option.requiresResin", "Requires resin for Nether Ovamorph spawning");

        translationBuilder.add("config.avp.option.statsConfigs", "Mob Stat Configs");
        translationBuilder.add("config.avp.option.ABERRANT_STATS_MULTIPLIER", "Aberrant Stats Multiplier");
        translationBuilder.add("config.avp.option.IRRADIATED_STATS_MULTIPLIER", "Irradiated Stats Multiplier");
        translationBuilder.add("config.avp.option.ACID_ATTACK_DAMAGE", "Acid Damage per tick");
        translationBuilder.add("config.avp.option.CHESTBURSTER_STATS", "Chestburster stats");
        translationBuilder.add("config.avp.option.health", "Health value");
        translationBuilder.add("config.avp.option.attackDamage", "Attack damage");
        translationBuilder.add("config.avp.option.healthRegenPerSecond", "Health regeneration per second");
        translationBuilder.add("config.avp.option.knockbackResistance", "Knockback resistance");
        translationBuilder.add("config.avp.option.moveSpeed", "Movement speed");
        translationBuilder.add("config.avp.option.armorToughness", "Armor toughness.");
        translationBuilder.add("config.avp.option.armor", "Armor value");
        translationBuilder.add("config.avp.option.nestTickrate", "Nest tickrate");
        translationBuilder.add("config.avp.option.followRange", "Follow range");
        translationBuilder.add("config.avp.option.DRONE_STATS", "Drone stats");
        translationBuilder.add("config.avp.option.OVAMORPH_STATS", "Ovamorph stats");
        translationBuilder.add("config.avp.option.PRAETORIAN_STATS", "Praetorian stats");
        translationBuilder.add("config.avp.option.QUEEN_STATS", "Queen stats");
        translationBuilder.add("config.avp.option.WARRIOR_STATS", "Warrior stats");
        translationBuilder.add("config.avp.option.YAUTJA_STATS", "Yautja stats");
        translationBuilder.add("config.avp.option.MARINE_STATS", "Marine stats");
        translationBuilder.add("config.avp.option.FACEHUGGER_STATS", "Facehugger stats");

        translationBuilder.add("config.avp.option.weaponConfigs", "Weapon Options");
        translationBuilder.add("config.avp.option.BULLETS_DAMAGE_BLOCKS_ENABLED", "Enable bullet collision damage to blocks");
        translationBuilder.add("config.avp.option.ENABLE_NUKE_BLOCK_MECHS", "Allow nukes to work");
        translationBuilder.add("config.avp.option.TURRET_FOV", "Turret FOV");
        translationBuilder.add("config.avp.option.TURRET_RANGE", "Turret range");
        translationBuilder.add("config.avp.option.TURRET_DAMAGE", "Turret damage");
        translationBuilder.add("config.avp.option.TURRET_AMMOCHEST_SEARCH_RANGE", "Turret ammo chest search range");
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
