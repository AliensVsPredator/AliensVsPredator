package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.block.AVPBlocks;

public class BlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // Worldgen (natural) blocks
        CreativeModeTabUtil.accept(output, AVPBlocks.AUTUNITE_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.BAUXITE_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.DEEPSLATE_ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.GALENA_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.LITHIUM_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.LITHIUM_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.MONAZITE_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.SILICA_GRAVEL);
        CreativeModeTabUtil.accept(output, AVPBlocks.DEEPSLATE_TITANIUM_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, AVPBlocks.AUTUNITE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAW_BAUXITE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAW_GALENA_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAW_MONAZITE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.SILICON_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAW_TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAW_ZINC_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.TRINITITE_BLOCK);

        // Unnatural blocks (Metals, machines, etc.)
        // Aluminum
        CreativeModeTabUtil.accept(output, AVPBlocks.ALUMINUM_BLOCK);

        // Brass
        CreativeModeTabUtil.accept(output, AVPBlocks.BRASS_BLOCK);

        // Industrial Glass
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS_STAIRS);

        // Ferroaluminum
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, AVPBlocks.CHISELED_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_BUTTON);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_COLUMN);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_GRATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_PLATING);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_TREAD);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.FERROALUMINUM_TREAD_STAIRS);

        // Steel
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_BARS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, AVPBlocks.CHISELED_STEEL);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_STEEL);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_STEEL_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_BUTTON);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_COLUMN);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_GRATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_PLATING);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_TREAD);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.STEEL_TREAD_STAIRS);

        // Titanium
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, AVPBlocks.CHISELED_TITANIUM);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_TITANIUM);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.CUT_TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_BUTTON);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_COLUMN);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_GRATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_PLATING);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_SIDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_STANDING);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_TREAD);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, AVPBlocks.TITANIUM_TREAD_STAIRS);

        // Uranium and Zinc
        CreativeModeTabUtil.accept(output, AVPBlocks.URANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.ZINC_BLOCK);

        // Miscellaneous
        CreativeModeTabUtil.accept(output, AVPBlocks.NUKE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.SENTRY_TURRET);
        CreativeModeTabUtil.accept(output, AVPBlocks.ASH_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.LEAD_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.LEAD_CHEST);
        CreativeModeTabUtil.accept(output, AVPBlocks.RAZOR_WIRE);
        CreativeModeTabUtil.accept(output, AVPBlocks.BLUEPRINT_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.REDSTONE_GENERATOR);
        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_FURNACE);
        CreativeModeTabUtil.accept(output, AVPBlocks.DESK_TERMINAL_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.TRIP_MINE_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESONATOR_BLOCK);

        // Alien blocks
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_WEB);
        CreativeModeTabUtil.accept(output, AVPBlocks.NETHER_RESIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.NETHER_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.NETHER_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AVPBlocks.ABERRANT_RESIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.ABERRANT_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.ABERRANT_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AVPBlocks.IRRADIATED_RESIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.IRRADIATED_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, AVPBlocks.IRRADIATED_RESIN_WEB);
        CreativeModeTabUtil.accept(output, AVPBlocks.ROYAL_JELLY_BLOCK);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_BRICKS);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_O);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_RIBBED);
        CreativeModeTabUtil.accept(output, AVPBlocks.RESIN_SMOOTH);
    };
}
