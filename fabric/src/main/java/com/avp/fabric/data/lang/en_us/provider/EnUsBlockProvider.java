package com.avp.fabric.data.lang.en_us.provider;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.block.AlienChitinBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.predator.common.registry.init.PredatorBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.common.registry.AVPRegistryValidation;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;

public class EnUsBlockProvider {

    private static final HashSet<Block> TOUCHED_ENTRIES = new HashSet<>();

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addBlock(builder, AVPBlocks.BLUEPRINT_BLOCK, "Blueprint Block");
        addBlock(builder, AVPBlocks.CABLE, "Cable");
        addBlock(builder, AVPBlocks.REDSTONE_GENERATOR, "Redstone Generator");
        addBlock(builder, AVPBlocks.DESK_TERMINAL_BLOCK, "Desk Terminal");
        addBlock(builder, PredatorBlocks.TRIP_MINE_BLOCK, "Trip Mine");
        addBlock(builder, AVPBlocks.RESONATOR_BLOCK, "Resonator");
        addBlock(builder, AVPBlocks.AMMO_CHEST, "Ammo Chest");
        addBlock(builder, AVPBlocks.SENTRY_TURRET, "Sentry Turret");
        addBlock(builder, CoreBlocks.ASH_BLOCK, "Ash Block");
        addBlock(builder, AVPBlocks.NUKE_BLOCK, "Nuke Block");
        addBlock(builder, AlienBlocks.ROYAL_JELLY_BLOCK, "Royal Jelly Block");
        addBlock(builder, CoreBlocks.TRINITITE_BLOCK, "Block of Trinitite");
        addBlock(builder, CoreBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        addBlock(builder, CoreBlocks.AUTUNITE_BLOCK, "Autunite Block");
        addBlock(builder, CoreBlocks.AUTUNITE_ORE, "Autunite Ore");
        addBlock(builder, CoreBlocks.BAUXITE_ORE, "Bauxite Ore");
        addBlock(builder, CoreBlocks.BRASS_BLOCK, "Block of Brass");
        addBlock(builder, HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM, "Chiseled Ferroaluminum");
        addBlock(builder, HumanSteelBlocks.CHISELED_STEEL, "Chiseled Steel");
        addBlock(builder, HumanTitaniumBlocks.CHISELED_TITANIUM, "Chiseled Titanium");
        addBlock(builder, HumanFerroaluminumBlocks.CUT_FERROALUMINUM, "Cut Ferroaluminum");
        addBlock(builder, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB, "Cut Ferroaluminum Slab");
        addBlock(builder, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS, "Cut Ferroaluminum Stairs");
        addBlock(builder, HumanSteelBlocks.CUT_STEEL, "Cut Steel");
        addBlock(builder, HumanSteelBlocks.CUT_STEEL_SLAB, "Cut Steel Slab");
        addBlock(builder, HumanSteelBlocks.CUT_STEEL_STAIRS, "Cut Steel Stairs");
        addBlock(builder, HumanTitaniumBlocks.CUT_TITANIUM, "Cut Titanium");
        addBlock(builder, HumanTitaniumBlocks.CUT_TITANIUM_SLAB, "Cut Titanium Slab");
        addBlock(builder, HumanTitaniumBlocks.CUT_TITANIUM_STAIRS, "Cut Titanium Stairs");
        addBlock(builder, CoreBlocks.DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(builder, CoreBlocks.DEEPSLATE_ZINC_ORE, "Deepslate Zinc Ore");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK, "Block of Ferroaluminum");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON, "Ferroaluminum Button");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE, "Ferroaluminum Chain Fence");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN, "Ferroaluminum Column");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_DOOR, "Ferroaluminum Door");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING, "Ferroaluminum Fastened Siding");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING, "Ferroaluminum Fastened Standing");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE, "Ferroaluminum Grate");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE, "Ferroaluminum Pressure Plate");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING, "Ferroaluminum Plating");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING, "Ferroaluminum Siding");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING, "Ferroaluminum Standing");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR, "Ferroaluminum Trapdoor");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD, "Ferroaluminum Tread");
        addBlock(builder, CoreBlocks.GALENA_ORE, "Galena Ore");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS, "Industrial Glass");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE, "Industrial Glass Pane");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR, "Industrial Glass Door");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR, "Industrial Glass Trap Door");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB, "Industrial Glass Slab");
        addBlock(builder, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS, "Industrial Glass Stairs");
        addBlock(builder, CoreBlocks.LEAD_BLOCK, "Block of Lead");
        addBlock(builder, AVPBlocks.LEAD_CHEST, "Lead Chest");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Ferroaluminum Fastened Standing Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Ferroaluminum Fastened Standing Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB, "Ferroaluminum Grate Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS, "Ferroaluminum Grate Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB, "Ferroaluminum Plating Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS, "Ferroaluminum Plating Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB, "Ferroaluminum Standing Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS, "Ferroaluminum Standing Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB, "Ferroaluminum Tread Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS, "Ferroaluminum Tread Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB, "Steel Fastened Standing Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS, "Steel Fastened Standing Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_GRATE_SLAB, "Steel Grate Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_GRATE_STAIRS, "Steel Grate Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_PLATING_SLAB, "Steel Plating Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_PLATING_STAIRS, "Steel Plating Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_TREAD_SLAB, "Steel Tread Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_TREAD_STAIRS, "Steel Tread Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Titanium Fastened Standing Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Titanium Fastened Standing Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_GRATE_SLAB, "Titanium Grate Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS, "Titanium Grate Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_PLATING_SLAB, "Titanium Plating Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS, "Titanium Plating Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_TREAD_SLAB, "Titanium Tread Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS, "Titanium Tread Stairs");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Slab")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Concrete Stairs")
        );

        HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete")
        );
        HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Slab")
        );
        HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Stairs")
        );
        HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Concrete Wall")
        );
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass")
        );
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Industrial Glass Pane")
        );

        addBlock(builder, CoreBlocks.LITHIUM_BLOCK, "Block of Lithium");
        addBlock(builder, CoreBlocks.LITHIUM_ORE, "Lithium Ore");
        addBlock(builder, CoreBlocks.MONAZITE_ORE, "Monazite Ore");

        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN, "Aberrant Resin");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_BRICKS, "Aberrant Resin Bricks");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB, "Aberrant Resin Brick Slab");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS, "Aberrant Resin Brick Stairs");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_BRICK_WALL, "Aberrant Resin Brick Wall");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_SLAB, "Aberrant Resin Slab");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_STAIRS, "Aberrant Resin Stairs");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_NODE, "Aberrant Resin");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_VEIN, "Aberrant Resin Vein");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_VENT, "Aberrant Resin Vent");
        addBlock(builder, AlienResinBlocks.ABERRANT_RESIN_WEB, "Aberrant Resin Web");

        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN, "Irradiated Resin");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_BRICKS, "Irradiated Resin Bricks");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB, "Irradiated Resin Brick Slab");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS, "Irradiated Resin Brick Stairs");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL, "Irradiated Resin Brick Wall");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_SLAB, "Irradiated Resin Slab");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_STAIRS, "Irradiated Resin Stairs");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_NODE, "Irradiated Resin");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_VEIN, "Irradiated Resin Vein");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_VENT, "Irradiated Resin Vent");
        addBlock(builder, AlienResinBlocks.IRRADIATED_RESIN_WEB, "Irradiated Resin Web");

        addBlock(builder, AlienResinBlocks.NETHER_RESIN, "Nether Resin");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_BRICKS, "Nether Resin Bricks");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_BRICK_SLAB, "Nether Resin Brick Slab");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_BRICK_STAIRS, "Nether Resin Brick Stairs");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_BRICK_WALL, "Nether Resin Brick Wall");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_SLAB, "Nether Resin Slab");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_STAIRS, "Nether Resin Stairs");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_NODE, "Nether Resin");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_VEIN, "Nether Resin Vein");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_VENT, "Nether Resin Vent");
        addBlock(builder, AlienResinBlocks.NETHER_RESIN_WEB, "Nether Resin Web");

        addBlock(builder, AlienResinBlocks.RESIN, "Resin");
        addBlock(builder, AlienResinBlocks.RESIN_BRICKS, "Resin Bricks");
        addBlock(builder, AlienResinBlocks.RESIN_BRICK_SLAB, "Resin Brick Slab");
        addBlock(builder, AlienResinBlocks.RESIN_BRICK_STAIRS, "Resin Brick Stairs");
        addBlock(builder, AlienResinBlocks.RESIN_BRICK_WALL, "Resin Brick Wall");
        addBlock(builder, AlienResinBlocks.RESIN_NODE, "Resin");
        addBlock(builder, AlienResinBlocks.RESIN_SLAB, "Resin Slab");
        addBlock(builder, AlienResinBlocks.RESIN_STAIRS, "Resin Stairs");
        addBlock(builder, AlienResinBlocks.RESIN_VEIN, "Resin Vein");
        addBlock(builder, AlienResinBlocks.RESIN_VENT, "Resin Vent");
        addBlock(builder, AlienResinBlocks.RESIN_WEB, "Resin Web");

        addBlock(builder, AlienResinBlocks.RIBBED_ABERRANT_RESIN, "Ribbed Aberrant Resin");
        addBlock(builder, AlienResinBlocks.RIBBED_IRRADIATED_RESIN, "Ribbed Irradiated Resin");
        addBlock(builder, AlienResinBlocks.RIBBED_NETHER_RESIN, "Ribbed Nether Resin");
        addBlock(builder, AlienResinBlocks.RIBBED_RESIN, "Ribbed Resin");
        addBlock(builder, AlienResinBlocks.SMOOTH_ABERRANT_RESIN, "Smooth Aberrant Resin");
        addBlock(builder, AlienResinBlocks.SMOOTH_ABERRANT_RESIN_SLAB, "Smooth Aberrant Resin Slab");
        addBlock(builder, AlienResinBlocks.SMOOTH_ABERRANT_RESIN_STAIRS, "Smooth Aberrant Resin Stairs");
        addBlock(builder, AlienResinBlocks.SMOOTH_ABERRANT_RESIN_WALL, "Smooth Aberrant Resin Wall");
        addBlock(builder, AlienResinBlocks.SMOOTH_IRRADIATED_RESIN, "Smooth Irradiated Resin");
        addBlock(builder, AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_SLAB, "Smooth Irradiated Resin Slab");
        addBlock(builder, AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_STAIRS, "Smooth Irradiated Resin Stairs");
        addBlock(builder, AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_WALL, "Smooth Irradiated Resin Wall");
        addBlock(builder, AlienResinBlocks.SMOOTH_NETHER_RESIN, "Smooth Nether Resin");
        addBlock(builder, AlienResinBlocks.SMOOTH_NETHER_RESIN_SLAB, "Smooth Nether Resin Slab");
        addBlock(builder, AlienResinBlocks.SMOOTH_NETHER_RESIN_STAIRS, "Smooth Nether Resin Stairs");
        addBlock(builder, AlienResinBlocks.SMOOTH_NETHER_RESIN_WALL, "Smooth Nether Resin Wall");
        addBlock(builder, AlienResinBlocks.SMOOTH_RESIN, "Smooth Resin");
        addBlock(builder, AlienResinBlocks.SMOOTH_RESIN_SLAB, "Smooth Resin Slab");
        addBlock(builder, AlienResinBlocks.SMOOTH_RESIN_STAIRS, "Smooth Resin Stairs");
        addBlock(builder, AlienResinBlocks.SMOOTH_RESIN_WALL, "Smooth Resin Wall");

        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BLOCK, "Block of Aberrant Chitin");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_SLAB, "Aberrant Chitin Slab");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_STAIRS, "Aberrant Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_WALL, "Aberrant Chitin Wall");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BRICKS, "Aberrant Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BRICK_SLAB, "Aberrant Chitin Brick Slab");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BRICK_STAIRS, "Aberrant Chitin Brick Stairs");
        addBlock(builder, AlienChitinBlocks.ABERRANT_CHITIN_BRICK_WALL, "Aberrant Chitin Brick Wall");
        addBlock(builder, AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS, "Chiseled Aberrant Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS_EMBRYO, "Chiseled Aberrant Chitin Bricks (Embryo)");
        addBlock(builder, AlienChitinBlocks.POLISHED_ABERRANT_CHITIN, "Polished Aberrant Chitin");
        addBlock(builder, AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_SLAB, "Polished Aberrant Chitin Slab");
        addBlock(builder, AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_STAIRS, "Polished Aberrant Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_WALL, "Polished Aberrant Chitin Wall");

        addBlock(builder, AlienChitinBlocks.CHITIN_BLOCK, "Block of Chitin");
        addBlock(builder, AlienChitinBlocks.CHITIN_BLOCK_SLAB, "Chitin Slab");
        addBlock(builder, AlienChitinBlocks.CHITIN_BLOCK_STAIRS, "Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.CHITIN_BLOCK_WALL, "Chitin Wall");
        addBlock(builder, AlienChitinBlocks.CHITIN_BRICKS, "Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.CHITIN_BRICK_SLAB, "Chitin Brick Slab");
        addBlock(builder, AlienChitinBlocks.CHITIN_BRICK_STAIRS, "Chitin Brick Stairs");
        addBlock(builder, AlienChitinBlocks.CHITIN_BRICK_WALL, "Chitin Brick Wall");
        addBlock(builder, AlienChitinBlocks.CHISELED_CHITIN_BRICKS, "Chiseled Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.CHISELED_CHITIN_BRICKS_EMBRYO, "Chiseled Chitin Bricks (Embryo)");
        addBlock(builder, AlienChitinBlocks.POLISHED_CHITIN, "Polished Chitin");
        addBlock(builder, AlienChitinBlocks.POLISHED_CHITIN_SLAB, "Polished Chitin Slab");
        addBlock(builder, AlienChitinBlocks.POLISHED_CHITIN_STAIRS, "Polished Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.POLISHED_CHITIN_WALL, "Polished Chitin Wall");

        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BLOCK, "Block of Nether Chitin");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BLOCK_SLAB, "Nether Chitin Slab");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BLOCK_STAIRS, "Nether Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BLOCK_WALL, "Nether Chitin Wall");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BRICKS, "Nether Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BRICK_SLAB, "Nether Chitin Brick Slab");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BRICK_STAIRS, "Nether Chitin Brick Stairs");
        addBlock(builder, AlienChitinBlocks.NETHER_CHITIN_BRICK_WALL, "Nether Chitin Brick Wall");
        addBlock(builder, AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS, "Chiseled Nether Chitin Bricks");
        addBlock(builder, AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS_EMBRYO, "Chiseled Nether Chitin Bricks (Embryo)");
        addBlock(builder, AlienChitinBlocks.POLISHED_NETHER_CHITIN, "Polished Nether Chitin");
        addBlock(builder, AlienChitinBlocks.POLISHED_NETHER_CHITIN_SLAB, "Polished Nether Chitin Slab");
        addBlock(builder, AlienChitinBlocks.POLISHED_NETHER_CHITIN_STAIRS, "Polished Nether Chitin Stairs");
        addBlock(builder, AlienChitinBlocks.POLISHED_NETHER_CHITIN_WALL, "Polished Nether Chitin Wall");

        HumanPaddingBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Slab")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Padding Stairs")
        );

        HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Slab")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Panel Padding Stairs")
        );

        HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Slab")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pipe Padding Stairs")
        );

        HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Slab")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Cut Plastic Stairs")
        );

        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Slab")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Stairs")
        );

        addBlock(builder, CoreBlocks.RAW_BAUXITE_BLOCK, "Block of Raw Bauxite");
        addBlock(builder, CoreBlocks.RAW_GALENA_BLOCK, "Block of Raw Galena");
        addBlock(builder, CoreBlocks.RAW_MONAZITE_BLOCK, "Block of Raw Monazite");
        addBlock(builder, CoreBlocks.SILICON_BLOCK, "Block of Silicon");
        addBlock(builder, CoreBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(builder, CoreBlocks.RAW_ZINC_BLOCK, "Block of Raw Zinc");
        addBlock(builder, AVPBlocks.RAZOR_WIRE, "Razor Wire");
        addBlock(builder, CoreBlocks.SILICA_GRAVEL, "Silica Gravel");
        addBlock(builder, HumanSteelBlocks.STEEL_BARS, "Steel Bars");
        addBlock(builder, HumanSteelBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(builder, HumanSteelBlocks.STEEL_BUTTON, "Steel Button");
        addBlock(builder, HumanSteelBlocks.STEEL_CHAIN_FENCE, "Steel Chain Fence");
        addBlock(builder, HumanSteelBlocks.STEEL_COLUMN, "Steel Column");
        addBlock(builder, HumanSteelBlocks.STEEL_DOOR, "Steel Door");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_SIDING, "Steel Fastened Siding");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_STANDING, "Steel Fastened Standing");
        addBlock(builder, HumanSteelBlocks.STEEL_GRATE, "Steel Grate");
        addBlock(builder, HumanSteelBlocks.STEEL_PRESSURE_PLATE, "Steel Pressure Plate");
        addBlock(builder, HumanSteelBlocks.STEEL_PLATING, "Steel Plating");
        addBlock(builder, HumanSteelBlocks.STEEL_SIDING, "Steel Siding");
        addBlock(builder, HumanSteelBlocks.STEEL_STANDING, "Steel Standing");
        addBlock(builder, HumanSteelBlocks.STEEL_TRAP_DOOR, "Steel Trapdoor");
        addBlock(builder, HumanSteelBlocks.STEEL_TREAD, "Steel Tread");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_BLOCK, "Block of Titanium");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_BUTTON, "Titanium Button");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE, "Titanium Chain Fence");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_COLUMN, "Titanium Column");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_DOOR, "Titanium Door");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING, "Titanium Fastened Siding");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING, "Titanium Fastened Standing");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_GRATE, "Titanium Grate");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE, "Titanium Pressure Plate");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_PLATING, "Titanium Plating");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_SIDING, "Titanium Siding");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_STANDING, "Titanium Standing");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_TRAP_DOOR, "Titanium Trapdoor");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_TREAD, "Titanium Tread");
        addBlock(builder, CoreBlocks.URANIUM_BLOCK, "Block of Uranium");
        addBlock(builder, CoreBlocks.ZINC_BLOCK, "Block of Zinc");
        addBlock(builder, CoreBlocks.ZINC_ORE, "Zinc Ore");
        addBlock(builder, AVPBlocks.INDUSTRIAL_FURNACE, "Industrial Furnace");
        addBlock(builder, AVPBlocks.BATTERY, "Battery");
        addBlock(builder, AVPBlocks.INFINITE_POWER_GENERATOR, "Infinite Power Generator");
        addBlock(builder, AVPBlocks.SOLAR_PANEL, "Solar Panel");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_SLAB, "Ferroaluminum Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS, "Ferroaluminum Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_SLAB, "Steel Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_STAIRS, "Steel Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_SLAB, "Titanium Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_STAIRS, "Titanium Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB, "Ferroaluminum Siding Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS, "Ferroaluminum Siding Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_SIDING_SLAB, "Steel Siding Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_SIDING_STAIRS, "Steel Siding Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_SIDING_SLAB, "Titanium Siding Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS, "Titanium Siding Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_STANDING_SLAB, "Steel Standing Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_STANDING_STAIRS, "Steel Standing Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_STANDING_SLAB, "Titanium Standing Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS, "Titanium Standing Stairs");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Ferroaluminum Fastened Siding Slab");
        addBlock(builder, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Ferroaluminum Fastened Siding Stairs");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB, "Steel Fastened Siding Slab");
        addBlock(builder, HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS, "Steel Fastened Siding Stairs");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Titanium Fastened Siding Slab");
        addBlock(builder, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Titanium Fastened Siding Stairs");

        AVPRegistryValidation.throwIfMissingEntries(
            AVPBlocks.getAll(),
            TOUCHED_ENTRIES::contains,
            Block::getDescriptionId,
            "Block translation did not complete successfully - there are unhandled blocks that need to be handled."
        );
    };

    private static void addBlock(
        FabricLanguageProvider.TranslationBuilder translationBuilder,
        Supplier<? extends Block> blockSupplier,
        String value
    ) {
        addBlock(translationBuilder, blockSupplier.get(), value);
    }

    private static void addBlock(FabricLanguageProvider.TranslationBuilder translationBuilder, Block block, String value) {
        TOUCHED_ENTRIES.add(block);
        translationBuilder.add(block, value);
    }

    private static String format(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return String.join(" ", Arrays.stream(input.split("_")).map(EnUsBlockProvider::capitalize).toList());
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
