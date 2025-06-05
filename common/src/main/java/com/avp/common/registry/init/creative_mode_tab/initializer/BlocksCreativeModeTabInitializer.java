package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.predator.common.registry.init.PredatorBlocks;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;

public class BlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // Worldgen (natural) blocks
        CreativeModeTabUtil.accept(output, CoreBlocks.AUTUNITE_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.BAUXITE_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.DEEPSLATE_ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.GALENA_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.LITHIUM_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.LITHIUM_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.MONAZITE_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.SILICA_GRAVEL);
        CreativeModeTabUtil.accept(output, CoreBlocks.DEEPSLATE_TITANIUM_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, CoreBlocks.AUTUNITE_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.RAW_BAUXITE_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.RAW_GALENA_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.RAW_MONAZITE_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.SILICON_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.RAW_TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.RAW_ZINC_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.TRINITITE_BLOCK);

        // Unnatural blocks (Metals, machines, etc.)
        // Aluminum
        CreativeModeTabUtil.accept(output, CoreBlocks.ALUMINUM_BLOCK);

        // Brass
        CreativeModeTabUtil.accept(output, CoreBlocks.BRASS_BLOCK);

        // Industrial Glass
        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR);
        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB);
        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS);

        // Ferroaluminum
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.CUT_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_DOOR);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS);

        // Steel
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_BLOCK);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_BARS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.CHISELED_STEEL);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.CUT_STEEL);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.CUT_STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.CUT_STEEL_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_BUTTON);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_COLUMN);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_DOOR);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_GRATE);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_PLATING);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_SIDING);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_STANDING);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_TREAD);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, HumanSteelBlocks.STEEL_TREAD_STAIRS);

        // Titanium
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.CHISELED_TITANIUM);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.CUT_TITANIUM);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.CUT_TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.CUT_TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_BUTTON);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_COLUMN);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_DOOR);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_GRATE);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_PLATING);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_SIDING);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_STANDING);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_TREAD);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS);

        // Uranium and Zinc
        CreativeModeTabUtil.accept(output, CoreBlocks.URANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.ZINC_BLOCK);

        // Miscellaneous
        CreativeModeTabUtil.accept(output, AVPBlocks.NUKE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.SENTRY_TURRET);
        CreativeModeTabUtil.accept(output, CoreBlocks.ASH_BLOCK);
        CreativeModeTabUtil.accept(output, CoreBlocks.LEAD_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.LEAD_CHEST);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAZOR_WIRE);
        CreativeModeTabUtil.accept(output, AVPBlocks.BLUEPRINT_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.REDSTONE_GENERATOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_FURNACE);
        CreativeModeTabUtil.accept(output, AVPBlocks.DESK_TERMINAL_BLOCK);
        CreativeModeTabUtil.accept(output, PredatorBlocks.TRIP_MINE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESONATOR_BLOCK);

        // Alien blocks
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_SLAB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_STAIRS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_WEB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.NETHER_RESIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.NETHER_RESIN_SLAB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.NETHER_RESIN_STAIRS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.NETHER_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.NETHER_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.ABERRANT_RESIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.ABERRANT_RESIN_SLAB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.ABERRANT_RESIN_STAIRS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.ABERRANT_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.ABERRANT_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.IRRADIATED_RESIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.IRRADIATED_RESIN_SLAB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.IRRADIATED_RESIN_STAIRS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.IRRADIATED_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.IRRADIATED_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AlienBlocks.ROYAL_JELLY_BLOCK);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_BRICKS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_BRICK_SLAB);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_BRICK_STAIRS);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RESIN_VENT);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.RIBBED_RESIN);
        CreativeModeTabUtil.accept(output, AlienResinBlocks.SMOOTH_RESIN);
    };
}
