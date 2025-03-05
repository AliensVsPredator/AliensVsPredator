package com.avp.fabric.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.creative_mode_tab.CreativeModeTabs;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import com.avp.core.common.item.SpawnEggItems;
import com.avp.core.common.sound.AVPSoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Blocks
        addBlock(translationBuilder, AVPBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        addBlock(translationBuilder, AVPBlocks.AUTUNITE_BLOCK, "Autunite Block");
        addBlock(translationBuilder, AVPBlocks.AUTUNITE_ORE, "Autunite Ore");
        addBlock(translationBuilder, AVPBlocks.BAUXITE_ORE, "Bauxite Ore");
        addBlock(translationBuilder, AVPBlocks.BRASS_BLOCK, "Block of Brass");
        addBlock(translationBuilder, AVPBlocks.CHISELED_FERROALUMINUM, "Chiseled Ferroaluminum");
        addBlock(translationBuilder, AVPBlocks.CHISELED_STEEL, "Chiseled Steel");
        addBlock(translationBuilder, AVPBlocks.CHISELED_TITANIUM, "Chiseled Titanium");
        addBlock(translationBuilder, AVPBlocks.CUT_FERROALUMINUM, "Cut Ferroaluminum");
        addBlock(translationBuilder, AVPBlocks.CUT_FERROALUMINUM_SLAB, "Cut Ferroaluminum Slab");
        addBlock(translationBuilder, AVPBlocks.CUT_FERROALUMINUM_STAIRS, "Cut Ferroaluminum Stairs");
        addBlock(translationBuilder, AVPBlocks.CUT_STEEL, "Cut Steel");
        addBlock(translationBuilder, AVPBlocks.CUT_STEEL_SLAB, "Cut Steel Slab");
        addBlock(translationBuilder, AVPBlocks.CUT_STEEL_STAIRS, "Cut Steel Stairs");
        addBlock(translationBuilder, AVPBlocks.CUT_TITANIUM, "Cut Titanium");
        addBlock(translationBuilder, AVPBlocks.CUT_TITANIUM_SLAB, "Cut Titanium Slab");
        addBlock(translationBuilder, AVPBlocks.CUT_TITANIUM_STAIRS, "Cut Titanium Stairs");
        addBlock(translationBuilder, AVPBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(translationBuilder, AVPBlocks.DEEPSLATE_ZINC_ORE, "Deepslate Zinc Ore");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_BLOCK, "Block of Ferroaluminum");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        addBlock(translationBuilder, AVPBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        addBlock(translationBuilder, AVPBlocks.GALENA_ORE, "Galena Ore");
        addBlock(translationBuilder, AVPBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        addBlock(translationBuilder, AVPBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        addBlock(translationBuilder, AVPBlocks.LEAD_BLOCK, "Block of Lead");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Concrete Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Concrete")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Concrete Stairs")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Concrete Wall")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Glass")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Industrial Glass Pane")
        );

        addBlock(translationBuilder, AVPBlocks.LITHIUM_BLOCK, "Block of Lithium");
        addBlock(translationBuilder, AVPBlocks.LITHIUM_ORE, "Lithium Ore");
        addBlock(translationBuilder, AVPBlocks.MONAZITE_ORE, "Monazite Ore");
        addBlock(translationBuilder, AVPBlocks.NETHER_RESIN, "Nether Resin");
        addBlock(translationBuilder, AVPBlocks.NETHER_RESIN_NODE, "Nether Resin");
        addBlock(translationBuilder, AVPBlocks.NETHER_RESIN_VEIN, "Nether Resin Vein");
        addBlock(translationBuilder, AVPBlocks.NETHER_RESIN_WEB, "Nether Resin Web");

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Panel Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Panel Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Panel Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Pipe Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Pipe Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Pipe Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Cut Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Cut Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Cut Plastic Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(translationBuilder, value, format(key.getName()) + " Plastic Stairs")
        );

        addBlock(translationBuilder, AVPBlocks.RAW_BAUXITE_BLOCK, "Block of Raw Bauxite");
        addBlock(translationBuilder, AVPBlocks.RAW_GALENA_BLOCK, "Block of Raw Galena");
        addBlock(translationBuilder, AVPBlocks.RAW_MONAZITE_BLOCK, "Block of Raw Monazite");
        addBlock(translationBuilder, AVPBlocks.RAW_SILICA_BLOCK, "Block of Raw Silica");
        addBlock(translationBuilder, AVPBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(translationBuilder, AVPBlocks.RAW_ZINC_BLOCK, "Block of Raw Zinc");
        addBlock(translationBuilder, AVPBlocks.RAZOR_WIRE, "Razor Wire");
        addBlock(translationBuilder, AVPBlocks.RESIN, "Resin");
        addBlock(translationBuilder, AVPBlocks.RESIN_NODE, "Resin");
        addBlock(translationBuilder, AVPBlocks.RESIN_VEIN, "Resin Vein");
        addBlock(translationBuilder, AVPBlocks.RESIN_WEB, "Resin Web");
        addBlock(translationBuilder, AVPBlocks.SILICA_GRAVEL, "Silica Gravel");
        addBlock(translationBuilder, AVPBlocks.STEEL_BARS, "Steel Bars");
        addBlock(translationBuilder, AVPBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(translationBuilder, AVPBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        addBlock(translationBuilder, AVPBlocks.STEEL_COLUMN, "Steel Column");
        addBlock(translationBuilder, AVPBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        addBlock(translationBuilder, AVPBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        addBlock(translationBuilder, AVPBlocks.STEEL_GRATE, "Steel Grate");
        addBlock(translationBuilder, AVPBlocks.STEEL_PLATING, "Steel Plating");
        addBlock(translationBuilder, AVPBlocks.STEEL_SIDING, "Steel Siding");
        addBlock(translationBuilder, AVPBlocks.STEEL_STANDING, "Steel Standing");
        addBlock(translationBuilder, AVPBlocks.STEEL_TREAD, "Steel Tread");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_BLOCK, "Block of Titanium");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_COLUMN, "Titanium Column");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_GRATE, "Titanium Grate");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_PLATING, "Titanium Plating");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_SIDING, "Titanium Siding");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_STANDING, "Titanium Standing");
        addBlock(translationBuilder, AVPBlocks.TITANIUM_TREAD, "Titanium Tread");
        addBlock(translationBuilder, AVPBlocks.URANIUM_BLOCK, "Block of Uranium");
        addBlock(translationBuilder, AVPBlocks.ZINC_BLOCK, "Block of Zinc");
        addBlock(translationBuilder, AVPBlocks.ZINC_ORE, "Zinc Ore");

        // Creative Mode Tabs
        translationBuilder.add(CreativeModeTabs.BLOCKS_KEY, "AVP Blocks");
        translationBuilder.add(CreativeModeTabs.COLORED_BLOCKS_KEY, "AVP Colored Blocks");
        translationBuilder.add(CreativeModeTabs.COMBAT_KEY, "AVP Combat");
        translationBuilder.add(CreativeModeTabs.INGREDIENTS_KEY, "AVP Ingredients");
        translationBuilder.add(CreativeModeTabs.SPAWN_EGGS_KEY, "AVP Spawn Eggs");
        translationBuilder.add(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY, "AVP Tools & Utilities");

        // Entities
        addEntity(translationBuilder, AVPEntityTypes.ACID, "Acid");
        addEntity(translationBuilder, AVPEntityTypes.CHESTBURSTER, "Chestburster");
        addEntity(translationBuilder, AVPEntityTypes.DRONE, "Drone");
        addEntity(translationBuilder, AVPEntityTypes.FACEHUGGER, "Facehugger");
        addEntity(translationBuilder, AVPEntityTypes.OVAMORPH, "Ovamorph");
        addEntity(translationBuilder, AVPEntityTypes.PRAETORIAN, "Praetorian");
        addEntity(translationBuilder, AVPEntityTypes.QUEEN, "Queen");
        addEntity(translationBuilder, AVPEntityTypes.WARRIOR, "Warrior");
        addEntity(translationBuilder, AVPEntityTypes.YAUTJA, "Yautja");

        // Combat Items

        addItem(translationBuilder, AVPItems.CASELESS_BULLET, "Caseless Bullet");
        addItem(translationBuilder, ArmorItems.CHITIN_BOOTS,  "Chitin Boots");
        addItem(translationBuilder, ArmorItems.CHITIN_CHESTPLATE,  "Chitin Chestplate");
        addItem(translationBuilder, ArmorItems.CHITIN_HELMET,  "Chitin Helmet");
        addItem(translationBuilder, ArmorItems.CHITIN_LEGGINGS,  "Chitin Leggings");
        addItem(translationBuilder, AVPItems.F903WE_RIFLE, "F903WE Rifle");
        addItem(translationBuilder, AVPItems.FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol)");
        addItem(translationBuilder, AVPItems.FUEL_TANK, "Fuel Tank");
        addItem(translationBuilder, AVPItems.HEAVY_BULLET, "Heavy Bullet");
        addItem(translationBuilder, ArmorItems.JUNGLE_PREDATOR_BOOTS, "Predator Boots");
        addItem(translationBuilder, ArmorItems.JUNGLE_PREDATOR_CHESTPLATE, "Predator Chestplate");
        addItem(translationBuilder, ArmorItems.JUNGLE_PREDATOR_HELMET, "Predator Helmet");
        addItem(translationBuilder, ArmorItems.JUNGLE_PREDATOR_LEGGINGS, "Predator Leggings");
        addItem(translationBuilder, AVPItems.M37_12_SHOTGUN, "M37-12 Shotgun");
        addItem(translationBuilder, AVPItems.M41A_PULSE_RIFLE, "M41A Pulse Rifle");
        addItem(translationBuilder, AVPItems.M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle");
        addItem(translationBuilder, AVPItems.M4RA_BATTLE_RIFLE, "M4RA Battle Rifle");
        addItem(translationBuilder, AVPItems.M56_SMARTGUN, "M56 Smartgun");
        addItem(translationBuilder, AVPItems.M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher");
        addItem(translationBuilder, AVPItems.M88_MOD_4_COMBAT_PISTOL, "88 Mod 4 Combat Pistol");
        addItem(translationBuilder, AVPItems.MEDIUM_BULLET, "Medium Bullet");
        addItem(translationBuilder, ArmorItems.MK50_BOOTS, "MK50 Boots");
        addItem(translationBuilder, ArmorItems.MK50_CHESTPLATE, "MK50 Chestplate");
        addItem(translationBuilder, ArmorItems.MK50_HELMET, "MK50 Helmet");
        addItem(translationBuilder, ArmorItems.MK50_LEGGINGS, "MK50 Leggings");
        addItem(translationBuilder, ArmorItems.NETHER_CHITIN_BOOTS, "Nether Chitin Boots");
        addItem(translationBuilder, ArmorItems.NETHER_CHITIN_CHESTPLATE, "Nether Chitin Chestplate");
        addItem(translationBuilder, ArmorItems.NETHER_CHITIN_HELMET, "Nether Chitin Helmet");
        addItem(translationBuilder, ArmorItems.NETHER_CHITIN_LEGGINGS, "Nether Chitin Leggings");
        addItem(translationBuilder, AVPItems.OLD_PAINLESS, "Old Painless");
        addItem(translationBuilder, ArmorItems.PLATED_CHITIN_BOOTS, "Plated Chitin Boots");
        addItem(translationBuilder, ArmorItems.PLATED_CHITIN_CHESTPLATE, "Plated Chitin Chestplate");
        addItem(translationBuilder, ArmorItems.PLATED_CHITIN_HELMET, "Plated Chitin Helmet");
        addItem(translationBuilder, ArmorItems.PLATED_CHITIN_LEGGINGS, "Plated Chitin Leggings");
        addItem(translationBuilder, ArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Plated Nether Chitin Boots");
        addItem(translationBuilder, ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Plated Nether Chitin Chestplate");
        addItem(translationBuilder, ArmorItems.PLATED_NETHER_CHITIN_HELMET, "Plated Nether Chitin Helmet");
        addItem(translationBuilder, ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Plated Nether Chitin Leggings");
        addItem(translationBuilder, ArmorItems.PRESSURE_BOOTS, "Pressure Boots");
        addItem(translationBuilder, ArmorItems.PRESSURE_CHESTPLATE, "Pressure Chestplate");
        addItem(translationBuilder, ArmorItems.PRESSURE_HELMET, "Pressure Helmet");
        addItem(translationBuilder, ArmorItems.PRESSURE_LEGGINGS, "Pressure Leggings");
        addItem(translationBuilder, AVPItems.ROCKET, "Rocket");
        addItem(translationBuilder, AVPItems.SHOTGUN_BULLET, "Shotgun Bullet");
        addItem(translationBuilder, AVPItems.SMALL_BULLET, "Small Bullet");
        addItem(translationBuilder, ArmorItems.STEEL_BOOTS, "Steel Boots");
        addItem(translationBuilder, ArmorItems.STEEL_CHESTPLATE, "Steel Chestplate");
        addItem(translationBuilder, ArmorItems.STEEL_HELMET, "Steel Helmet");
        addItem(translationBuilder, ArmorItems.STEEL_LEGGINGS, "Steel Leggings");
        addItem(translationBuilder, ArmorItems.TACTICAL_BOOTS, "Tactical Boots");
        addItem(translationBuilder, ArmorItems.TACTICAL_CHESTPLATE, "Tactical Chestplate");
        addItem(translationBuilder, ArmorItems.TACTICAL_HELMET, "Tactical Helmet");
        addItem(translationBuilder, ArmorItems.TACTICAL_LEGGINGS, "Tactical Leggings");
        addItem(translationBuilder, ArmorItems.TITANIUM_BOOTS, "Titanium Boots");
        addItem(translationBuilder, ArmorItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        addItem(translationBuilder, ArmorItems.TITANIUM_HELMET, "Titanium Helmet");
        addItem(translationBuilder, ArmorItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        addItem(translationBuilder, AVPItems.ZX_76_SHOTGUN, "ZX-76 Shotgun");

        // Ingredient Items
        addItem(translationBuilder, AVPItems.ALUMINUM_INGOT, "Aluminum Ingot");
        addItem(translationBuilder, AVPItems.AUTUNITE_DUST, "Autunite Dust");
        addItem(translationBuilder, AVPItems.BARREL, "Barrel");
        addItem(translationBuilder, AVPItems.BATTERY_PACK, "Battery Pack");
        addItem(translationBuilder, AVPItems.BLUEPRINT_F903WE_RIFLE, "F903WE Rifle Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol) Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M37_12_SHOTGUN, "M37-12 Shotgun Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "M41A Pulse Rifle Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "M4RA Battle Rifle Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M56_SMARTGUN, "M56 Smartgun Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "M88 Mod 4 Combat Pistol Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_OLD_PAINLESS, "Old Painless Blueprint");
        addItem(translationBuilder, AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "ZX-76 Shotgun Blueprint");
        addItem(translationBuilder, AVPItems.BRASS_INGOT, "Brass Ingot");
        addItem(translationBuilder, AVPItems.BULLET_TIP, "Bullet Tip");
        addItem(translationBuilder, AVPItems.CAPACITOR, "Capacitor");
        addItem(translationBuilder, AVPItems.CARBON_DUST, "Carbon Dust");
        addItem(translationBuilder, AVPItems.CASELESS_CASING, "Caseless Casing");
        addItem(translationBuilder, AVPItems.CHITIN, "Chitin");
        addItem(translationBuilder, AVPItems.CPU, "CPU");
        addItem(translationBuilder, AVPItems.DIODE, "Diode");
        addItem(translationBuilder, AVPItems.FERROALUMINUM_INGOT, "Ferroaluminum Ingot");
        addItem(translationBuilder, AVPItems.GRIP, "Grip");
        addItem(translationBuilder, AVPItems.HEAVY_CASING, "Heavy Casing");
        addItem(translationBuilder, AVPItems.INTEGRATED_CIRCUIT, "Integrated Circuit");
        addItem(translationBuilder, AVPItems.LEAD_INGOT, "Lead Ingot");
        addItem(translationBuilder, AVPItems.LED, "LED");
        addItem(translationBuilder, AVPItems.LED_DISPLAY, "LED Display");
        addItem(translationBuilder, AVPItems.LITHIUM_DUST, "Lithium Dust");
        addItem(translationBuilder, AVPItems.MEDIUM_CASING, "Rifle Casing");
        addItem(translationBuilder, AVPItems.MINIGUN_BARREL, "Minigun Barrel");
        addItem(translationBuilder, AVPItems.NEODYMIUM_MAGNET, "Neodymium Magnet");
        addItem(translationBuilder, AVPItems.NETHER_CHITIN, "Nether Chitin");
        addItem(translationBuilder, AVPItems.NETHER_RESIN_BALL, "Nether Resin Ball");
        addItem(translationBuilder, AVPItems.OVOID_POTTERY_SHERD, "Ovoid Pottery Sherd");
        addItem(translationBuilder, AVPItems.PARASITE_POTTERY_SHERD, "Parasite Pottery Sherd");
        addItem(translationBuilder, AVPItems.ROYALTY_POTTERY_SHERD, "Royalty Pottery Sherd");
        addItem(translationBuilder, AVPItems.PLATED_CHITIN, "Plated Chitin");
        addItem(translationBuilder, AVPItems.PLATED_NETHER_CHITIN, "Plated Nether Chitin");
        addItem(translationBuilder, AVPItems.POLYMER, "Polymer");
        addItem(translationBuilder, AVPItems.RAW_BAUXITE, "Raw Bauxite");
        addItem(translationBuilder, AVPItems.RAW_BRASS, "Raw Brass");
        addItem(translationBuilder, AVPItems.RAW_CRUDE_IRON, "Raw Crude Iron");
        addItem(translationBuilder, AVPItems.RAW_FERROBAUXITE, "Raw Ferrobauxite");
        addItem(translationBuilder, AVPItems.RAW_GALENA, "Raw Galena");
        addItem(translationBuilder, AVPItems.RAW_MONAZITE, "Raw Monazite");
        addItem(translationBuilder, AVPItems.RAW_ROYAL_JELLY, "Raw Royal Jelly");
        addItem(translationBuilder, AVPItems.RAW_SILICA, "Raw Silica");
        addItem(translationBuilder, AVPItems.RAW_TITANIUM, "Raw Titanium");
        addItem(translationBuilder, AVPItems.RAW_ZINC, "Raw Zinc");
        addItem(translationBuilder, AVPItems.RECEIVER, "Receiver");
        addItem(translationBuilder, AVPItems.REGULATOR, "Regulator");
        addItem(translationBuilder, AVPItems.RESIN_BALL, "Resin Ball");
        addItem(translationBuilder, AVPItems.RESISTOR, "Resistor");
        addItem(translationBuilder, AVPItems.ROCKET_BARREL, "Rocket Barrel");
        addItem(translationBuilder, AVPItems.SHOTGUN_CASING, "Shotgun Casing");
        addItem(translationBuilder, AVPItems.SMALL_CASING, "Pistol Casing");
        addItem(translationBuilder, AVPItems.SMART_BARREL, "Smart Barrel");
        addItem(translationBuilder, AVPItems.SMART_RECEIVER, "Smart Receiver");
        addItem(translationBuilder, AVPItems.STEEL_INGOT, "Steel Ingot");
        addItem(translationBuilder, AVPItems.STOCK, "Stock");
        addItem(translationBuilder, AVPItems.TITANIUM_INGOT, "Titanium Ingot");
        addItem(translationBuilder, AVPItems.TRANSISTOR, "Transistor");
        addItem(translationBuilder, AVPItems.URANIUM_INGOT, "Uranium Ingot");
        addItem(translationBuilder, AVPItems.VECTOR_POTTERY_SHERD, "Vector Pottery Sherd");
        addItem(translationBuilder, AVPItems.VERITANIUM_SHARD, "Veritanium Shard");
        addItem(translationBuilder, AVPItems.ZINC_INGOT, "Zinc Ingot");

        // Tools & Utilities Items
        addItem(translationBuilder, AVPItems.ARMOR_CASE, "Armor Case");
        addItem(translationBuilder, AVPItems.CANISTER, "Canister");
        addItem(translationBuilder, AVPItems.ROYAL_JELLY_CANISTER, "Royal Jelly Canister");
        addItem(translationBuilder, AVPItems.STEEL_AXE, "Steel Axe");
        addItem(translationBuilder, AVPItems.STEEL_HOE, "Steel Hoe");
        addItem(translationBuilder, AVPItems.STEEL_PICKAXE, "Steel Pickaxe");
        addItem(translationBuilder, AVPItems.STEEL_SHOVEL, "Steel Shovel");
        addItem(translationBuilder, AVPItems.STEEL_SWORD, "Steel Sword");
        addItem(translationBuilder, AVPItems.TITANIUM_AXE, "Titanium Axe");
        addItem(translationBuilder, AVPItems.TITANIUM_HOE, "Titanium Hoe");
        addItem(translationBuilder, AVPItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        addItem(translationBuilder, AVPItems.TITANIUM_SHOVEL, "Titanium Shovel");
        addItem(translationBuilder, AVPItems.TITANIUM_SWORD, "Titanium Sword");
        addItem(translationBuilder, AVPItems.VERITANIUM_AXE, "Veritanium Axe");
        addItem(translationBuilder, AVPItems.VERITANIUM_HOE, "Veritanium Hoe");
        addItem(translationBuilder, AVPItems.VERITANIUM_PICKAXE, "Veritanium Pickaxe");
        addItem(translationBuilder, AVPItems.VERITANIUM_SHOVEL, "Veritanium Shovel");
        addItem(translationBuilder, AVPItems.VERITANIUM_SWORD, "Veritanium Sword");

        // Spawn Egg Items
        addItem(translationBuilder, SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Aberrant Chestburster Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Aberrant Drone Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Aberrant Facehugger Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG, "Aberrant Ovamorph Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Aberrant Praetorian Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Aberrant Warrior Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Chestburster Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.DRONE_SPAWN_EGG, "Drone Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Facehugger Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Nether Chestburster Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Nether Drone Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Nether Facehugger Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Nether Ovamorph Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Nether Praetorian Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Nether Warrior Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.OVAMORPH_SPAWN_EGG, "Ovamorph Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Praetorian Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.QUEEN_SPAWN_EGG, "Queen Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.WARRIOR_SPAWN_EGG, "Warrior Spawn Egg");
        addItem(translationBuilder, SpawnEggItems.YAUTJA_SPAWN_EGG, "Yautja Spawn Egg");

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

    private void addBlock(TranslationBuilder translationBuilder, Supplier<? extends Block> blockSupplier, String value) {
        translationBuilder.add(blockSupplier.get(), value);
    }

    private void addEntity(TranslationBuilder translationBuilder, Supplier<? extends EntityType<?>> entityTypeSupplier, String value) {
        translationBuilder.add(entityTypeSupplier.get(), value);
    }

    private void addItem(TranslationBuilder translationBuilder, Supplier<? extends Item> itemSupplier, String value) {
        translationBuilder.add(itemSupplier.get(), value);
    }

    private void addSound(TranslationBuilder translationBuilder, Supplier<SoundEvent> soundEvent, String value) {
        translationBuilder.add("subtitles." + soundEvent.get().getLocation().getPath(), value);
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
