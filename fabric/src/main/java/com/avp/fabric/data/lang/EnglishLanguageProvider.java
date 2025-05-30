package com.avp.fabric.data.lang;

import com.alien.common.gameplay.hive.HiveBossBarManager;
import com.alien.common.registry.init.AlienArmorItems;
import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.common.registry.init.AVPBlocks;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.init.item.AVPSpawnEggItems;
import com.avp.common.registry.key.AVPCreativeModeTabKeys;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    public EnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Villagers
        builder.add("entity.minecraft.villager.commissary", "Commissary Villager");
        builder.add("entity.minecraft.villager.avp.commissary", "Commissary Villager");

        // Blocks
        addBlock(builder, AVPBlocks.BLUEPRINT_BLOCK, "Blueprint Block");
        addBlock(builder, AVPBlocks.REDSTONE_GENERATOR, "Redstone Generator");
        addBlock(builder, AVPBlocks.DESK_TERMINAL_BLOCK, "Desk Terminal");
        addBlock(builder, AVPBlocks.TRIP_MINE_BLOCK, "Trip Mine");
        addBlock(builder, AVPBlocks.RESONATOR_BLOCK, "Resonator");
        addBlock(builder, AVPBlocks.AMMO_CHEST, "Ammo Chest");
        addBlock(builder, AVPBlocks.SENTRY_TURRET, "Sentry Turret");
        addBlock(builder, AVPBlocks.ASH_BLOCK, "Ash Block");
        addBlock(builder, AVPBlocks.NUKE_BLOCK, "Nuke Block");
        addBlock(builder, AlienBlocks.ROYAL_JELLY_BLOCK, "Royal Jelly Block");
        addBlock(builder, AVPBlocks.TRINITITE_BLOCK, "Block of Trinitite");
        addBlock(builder, AVPBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        addBlock(builder, AVPBlocks.AUTUNITE_BLOCK, "Autunite Block");
        addBlock(builder, AVPBlocks.AUTUNITE_ORE, "Autunite Ore");
        addBlock(builder, AVPBlocks.BAUXITE_ORE, "Bauxite Ore");
        addBlock(builder, AVPBlocks.BRASS_BLOCK, "Block of Brass");
        addBlock(builder, AVPBlocks.CHISELED_FERROALUMINUM, "Chiseled Ferroaluminum");
        addBlock(builder, AVPBlocks.CHISELED_STEEL, "Chiseled Steel");
        addBlock(builder, AVPBlocks.CHISELED_TITANIUM, "Chiseled Titanium");
        addBlock(builder, AVPBlocks.CUT_FERROALUMINUM, "Cut Ferroaluminum");
        addBlock(builder, AVPBlocks.CUT_FERROALUMINUM_SLAB, "Cut Ferroaluminum Slab");
        addBlock(builder, AVPBlocks.CUT_FERROALUMINUM_STAIRS, "Cut Ferroaluminum Stairs");
        addBlock(builder, AVPBlocks.CUT_STEEL, "Cut Steel");
        addBlock(builder, AVPBlocks.CUT_STEEL_SLAB, "Cut Steel Slab");
        addBlock(builder, AVPBlocks.CUT_STEEL_STAIRS, "Cut Steel Stairs");
        addBlock(builder, AVPBlocks.CUT_TITANIUM, "Cut Titanium");
        addBlock(builder, AVPBlocks.CUT_TITANIUM_SLAB, "Cut Titanium Slab");
        addBlock(builder, AVPBlocks.CUT_TITANIUM_STAIRS, "Cut Titanium Stairs");
        addBlock(builder, AVPBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(builder, AVPBlocks.DEEPSLATE_ZINC_ORE, "Deepslate Zinc Ore");
        addBlock(builder, AVPBlocks.FERROALUMINUM_BLOCK, "Block of Ferroaluminum");
        addBlock(builder, AVPBlocks.FERROALUMINUM_BUTTON, "Ferroaluminum Button");
        addBlock(builder, AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        addBlock(builder, AVPBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        addBlock(builder, AVPBlocks.FERROALUMINUM_DOOR, "Ferroaluminum Door");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        addBlock(builder, AVPBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        addBlock(builder, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE, "Ferroaluminum Pressure Plate");
        addBlock(builder, AVPBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        addBlock(builder, AVPBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        addBlock(builder, AVPBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        addBlock(builder, AVPBlocks.FERROALUMINUM_TRAP_DOOR, "Ferroaluminum Trapdoor");
        addBlock(builder, AVPBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        addBlock(builder, AVPBlocks.GALENA_ORE, "Galena Ore");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS_DOOR, "Industrial Glass Door");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR, "Industrial Glass Trap Door");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS_SLAB, "Industrial Glass Slab");
        addBlock(builder, AVPBlocks.INDUSTRIAL_GLASS_STAIRS, "Industrial Glass Stairs");
        addBlock(builder, AVPBlocks.LEAD_BLOCK, "Block of Lead");
        addBlock(builder, AVPBlocks.LEAD_CHEST, "Lead Chest");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Ferroaluminum Fastened Standing Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Ferroaluminum Fastened Standing Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_GRATE_SLAB, "Ferroaluminum Grate Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_GRATE_STAIRS, "Ferroaluminum Grate Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_PLATING_SLAB, "Ferroaluminum Plating Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_PLATING_STAIRS, "Ferroaluminum Plating Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_STANDING_SLAB, "Ferroaluminum Standing Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_STANDING_STAIRS, "Ferroaluminum Standing Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_TREAD_SLAB, "Ferroaluminum Tread Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_TREAD_STAIRS, "Ferroaluminum Tread Stairs");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_STANDING_SLAB, "Steel Fastened Standing Slab");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_STANDING_STAIRS, "Steel Fastened Standing Stairs");
        addBlock(builder, AVPBlocks.STEEL_GRATE_SLAB, "Steel Grate Slab");
        addBlock(builder, AVPBlocks.STEEL_GRATE_STAIRS, "Steel Grate Stairs");
        addBlock(builder, AVPBlocks.STEEL_PLATING_SLAB, "Steel Plating Slab");
        addBlock(builder, AVPBlocks.STEEL_PLATING_STAIRS, "Steel Plating Stairs");
        addBlock(builder, AVPBlocks.STEEL_TREAD_SLAB, "Steel Tread Slab");
        addBlock(builder, AVPBlocks.STEEL_TREAD_STAIRS, "Steel Tread Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Titanium Fastened Standing Slab");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Titanium Fastened Standing Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_GRATE_SLAB, "Titanium Grate Slab");
        addBlock(builder, AVPBlocks.TITANIUM_GRATE_STAIRS, "Titanium Grate Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_PLATING_SLAB, "Titanium Plating Slab");
        addBlock(builder, AVPBlocks.TITANIUM_PLATING_STAIRS, "Titanium Plating Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_TREAD_SLAB, "Titanium Tread Slab");
        addBlock(builder, AVPBlocks.TITANIUM_TREAD_STAIRS, "Titanium Tread Stairs");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Stairs")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Wall")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass Pane")
        );

        addBlock(builder, AVPBlocks.LITHIUM_BLOCK, "Block of Lithium");
        addBlock(builder, AVPBlocks.LITHIUM_ORE, "Lithium Ore");
        addBlock(builder, AVPBlocks.MONAZITE_ORE, "Monazite Ore");
        addBlock(builder, AlienBlocks.NETHER_RESIN, "Nether Resin");
        addBlock(builder, AlienBlocks.NETHER_RESIN_NODE, "Nether Resin");
        addBlock(builder, AlienBlocks.NETHER_RESIN_VEIN, "Nether Resin Vein");
        addBlock(builder, AlienBlocks.NETHER_RESIN_WEB, "Nether Resin Web");
        addBlock(builder, AlienBlocks.ABERRANT_RESIN, "Aberrant Resin");
        addBlock(builder, AlienBlocks.ABERRANT_RESIN_NODE, "Aberrant Resin");
        addBlock(builder, AlienBlocks.ABERRANT_RESIN_VEIN, "Aberrant Resin Vein");
        addBlock(builder, AlienBlocks.ABERRANT_RESIN_WEB, "Aberrant Resin Web");
        addBlock(builder, AlienBlocks.IRRADIATED_RESIN, "Irradiated Resin");
        addBlock(builder, AlienBlocks.IRRADIATED_RESIN_NODE, "Irradiated Resin");
        addBlock(builder, AlienBlocks.IRRADIATED_RESIN_VEIN, "Irradiated Resin Vein");
        addBlock(builder, AlienBlocks.IRRADIATED_RESIN_WEB, "Irradiated Resin Web");
        addBlock(builder, AlienBlocks.RESIN_BRICKS, "Resin Bricks");
        addBlock(builder, AlienBlocks.RESIN_O, "Resin O");
        addBlock(builder, AlienBlocks.RESIN_RIBBED, "Resin Ribbed");
        addBlock(builder, AlienBlocks.RESIN_SMOOTH, "Resin Smooth");

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Stairs")
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Slab")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Stairs")
        );

        addBlock(builder, AVPBlocks.RAW_BAUXITE_BLOCK, "Block of Raw Bauxite");
        addBlock(builder, AVPBlocks.RAW_GALENA_BLOCK, "Block of Raw Galena");
        addBlock(builder, AVPBlocks.RAW_MONAZITE_BLOCK, "Block of Raw Monazite");
        addBlock(builder, AVPBlocks.SILICON_BLOCK, "Block of Silicon");
        addBlock(builder, AVPBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(builder, AVPBlocks.RAW_ZINC_BLOCK, "Block of Raw Zinc");
        addBlock(builder, AVPBlocks.RAZOR_WIRE, "Razor Wire");
        addBlock(builder, AlienBlocks.RESIN, "Resin");
        addBlock(builder, AlienBlocks.RESIN_NODE, "Resin");
        addBlock(builder, AlienBlocks.RESIN_VEIN, "Resin Vein");
        addBlock(builder, AlienBlocks.RESIN_WEB, "Resin Web");
        addBlock(builder, AVPBlocks.SILICA_GRAVEL, "Silica Gravel");
        addBlock(builder, AVPBlocks.STEEL_BARS, "Steel Bars");
        addBlock(builder, AVPBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(builder, AVPBlocks.STEEL_BUTTON, "Steel Button");
        addBlock(builder, AVPBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        addBlock(builder, AVPBlocks.STEEL_COLUMN, "Steel Column");
        addBlock(builder, AVPBlocks.STEEL_DOOR, "Steel Door");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        addBlock(builder, AVPBlocks.STEEL_GRATE, "Steel Grate");
        addBlock(builder, AVPBlocks.STEEL_PRESSURE_PLATE, "Steel Pressure Plate");
        addBlock(builder, AVPBlocks.STEEL_PLATING, "Steel Plating");
        addBlock(builder, AVPBlocks.STEEL_SIDING, "Steel Siding");
        addBlock(builder, AVPBlocks.STEEL_STANDING, "Steel Standing");
        addBlock(builder, AVPBlocks.STEEL_TRAP_DOOR, "Steel Trapdoor");
        addBlock(builder, AVPBlocks.STEEL_TREAD, "Steel Tread");
        addBlock(builder, AVPBlocks.TITANIUM_BLOCK, "Block of Titanium");
        addBlock(builder, AVPBlocks.TITANIUM_BUTTON, "Titanium Button");
        addBlock(builder, AVPBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        addBlock(builder, AVPBlocks.TITANIUM_COLUMN, "Titanium Column");
        addBlock(builder, AVPBlocks.TITANIUM_DOOR, "Titanium Door");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        addBlock(builder, AVPBlocks.TITANIUM_GRATE, "Titanium Grate");
        addBlock(builder, AVPBlocks.TITANIUM_PRESSURE_PLATE, "Titanium Pressure Plate");
        addBlock(builder, AVPBlocks.TITANIUM_PLATING, "Titanium Plating");
        addBlock(builder, AVPBlocks.TITANIUM_SIDING, "Titanium Siding");
        addBlock(builder, AVPBlocks.TITANIUM_STANDING, "Titanium Standing");
        addBlock(builder, AVPBlocks.TITANIUM_TRAP_DOOR, "Titanium Trapdoor");
        addBlock(builder, AVPBlocks.TITANIUM_TREAD, "Titanium Tread");
        addBlock(builder, AVPBlocks.URANIUM_BLOCK, "Block of Uranium");
        addBlock(builder, AVPBlocks.ZINC_BLOCK, "Block of Zinc");
        addBlock(builder, AVPBlocks.ZINC_ORE, "Zinc Ore");
        addBlock(builder, AVPBlocks.INDUSTRIAL_FURNACE, "Industrial Furnace");
        addBlock(builder, AVPBlocks.FERROALUMINUM_SLAB, "Ferroaluminum Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_STAIRS, "Ferroaluminum Stairs");
        addBlock(builder, AVPBlocks.STEEL_SLAB, "Steel Slab");
        addBlock(builder, AVPBlocks.STEEL_STAIRS, "Steel Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_SLAB, "Titanium Slab");
        addBlock(builder, AVPBlocks.TITANIUM_STAIRS, "Titanium Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_SIDING_SLAB, "Ferroaluminum Siding Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_SIDING_STAIRS, "Ferroaluminum Siding Stairs");
        addBlock(builder, AVPBlocks.STEEL_SIDING_SLAB, "Steel Siding Slab");
        addBlock(builder, AVPBlocks.STEEL_SIDING_STAIRS, "Steel Siding Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_SIDING_SLAB, "Titanium Siding Slab");
        addBlock(builder, AVPBlocks.TITANIUM_SIDING_STAIRS, "Titanium Siding Stairs");
        addBlock(builder, AVPBlocks.STEEL_STANDING_SLAB, "Steel Standing Slab");
        addBlock(builder, AVPBlocks.STEEL_STANDING_STAIRS, "Steel Standing Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_STANDING_SLAB, "Titanium Standing Slab");
        addBlock(builder, AVPBlocks.TITANIUM_STANDING_STAIRS, "Titanium Standing Stairs");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Ferroaluminum Fastened Siding Slab");
        addBlock(builder, AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Ferroaluminum Fastened Siding Stairs");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_SIDING_SLAB, "Steel Fastened Siding Slab");
        addBlock(builder, AVPBlocks.STEEL_FASTENED_SIDING_STAIRS, "Steel Fastened Siding Stairs");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Titanium Fastened Siding Slab");
        addBlock(builder, AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Titanium Fastened Siding Stairs");

        // Creative Mode Tabs
        builder.add(AVPCreativeModeTabKeys.BLOCKS_KEY, "AVP Blocks");
        builder.add(AVPCreativeModeTabKeys.COLORED_BLOCKS_KEY, "AVP Colored Blocks");
        builder.add(AVPCreativeModeTabKeys.COMBAT_KEY, "AVP Combat");
        builder.add(AVPCreativeModeTabKeys.INGREDIENTS_KEY, "AVP Ingredients");
        builder.add(AVPCreativeModeTabKeys.SPAWN_EGGS_KEY, "AVP Spawn Eggs");
        builder.add(AVPCreativeModeTabKeys.TOOLS_AND_UTILITIES_KEY, "AVP Tools & Utilities");

        // Entities
        addEntity(builder, AVPEntityTypes.ABERRANT_CHESTBURSTER, "Aberrant Chestburster");
        addEntity(builder, AVPEntityTypes.ABERRANT_DRONE, "Aberrant Drone");
        addEntity(builder, AVPEntityTypes.ABERRANT_FACEHUGGER, "Aberrant Facehugger");
        addEntity(builder, AVPEntityTypes.ABERRANT_OVOMORPH, "Aberrant Ovomorph");
        addEntity(builder, AVPEntityTypes.ABERRANT_PRAETORIAN, "Aberrant Praetorian");
        addEntity(builder, AVPEntityTypes.ABERRANT_QUEEN, "Aberrant Queen");
        addEntity(builder, AVPEntityTypes.ABERRANT_WARRIOR, "Aberrant Warrior");
        addEntity(builder, AVPEntityTypes.ACID, "Acid");
        addEntity(builder, AVPEntityTypes.CHESTBURSTER, "Chestburster");
        addEntity(builder, AVPEntityTypes.DRONE, "Drone");
        addEntity(builder, AVPEntityTypes.FACEHUGGER, "Facehugger");
        addEntity(builder, AVPEntityTypes.GRENADE_THROWN, "Grenade");
        addEntity(builder, AVPEntityTypes.IRRADIATED_DRONE, "Irradiated Drone");
        addEntity(builder, AVPEntityTypes.IRRADIATED_PRAETORIAN, "Irradiated Praetorian");
        addEntity(builder, AVPEntityTypes.IRRADIATED_QUEEN, "Irradiated Queen");
        addEntity(builder, AVPEntityTypes.IRRADIATED_WARRIOR, "Irradiated Warrior");
        addEntity(builder, AVPEntityTypes.MARINE, "Marine");
        addEntity(builder, AVPEntityTypes.NETHER_CHESTBURSTER, "Nether Chestburster");
        addEntity(builder, AVPEntityTypes.NETHER_DRONE, "Nether Drone");
        addEntity(builder, AVPEntityTypes.NETHER_FACEHUGGER, "Nether Facehugger");
        addEntity(builder, AVPEntityTypes.NETHER_OVOMORPH, "Nether Ovomorph");
        addEntity(builder, AVPEntityTypes.NETHER_PRAETORIAN, "Nether Praetorian");
        addEntity(builder, AVPEntityTypes.NETHER_QUEEN, "Nether Queen");
        addEntity(builder, AVPEntityTypes.NETHER_WARRIOR, "Nether Warrior");
        addEntity(builder, AVPEntityTypes.OVOMORPH, "Ovomorph");
        addEntity(builder, AVPEntityTypes.PRAETORIAN, "Praetorian");
        addEntity(builder, AVPEntityTypes.QUEEN, "Queen");
        addEntity(builder, AVPEntityTypes.ROCKET, "Rocket");
        addEntity(builder, AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER, "Royal Aberrant Chestburster");
        addEntity(builder, AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER, "Royal Aberrant Facehugger");
        addEntity(builder, AVPEntityTypes.ROYAL_ABERRANT_OVOMORPH, "Royal Aberrant Ovomorph");
        addEntity(builder, AVPEntityTypes.ROYAL_CHESTBURSTER, "Royal Chestburster");
        addEntity(builder, AVPEntityTypes.ROYAL_FACEHUGGER, "Royal Facehugger");
        addEntity(builder, AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER, "Royal Nether Chestburster");
        addEntity(builder, AVPEntityTypes.ROYAL_NETHER_FACEHUGGER, "Royal Nether Facehugger");
        addEntity(builder, AVPEntityTypes.ROYAL_NETHER_OVOMORPH, "Royal Nether Ovomorph");
        addEntity(builder, AVPEntityTypes.ROYAL_OVOMORPH, "Royal Ovomorph");
        addEntity(builder, AVPEntityTypes.SHURIKEN, "Shuriken");
        addEntity(builder, AVPEntityTypes.SMART_DISC, "Smart Disc");
        addEntity(builder, AVPEntityTypes.WARRIOR, "Warrior");
        addEntity(builder, AVPEntityTypes.YAUTJA, "Yautja");

        // Combat Items
        addItem(builder, AVPItems.SHURIKEN, "Shuriken");
        addItem(builder, AVPItems.SMART_DISC, "Smart Disc");
        addItem(builder, AVPItems.GRENADE, "Grenade");
        addItem(builder, AVPItems.GRENADE_INCENDIARY, "Incendiary Grenade");
        addItem(builder, AVPItems.GRENADE_IRRADIATED, "Irradiated Grenade");
        addItem(builder, AVPItems.CASELESS_BULLET, "Caseless Bullet");
        addItem(builder, AlienArmorItems.ABERRANT_CHITIN_BOOTS, "Aberrant Chitin Boots");
        addItem(builder, AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE, "Aberrant Chitin Chestplate");
        addItem(builder, AlienArmorItems.ABERRANT_CHITIN_HELMET, "Aberrant Chitin Helmet");
        addItem(builder, AlienArmorItems.ABERRANT_CHITIN_LEGGINGS, "Aberrant Chitin Leggings");
        addItem(builder, AlienArmorItems.CHITIN_BOOTS, "Chitin Boots");
        addItem(builder, AlienArmorItems.CHITIN_CHESTPLATE, "Chitin Chestplate");
        addItem(builder, AlienArmorItems.CHITIN_HELMET, "Chitin Helmet");
        addItem(builder, AlienArmorItems.CHITIN_LEGGINGS, "Chitin Leggings");
        addItem(builder, AlienArmorItems.IRRADIATED_CHITIN_BOOTS, "Irradiated Chitin Boots");
        addItem(builder, AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE, "Irradiated Chitin Chestplate");
        addItem(builder, AlienArmorItems.IRRADIATED_CHITIN_HELMET, "Irradiated Chitin Helmet");
        addItem(builder, AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS, "Irradiated Chitin Leggings");
        addItem(builder, AVPItems.F903WE_RIFLE, "F903WE Rifle");
        addItem(builder, AVPItems.FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol)");
        addItem(builder, AVPItems.FUEL_TANK, "Fuel Tank");
        addItem(builder, AVPItems.HEAVY_BULLET, "Heavy Bullet");
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
        addItem(builder, AVPItems.MEDIUM_BULLET, "Medium Bullet");
        addItem(builder, AVPArmorItems.MK50_BOOTS, "MK50 Boots");
        addItem(builder, AVPArmorItems.MK50_CHESTPLATE, "MK50 Chestplate");
        addItem(builder, AVPArmorItems.MK50_HELMET, "MK50 Helmet");
        addItem(builder, AVPArmorItems.MK50_LEGGINGS, "MK50 Leggings");
        addItem(builder, AlienArmorItems.NETHER_CHITIN_BOOTS, "Nether Chitin Boots");
        addItem(builder, AlienArmorItems.NETHER_CHITIN_CHESTPLATE, "Nether Chitin Chestplate");
        addItem(builder, AlienArmorItems.NETHER_CHITIN_HELMET, "Nether Chitin Helmet");
        addItem(builder, AlienArmorItems.NETHER_CHITIN_LEGGINGS, "Nether Chitin Leggings");
        addItem(builder, AVPItems.OLD_PAINLESS, "Old Painless");
        addItem(builder, AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS, "Plated Aberrant Chitin Boots");
        addItem(builder, AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE, "Plated Aberrant Chitin Chestplate");
        addItem(builder, AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET, "Plated Aberrant Chitin Helmet");
        addItem(builder, AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS, "Plated Aberrant Chitin Leggings");
        addItem(builder, AlienArmorItems.PLATED_CHITIN_BOOTS, "Plated Chitin Boots");
        addItem(builder, AlienArmorItems.PLATED_CHITIN_CHESTPLATE, "Plated Chitin Chestplate");
        addItem(builder, AlienArmorItems.PLATED_CHITIN_HELMET, "Plated Chitin Helmet");
        addItem(builder, AlienArmorItems.PLATED_CHITIN_LEGGINGS, "Plated Chitin Leggings");
        addItem(builder, AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS, "Plated Irradiated Chitin Boots");
        addItem(builder, AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE, "Plated Irradiated Chitin Chestplate");
        addItem(builder, AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET, "Plated Irradiated Chitin Helmet");
        addItem(builder, AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS, "Plated Irradiated Chitin Leggings");
        addItem(builder, AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Plated Nether Chitin Boots");
        addItem(builder, AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Plated Nether Chitin Chestplate");
        addItem(builder, AlienArmorItems.PLATED_NETHER_CHITIN_HELMET, "Plated Nether Chitin Helmet");
        addItem(builder, AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Plated Nether Chitin Leggings");
        addItem(builder, AVPArmorItems.PRESSURE_BOOTS, "Pressure Boots");
        addItem(builder, AVPArmorItems.PRESSURE_CHESTPLATE, "Pressure Chestplate");
        addItem(builder, AVPArmorItems.PRESSURE_HELMET, "Pressure Helmet");
        addItem(builder, AVPArmorItems.PRESSURE_LEGGINGS, "Pressure Leggings");
        addItem(builder, AVPItems.ROCKET, "Rocket");
        addItem(builder, AVPItems.SHOTGUN_SHELL, "Shotgun Shell");
        addItem(builder, AVPItems.SMALL_BULLET, "Small Bullet");
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
        addItem(builder, AVPItems.NUCLEAR_BATTERY, "Nuclear Battery");
        addItem(builder, AVPItems.REDSTONE_CRYSTAL, "Redstone Crystal");
        addItem(builder, AVPItems.SERVO, "Servo");
        addItem(builder, AVPItems.SPEAKER, "Speaker");
        addItem(builder, AVPItems.ALUMINUM_INGOT, "Aluminum Ingot");
        addItem(builder, AlienItems.ALIEN_MUSIC_DISC_1, "Music Disc");
        addItem(builder, AVPItems.PREDATOR_MUSIC_DISC_1, "Music Disc");
        addItem(builder, AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT, "Disc Fragment");
        addItem(builder, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT, "Disc Fragment");
        builder.add(AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT.get().getDescriptionId() + ".desc", "Music Disc - Silver Smile");
        builder.add(AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT.get().getDescriptionId() + ".desc", "Music Disc - Hunter");
        addItem(builder, AVPItems.AUTUNITE_DUST, "Autunite Dust");
        addItem(builder, AVPItems.BARREL, "Barrel");
        addItem(builder, AVPItems.BATTERY_PACK, "Battery Pack");
        addItem(builder, AVPItems.BLUEPRINT_F903WE_RIFLE, "F903WE Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol) Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M37_12_SHOTGUN, "M37-12 Shotgun Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "M41A Pulse Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "M4RA Battle Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M56_SMARTGUN, "M56 Smartgun Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "M88 Mod 4 Combat Pistol Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_OLD_PAINLESS, "Old Painless Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "ZX-76 Shotgun Blueprint");
        addItem(builder, AVPItems.BRASS_INGOT, "Brass Ingot");
        addItem(builder, AVPItems.BULLET_TIP, "Bullet Tip");
        addItem(builder, AVPItems.CAPACITOR, "Capacitor");
        addItem(builder, AVPItems.CARBON_DUST, "Carbon Dust");
        addItem(builder, AVPItems.CASELESS_CARTRIDGE, "Caseless Cartridge");
        addItem(builder, AlienItems.CHITIN, "Chitin");
        addItem(builder, AVPItems.CPU, "CPU");
        addItem(builder, AVPItems.DIODE, "Diode");
        addItem(builder, AVPItems.FERROALUMINUM_INGOT, "Ferroaluminum Ingot");
        addItem(builder, AVPItems.GRIP, "Grip");
        addItem(builder, AVPItems.HEAVY_CASING, "Heavy Casing");
        addItem(builder, AVPItems.INTEGRATED_CIRCUIT, "Integrated Circuit");
        addItem(builder, AVPItems.LEAD_INGOT, "Lead Ingot");
        addItem(builder, AVPItems.LED, "LED");
        addItem(builder, AVPItems.LED_DISPLAY, "LED Display");
        addItem(builder, AVPItems.LITHIUM_DUST, "Lithium Dust");
        addItem(builder, AVPItems.MEDIUM_CASING, "Rifle Casing");
        addItem(builder, AVPItems.MINIGUN_BARREL, "Minigun Barrel");
        addItem(builder, AVPItems.NEODYMIUM_MAGNET, "Neodymium Magnet");
        addItem(builder, AlienItems.NETHER_CHITIN, "Nether Chitin");
        addItem(builder, AlienItems.NETHER_RESIN_BALL, "Nether Resin Ball");
        addItem(builder, AlienItems.OVOID_POTTERY_SHERD, "Ovoid Pottery Sherd");
        addItem(builder, AlienItems.PARASITE_POTTERY_SHERD, "Parasite Pottery Sherd");
        addItem(builder, AlienItems.ROYALTY_POTTERY_SHERD, "Royalty Pottery Sherd");
        addItem(builder, AlienItems.PLATED_CHITIN, "Plated Chitin");
        addItem(builder, AlienItems.PLATED_NETHER_CHITIN, "Plated Nether Chitin");
        addItem(builder, AVPItems.POLYMER, "Polymer");
        addItem(builder, AVPItems.RAW_BAUXITE, "Raw Bauxite");
        addItem(builder, AVPItems.RAW_BRASS, "Raw Brass");
        addItem(builder, AVPItems.RAW_CRUDE_IRON, "Raw Crude Iron");
        addItem(builder, AVPItems.RAW_FERROBAUXITE, "Raw Ferrobauxite");
        addItem(builder, AVPItems.RAW_GALENA, "Raw Galena");
        addItem(builder, AVPItems.RAW_MONAZITE, "Raw Monazite");
        addItem(builder, AlienItems.RAW_ROYAL_JELLY, "Raw Royal Jelly");
        addItem(builder, AlienItems.POISON_JELLY, "Poison Jelly");
        addItem(builder, AVPItems.SILICON, "Silicon");
        addItem(builder, AVPItems.RAW_TITANIUM, "Raw Titanium");
        addItem(builder, AVPItems.RAW_ZINC, "Raw Zinc");
        addItem(builder, AVPItems.RECEIVER, "Receiver");
        addItem(builder, AVPItems.REGULATOR, "Regulator");
        addItem(builder, AlienItems.RESIN_BALL, "Resin Ball");
        addItem(builder, AVPItems.RESISTOR, "Resistor");
        addItem(builder, AVPItems.ROCKET_BARREL, "Rocket Barrel");
        addItem(builder, AVPItems.SHOTGUN_CASING, "Shotgun Casing");
        addItem(builder, AVPItems.SMALL_CASING, "Pistol Casing");
        addItem(builder, AVPItems.SMART_BARREL, "Smart Barrel");
        addItem(builder, AVPItems.SMART_RECEIVER, "Smart Receiver");
        addItem(builder, AVPItems.STEEL_INGOT, "Steel Ingot");
        addItem(builder, AVPItems.STOCK, "Stock");
        addItem(builder, AVPItems.TITANIUM_INGOT, "Titanium Ingot");
        addItem(builder, AVPItems.TRANSISTOR, "Transistor");
        addItem(builder, AVPItems.URANIUM_INGOT, "Uranium Ingot");
        addItem(builder, AlienItems.VECTOR_POTTERY_SHERD, "Vector Pottery Sherd");
        addItem(builder, AVPItems.VERITANIUM_SHARD, "Veritanium Shard");
        addItem(builder, AVPItems.ZINC_INGOT, "Zinc Ingot");
        addItem(builder, AVPItems.ALUMINUM_NUGGET, "Aluminum Nugget");
        addItem(builder, AVPItems.BRASS_NUGGET, "Brass Nugget");
        addItem(builder, AVPItems.FERROALUMINUM_NUGGET, "Ferroaluminum Nugget");
        addItem(builder, AVPItems.LEAD_NUGGET, "Lead Nugget");
        addItem(builder, AVPItems.STEEL_NUGGET, "Steel Nugget");
        addItem(builder, AVPItems.TITANIUM_NUGGET, "Titanium Nugget");
        addItem(builder, AVPItems.URANIUM_NUGGET, "Uranium Nugget");
        addItem(builder, AVPItems.ZINC_NUGGET, "Zinc Nugget");
        addItem(builder, AlienItems.ABERRANT_RESIN_BALL, "Aberrant Resin Ball");
        addItem(builder, AlienItems.ABERRANT_CHITIN, "Aberrant Chitin");
        addItem(builder, AlienItems.PLATED_ABERRANT_CHITIN, "Plated Aberrant Chitin");
        addItem(builder, AlienItems.IRRADIATED_RESIN_BALL, "Irradiated Resin Ball");
        addItem(builder, AlienItems.IRRADIATED_CHITIN, "Irradiated Chitin");
        addItem(builder, AlienItems.PLATED_IRRADIATED_CHITIN, "Plated Irradiated Chitin");

        // Tools & Utilities Items
        addItem(builder, AVPItems.ARMOR_CASE, "Armor Case");
        addItem(builder, AVPItems.CANISTER, "Canister");
        addItem(builder, AVPItems.WATER_CANISTER, "Water Canister");
        addItem(builder, AVPItems.LAVA_CANISTER, "Lava Canister");
        addItem(builder, AVPItems.MILK_CANISTER, "Milk Canister");
        addItem(builder, AVPItems.POWDER_SNOW_CANISTER, "Powder Snow Canister");
        addItem(builder, AVPItems.STEEL_AXE, "Steel Axe");
        addItem(builder, AVPItems.STEEL_HOE, "Steel Hoe");
        addItem(builder, AVPItems.STEEL_PICKAXE, "Steel Pickaxe");
        addItem(builder, AVPItems.STEEL_SHOVEL, "Steel Shovel");
        addItem(builder, AVPItems.STEEL_SWORD, "Steel Sword");
        addItem(builder, AVPItems.TITANIUM_AXE, "Titanium Axe");
        addItem(builder, AVPItems.TITANIUM_HOE, "Titanium Hoe");
        addItem(builder, AVPItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        addItem(builder, AVPItems.TITANIUM_SHOVEL, "Titanium Shovel");
        addItem(builder, AVPItems.TITANIUM_SWORD, "Titanium Sword");
        addItem(builder, AVPItems.VERITANIUM_AXE, "Veritanium Axe");
        addItem(builder, AVPItems.VERITANIUM_HOE, "Veritanium Hoe");
        addItem(builder, AVPItems.VERITANIUM_PICKAXE, "Veritanium Pickaxe");
        addItem(builder, AVPItems.VERITANIUM_SHOVEL, "Veritanium Shovel");
        addItem(builder, AVPItems.VERITANIUM_SWORD, "Veritanium Sword");

        // Spawn Egg Items
        addItem(builder, AVPSpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Aberrant Chestburster Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Aberrant Drone Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Aberrant Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_OVOMORPH_SPAWN_EGG, "Aberrant Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Aberrant Praetorian Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Aberrant Warrior Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG, "Aberrant Queen Spawn Egg");
        addItem(builder, AVPSpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Chestburster Spawn Egg");
        addItem(builder, AVPSpawnEggItems.DRONE_SPAWN_EGG, "Drone Spawn Egg");
        addItem(builder, AVPSpawnEggItems.FACEHUGGER_SPAWN_EGG, "Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Nether Chestburster Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Nether Drone Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Nether Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_OVOMORPH_SPAWN_EGG, "Nether Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Nether Praetorian Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Nether Warrior Spawn Egg");
        addItem(builder, AVPSpawnEggItems.NETHER_QUEEN_SPAWN_EGG, "Nether Queen Spawn Egg");
        addItem(builder, AVPSpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG, "Irradiated Drone Spawn Egg");
        addItem(builder, AVPSpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG, "Irradiated Praetorian Spawn Egg");
        addItem(builder, AVPSpawnEggItems.IRRADIATED_QUEEN_SPAWN_EGG, "Irradiated Queen Spawn Egg");
        addItem(builder, AVPSpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG, "Irradiated Warrior Spawn Egg");
        addItem(builder, AVPSpawnEggItems.OVOMORPH_SPAWN_EGG, "Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.PRAETORIAN_SPAWN_EGG, "Praetorian Spawn Egg");
        addItem(builder, AVPSpawnEggItems.QUEEN_SPAWN_EGG, "Queen Spawn Egg");
        addItem(builder, AVPSpawnEggItems.WARRIOR_SPAWN_EGG, "Warrior Spawn Egg");
        addItem(builder, AVPSpawnEggItems.YAUTJA_SPAWN_EGG, "Yautja Spawn Egg");
        addItem(builder, AVPSpawnEggItems.MARINE_SPAWN_EGG, "Marine Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_OVOMORPH_SPAWN_EGG, "Royal Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG, "Royal Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG, "Royal Chestburster Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_NETHER_OVOMORPH_SPAWN_EGG, "Royal Nether Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG, "Royal Nether Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG, "Royal Nether Chestburster Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_ABERRANT_OVOMORPH_SPAWN_EGG, "Royal Aberrant Ovomorph Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG, "Royal Aberrant Facehugger Spawn Egg");
        addItem(builder, AVPSpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG, "Royal Aberrant Chestburster Spawn Egg");

        // Sounds
        addSound(builder, AVPSoundEvents.BLOCK_ACID_BURN, "Acid burns");
        addSound(builder, AVPSoundEvents.BLOCK_RESIN_SPREAD, "Xenomorph spreads resin");

        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_HATCH, "Ovomorph hatches");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_LAID, "Queen lays egg");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_ROOT, "Ovomorph takes root");
        addSound(builder, AVPSoundEvents.ENTITY_OVOMORPH_SHEAR, "Ovomorph de-roots");

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

        // TODO: Change this to "ovomorph" with 0.2.0.
        builder.add("advancements.aliens.shear_an_ovamorph.title", "Eggsploration Time");
        // TODO: Change this to "ovomorph" with 0.2.0.
        builder.add("advancements.aliens.shear_an_ovamorph.description", "Free an ovamorph from its bindings");

        builder.add("advancements.aliens.plated_chitin_armor.title", "Kneel to the Crown");
        builder.add("advancements.aliens.plated_chitin_armor.description", "Equip a full set of plated chitin armor");

        // Hive boss bars
        HiveBossBarManager.ALIEN_VARIANT_TO_TRANSLATABLE_STRING_MAP.forEach((alienVariant, translationKey) -> {
            var prefix = switch (alienVariant) {
                case ABERRANT -> "Aberrant ";
                case IRRADIATED -> "Irradiated ";
                case NETHER -> "Nether ";
                case NORMAL -> "";
            };

            builder.add(translationKey, prefix + "Hive");
        });

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
        // TODO: Change this to "ovomorph" with 0.2.0.
        builder.add("config.avp.option.NETHER_OVAMORPH_SPAWN", "Nether Ovomorph spawn settings");
        builder.add("config.avp.option.NETHER_PRAETORIAN_SPAWN", "Nether Praetorian spawn settings");
        builder.add("config.avp.option.NETHER_WARRIOR_SPAWN", "Nether Warrior spawn settings");
        builder.add("config.avp.option.NETHER_QUEEN_SPAWN", "Nether Queen spawn settings");
        // TODO: Change this to "ovomorph" with 0.2.0.
        builder.add("config.avp.option.OVAMORPH_SPAWN", "Ovomorph spawn settings");
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
        builder.add("config.avp.option.requiresResin", "Requires resin for Nether Ovomorph spawning");

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
        // TODO: Change this to "ovomorph" with 0.2.0.
        builder.add("config.avp.option.OVAMORPH_STATS", "Ovomorph stats");
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

    private void addEntity(TranslationBuilder translationBuilder, Supplier<? extends EntityType<?>> entityTypeSupplier, String value) {
        addEntity(translationBuilder, entityTypeSupplier.get(), value);
    }

    private void addEntity(TranslationBuilder translationBuilder, EntityType<?> entityType, String value) {
        translationBuilder.add(entityType, value);
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
