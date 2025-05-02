package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.block.TempAVPBlocks;

public class BlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // Worldgen (natural) blocks
        CreativeModeTabUtil.accept(output, TempAVPBlocks.AUTUNITE_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.BAUXITE_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.DEEPSLATE_ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.GALENA_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.LITHIUM_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.LITHIUM_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.MONAZITE_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.SILICA_GRAVEL);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ZINC_ORE.get());
        CreativeModeTabUtil.accept(output, TempAVPBlocks.AUTUNITE_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_BAUXITE_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_GALENA_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_MONAZITE_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_SILICA_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RAW_ZINC_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TRINITITE_BLOCK);

        // Unnatural blocks (Metals, machines, etc.)
        // Aluminum
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ALUMINUM_BLOCK);

        // Brass
        CreativeModeTabUtil.accept(output, TempAVPBlocks.BRASS_BLOCK);

        // Industrial Glass
        CreativeModeTabUtil.accept(output, TempAVPBlocks.INDUSTRIAL_GLASS_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.INDUSTRIAL_GLASS_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS);

        // Ferroaluminum
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CHISELED_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_FERROALUMINUM);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_BUTTON);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_COLUMN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_GRATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_PLATING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_TREAD);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS);

        // Steel
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_BARS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CHISELED_STEEL);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_STEEL);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_STEEL_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_BUTTON);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_COLUMN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_GRATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_PLATING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_TREAD);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.STEEL_TREAD_STAIRS);

        // Titanium
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_CHAIN_FENCE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CHISELED_TITANIUM);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_TITANIUM);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.CUT_TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_BUTTON);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_COLUMN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_GRATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_GRATE_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_GRATE_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_PLATING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_PLATING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_PLATING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_PRESSURE_PLATE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_SIDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_SIDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_SIDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_STANDING);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_STANDING_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_STANDING_STAIRS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_TRAP_DOOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_TREAD);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_TREAD_SLAB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TITANIUM_TREAD_STAIRS);

        // Uranium and Zinc
        CreativeModeTabUtil.accept(output, TempAVPBlocks.URANIUM_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ZINC_BLOCK);

        // Miscellaneous
        // FIXME:
        // CreativeModeTabUtil.accept(output, AVPBlocks.NUKE_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.SENTRY_TURRET);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ASH_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.LEAD_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.LEAD_CHEST);
        // CreativeModeTabUtil.accept(output, AVPBlocks.RAZOR_WIRE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.BLUEPRINT_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.REDSTONE_GENERATOR);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.INDUSTRIAL_FURNACE);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.DESK_TERMINAL_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.TRIP_MINE_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESONATOR_BLOCK);

        // Alien blocks
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_VEIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_WEB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.NETHER_RESIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.NETHER_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.NETHER_RESIN_WEB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ABERRANT_RESIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ABERRANT_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ABERRANT_RESIN_WEB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.IRRADIATED_RESIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.IRRADIATED_RESIN_VEIN);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.IRRADIATED_RESIN_WEB);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.ROYAL_JELLY_BLOCK);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_BRICKS);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_O);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_RIBBED);
        CreativeModeTabUtil.accept(output, TempAVPBlocks.RESIN_SMOOTH);
    };
}
