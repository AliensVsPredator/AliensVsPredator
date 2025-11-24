package com.avp.fabric.data.lang.en_us.provider;

import com.avp.common.registry.AVPRegistryValidation;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnUsBlockProvider {

    private static final HashSet<Block> TOUCHED_ENTRIES = new HashSet<>();

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addBlock(builder, AVPBlocks.BLUEPRINT_BLOCK, "Blueprint Block");
        addBlock(builder, AVPBlocks.CABLE, "Cable");
        addBlock(builder, AVPBlocks.REDSTONE_GENERATOR, "Redstone Generator");
        addBlock(builder, AVPBlocks.DESK_TERMINAL_BLOCK, "Desk Terminal");
        addBlock(builder, AVPBlocks.RESONATOR_BLOCK, "Resonator");
        addBlock(builder, AVPBlocks.AMMO_CHEST, "Ammo Chest");
        addBlock(builder, AVPBlocks.SENTRY_TURRET, "Sentry Turret");
        addBlock(builder, CoreBlocks.ASH_BLOCK, "Ash Block");
        addBlock(builder, AVPBlocks.NUKE_BLOCK, "Nuke Block");
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

        HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Tile Padding")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Tile Padding Slab")
        );
        HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Tile Padding Stairs")
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

        HumanPlasticBlocks.DYE_COLOR_TO_FRAMED_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Framed Plastic")
        );

        HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pitted Plastic")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pitted Plastic Slab")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Pitted Plastic Stairs")
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

        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Grate")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_SLAB.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Grate Slab")
        );
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_STAIRS.forEach(
            (key, value) -> addBlock(builder, value, format(key.getName()) + " Plastic Grate Stairs")
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
        addBlock(builder, AVPBlocks.THERMAL_GENERATOR, "Thermal Generator");
        addBlock(builder, AVPBlocks.WIND_TURBINE, "Wind Turbine");
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
