package com.avp.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.block.AVPBlocks;
import com.avp.common.creative_mode_tab.CreativeModeTabs;

public class BlocksCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BLOCKS_KEY).register(entries -> {
            // Worldgen (natural) blocks
            entries.accept(AVPBlocks.AUTUNITE_ORE);
            entries.accept(AVPBlocks.BAUXITE_ORE);
            entries.accept(AVPBlocks.DEEPSLATE_ZINC_ORE);
            entries.accept(AVPBlocks.GALENA_ORE);
            entries.accept(AVPBlocks.LITHIUM_BLOCK);
            entries.accept(AVPBlocks.LITHIUM_ORE);
            entries.accept(AVPBlocks.MONAZITE_ORE);
            entries.accept(AVPBlocks.SILICA_GRAVEL);
            entries.accept(AVPBlocks.DEEPSLATE_TITANIUM_ORE);
            entries.accept(AVPBlocks.ZINC_ORE);
            entries.accept(AVPBlocks.AUTUNITE_BLOCK);
            entries.accept(AVPBlocks.RAW_BAUXITE_BLOCK);
            entries.accept(AVPBlocks.RAW_GALENA_BLOCK);
            entries.accept(AVPBlocks.RAW_MONAZITE_BLOCK);
            entries.accept(AVPBlocks.RAW_SILICA_BLOCK);
            entries.accept(AVPBlocks.RAW_TITANIUM_BLOCK);
            entries.accept(AVPBlocks.RAW_ZINC_BLOCK);

            // Unnatural blocks (Metals, machines, etc.)
            entries.accept(AVPBlocks.ALUMINUM_BLOCK);
            entries.accept(AVPBlocks.BRASS_BLOCK);
            entries.accept(AVPBlocks.FERROALUMINUM_BLOCK);
            entries.accept(AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
            entries.accept(AVPBlocks.CHISELED_FERROALUMINUM);
            entries.accept(AVPBlocks.CUT_FERROALUMINUM);
            entries.accept(AVPBlocks.CUT_FERROALUMINUM_STAIRS);
            entries.accept(AVPBlocks.CUT_FERROALUMINUM_SLAB);
            entries.accept(AVPBlocks.FERROALUMINUM_COLUMN);
            entries.accept(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
            entries.accept(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
            entries.accept(AVPBlocks.FERROALUMINUM_GRATE);
            entries.accept(AVPBlocks.FERROALUMINUM_PLATING);
            entries.accept(AVPBlocks.FERROALUMINUM_SIDING);
            entries.accept(AVPBlocks.FERROALUMINUM_STANDING);
            entries.accept(AVPBlocks.FERROALUMINUM_TREAD);
            entries.accept(AVPBlocks.LEAD_BLOCK);
            entries.accept(AVPBlocks.STEEL_BLOCK);
            entries.accept(AVPBlocks.STEEL_BARS);
            entries.accept(AVPBlocks.STEEL_CHAIN_FENCE);
            entries.accept(AVPBlocks.CHISELED_STEEL);
            entries.accept(AVPBlocks.CUT_STEEL);
            entries.accept(AVPBlocks.CUT_STEEL_STAIRS);
            entries.accept(AVPBlocks.CUT_STEEL_SLAB);
            entries.accept(AVPBlocks.STEEL_COLUMN);
            entries.accept(AVPBlocks.STEEL_FASTENED_SIDING);
            entries.accept(AVPBlocks.STEEL_FASTENED_STANDING);
            entries.accept(AVPBlocks.STEEL_GRATE);
            entries.accept(AVPBlocks.STEEL_PLATING);
            entries.accept(AVPBlocks.STEEL_SIDING);
            entries.accept(AVPBlocks.STEEL_STANDING);
            entries.accept(AVPBlocks.STEEL_TREAD);
            entries.accept(AVPBlocks.TITANIUM_BLOCK);
            entries.accept(AVPBlocks.TITANIUM_CHAIN_FENCE);
            entries.accept(AVPBlocks.CHISELED_TITANIUM);
            entries.accept(AVPBlocks.CUT_TITANIUM);
            entries.accept(AVPBlocks.CUT_TITANIUM_STAIRS);
            entries.accept(AVPBlocks.CUT_TITANIUM_SLAB);
            entries.accept(AVPBlocks.TITANIUM_COLUMN);
            entries.accept(AVPBlocks.TITANIUM_FASTENED_SIDING);
            entries.accept(AVPBlocks.TITANIUM_FASTENED_STANDING);
            entries.accept(AVPBlocks.TITANIUM_GRATE);
            entries.accept(AVPBlocks.TITANIUM_PLATING);
            entries.accept(AVPBlocks.TITANIUM_SIDING);
            entries.accept(AVPBlocks.TITANIUM_STANDING);
            entries.accept(AVPBlocks.TITANIUM_TREAD);
            entries.accept(AVPBlocks.URANIUM_BLOCK);
            entries.accept(AVPBlocks.ZINC_BLOCK);
            entries.accept(AVPBlocks.RAZOR_WIRE);

            // Alien blocks
            entries.accept(AVPBlocks.RESIN);
            entries.accept(AVPBlocks.RESIN_VEIN);
            entries.accept(AVPBlocks.RESIN_WEB);
            entries.accept(AVPBlocks.NETHER_RESIN);
            entries.accept(AVPBlocks.NETHER_RESIN_VEIN);
            entries.accept(AVPBlocks.NETHER_RESIN_WEB);
        });
    }
}
