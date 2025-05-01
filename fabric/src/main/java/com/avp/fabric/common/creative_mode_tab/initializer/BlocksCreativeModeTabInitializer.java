package com.avp.fabric.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.block.TempAVPBlocks;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.creative_mode_tab.CreativeModeTabs;

public class BlocksCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BLOCKS_KEY).register(entries -> {
            // Worldgen (natural) blocks
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.AUTUNITE_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.BAUXITE_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.DEEPSLATE_ZINC_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.GALENA_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.LITHIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.LITHIUM_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.MONAZITE_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.SILICA_GRAVEL);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ZINC_ORE.get());
            CreativeModeTabUtil.accept(entries, AVPBlocks.AUTUNITE_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_BAUXITE_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_GALENA_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_MONAZITE_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_SILICA_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_TITANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RAW_ZINC_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TRINITITE_BLOCK);

            // Unnatural blocks (Metals, machines, etc.)
            // Aluminum
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ALUMINUM_BLOCK);

            // Brass
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.BRASS_BLOCK);

            // Industrial Glass
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.INDUSTRIAL_GLASS_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.INDUSTRIAL_GLASS_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS);

            // Ferroaluminum
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CHISELED_FERROALUMINUM);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_FERROALUMINUM);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_FERROALUMINUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_FERROALUMINUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_COLUMN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_GRATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_GRATE_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_PLATING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_PLATING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS);

            // Steel
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_BARS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CHISELED_STEEL);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_STEEL);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_STEEL_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_STEEL_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_COLUMN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_GRATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_GRATE_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_GRATE_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_PLATING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_PLATING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_PLATING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_PRESSURE_PLATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD_STAIRS);

            // Titanium
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CHISELED_TITANIUM);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_TITANIUM);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_TITANIUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.CUT_TITANIUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_COLUMN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_GRATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_GRATE_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_GRATE_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_PLATING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_PLATING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_PLATING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_PRESSURE_PLATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD_STAIRS);

            // Uranium and Zinc
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.URANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ZINC_BLOCK);

            // Miscellaneous
            CreativeModeTabUtil.accept(entries, AVPBlocks.NUKE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.SENTRY_TURRET);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ASH_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.LEAD_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.LEAD_CHEST);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAZOR_WIRE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.BLUEPRINT_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.REDSTONE_GENERATOR);
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_FURNACE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.DESK_TERMINAL_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TRIP_MINE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESONATOR_BLOCK);

            // Alien blocks
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_WEB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.NETHER_RESIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.NETHER_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.NETHER_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ABERRANT_RESIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ABERRANT_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ABERRANT_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.IRRADIATED_RESIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.IRRADIATED_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.IRRADIATED_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ROYAL_JELLY_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_BRICKS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_O);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_RIBBED);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.RESIN_SMOOTH);
        });
    }
}
