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

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Blocks
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
        translationBuilder.add(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        translationBuilder.add(AVPBlocks.GALENA_ORE, "Galena Ore");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        translationBuilder.add(AVPBlocks.LEAD_BLOCK, "Block of Lead");

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
        translationBuilder.add(AVPBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        translationBuilder.add(AVPBlocks.STEEL_COLUMN, "Steel Column");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        translationBuilder.add(AVPBlocks.STEEL_GRATE, "Steel Grate");
        translationBuilder.add(AVPBlocks.STEEL_PLATING, "Steel Plating");
        translationBuilder.add(AVPBlocks.STEEL_SIDING, "Steel Siding");
        translationBuilder.add(AVPBlocks.STEEL_STANDING, "Steel Standing");
        translationBuilder.add(AVPBlocks.STEEL_TREAD, "Steel Tread");
        translationBuilder.add(AVPBlocks.TITANIUM_BLOCK, "Block of Titanium");
        translationBuilder.add(AVPBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        translationBuilder.add(AVPBlocks.TITANIUM_COLUMN, "Titanium Column");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE, "Titanium Grate");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING, "Titanium Plating");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING, "Titanium Siding");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING, "Titanium Standing");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD, "Titanium Tread");
        translationBuilder.add(AVPBlocks.URANIUM_BLOCK, "Block of Uranium");
        translationBuilder.add(AVPBlocks.ZINC_BLOCK, "Block of Zinc");
        translationBuilder.add(AVPBlocks.ZINC_ORE, "Zinc Ore");

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

        // Combat Items

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
        translationBuilder.add(ArmorItems.TITANIUM_BOOTS, "Titanium Boots");
        translationBuilder.add(ArmorItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        translationBuilder.add(ArmorItems.TITANIUM_HELMET, "Titanium Helmet");
        translationBuilder.add(ArmorItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        translationBuilder.add(AVPItems.ZX_76_SHOTGUN, "ZX-76 Shotgun");

        // Ingredient Items
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
        translationBuilder.add(SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.DRONE_SPAWN_EGG, "Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Nether Chestburster Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Nether Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Nether Facehugger Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Nether Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Nether Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Nether Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.OVAMORPH_SPAWN_EGG, "Ovamorph Spawn Egg");
        translationBuilder.add(SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.QUEEN_SPAWN_EGG, "Queen Spawn Egg");
        translationBuilder.add(SpawnEggItems.WARRIOR_SPAWN_EGG, "Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.YAUTJA_SPAWN_EGG, "Yautja Spawn Egg");

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

        // Death messages
        translationBuilder.add("death.attack.acid", "%1$s vaporized in acid");

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
