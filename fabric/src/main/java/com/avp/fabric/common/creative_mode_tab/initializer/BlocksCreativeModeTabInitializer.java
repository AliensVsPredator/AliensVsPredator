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
            CreativeModeTabUtil.accept(entries, AVPBlocks.LITHIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.LITHIUM_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.MONAZITE_ORE.get());
            CreativeModeTabUtil.accept(entries, AVPBlocks.SILICA_GRAVEL);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get());
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ZINC_ORE.get());
            CreativeModeTabUtil.accept(entries, AVPBlocks.AUTUNITE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_BAUXITE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_GALENA_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_MONAZITE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_SILICA_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_TITANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAW_ZINC_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TRINITITE_BLOCK);

            // Unnatural blocks (Metals, machines, etc.)
            // Aluminum
            CreativeModeTabUtil.accept(entries, AVPBlocks.ALUMINUM_BLOCK);

            // Brass
            CreativeModeTabUtil.accept(entries, AVPBlocks.BRASS_BLOCK);

            // Industrial Glass
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_GLASS_DOOR);
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_GLASS_SLAB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_GLASS_STAIRS);

            // Ferroaluminum
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CHISELED_FERROALUMINUM);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_FERROALUMINUM);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_FERROALUMINUM_STAIRS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_FERROALUMINUM_SLAB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.FERROALUMINUM_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_COLUMN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.FERROALUMINUM_DOOR);
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
            CreativeModeTabUtil.accept(entries, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.FERROALUMINUM_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS);

            // Steel
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_BARS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CHISELED_STEEL);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_STEEL);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_STEEL_STAIRS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_STEEL_SLAB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.STEEL_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_COLUMN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.STEEL_DOOR);
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
            CreativeModeTabUtil.accept(entries, AVPBlocks.STEEL_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.STEEL_TREAD_STAIRS);

            // Titanium
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_CHAIN_FENCE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CHISELED_TITANIUM);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_TITANIUM);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_TITANIUM_STAIRS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.CUT_TITANIUM_SLAB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TITANIUM_BUTTON);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_COLUMN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TITANIUM_DOOR);
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
            CreativeModeTabUtil.accept(entries, AVPBlocks.TITANIUM_PRESSURE_PLATE);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SIDING_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STAIRS);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_STANDING_STAIRS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TITANIUM_TRAP_DOOR);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD_SLAB);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.TITANIUM_TREAD_STAIRS);

            // Uranium and Zinc
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.URANIUM_BLOCK);
            CreativeModeTabUtil.accept(entries, TempAVPBlocks.ZINC_BLOCK);

            // Miscellaneous
            CreativeModeTabUtil.accept(entries, AVPBlocks.NUKE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.SENTRY_TURRET);
            CreativeModeTabUtil.accept(entries, AVPBlocks.ASH_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.LEAD_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.LEAD_CHEST);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RAZOR_WIRE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.BLUEPRINT_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.REDSTONE_GENERATOR);
            CreativeModeTabUtil.accept(entries, AVPBlocks.INDUSTRIAL_FURNACE);
            CreativeModeTabUtil.accept(entries, AVPBlocks.DESK_TERMINAL_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.TRIP_MINE_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESONATOR_BLOCK);

            // Alien blocks
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_WEB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.NETHER_RESIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.NETHER_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.NETHER_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.ABERRANT_RESIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.ABERRANT_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.ABERRANT_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.IRRADIATED_RESIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.IRRADIATED_RESIN_VEIN);
            CreativeModeTabUtil.accept(entries, AVPBlocks.IRRADIATED_RESIN_WEB);
            CreativeModeTabUtil.accept(entries, AVPBlocks.ROYAL_JELLY_BLOCK);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_BRICKS);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_O);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_RIBBED);
            CreativeModeTabUtil.accept(entries, AVPBlocks.RESIN_SMOOTH);
        });
    }
}
