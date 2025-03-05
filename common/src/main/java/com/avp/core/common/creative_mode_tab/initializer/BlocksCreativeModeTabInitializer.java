package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class BlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);

        // Worldgen (natural) blocks
        adaptedOutput.accept(AVPBlocks.AUTUNITE_ORE);
        adaptedOutput.accept(AVPBlocks.BAUXITE_ORE);
        adaptedOutput.accept(AVPBlocks.DEEPSLATE_ZINC_ORE);
        adaptedOutput.accept(AVPBlocks.GALENA_ORE);
        adaptedOutput.accept(AVPBlocks.LITHIUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.LITHIUM_ORE);
        adaptedOutput.accept(AVPBlocks.MONAZITE_ORE);
        adaptedOutput.accept(AVPBlocks.SILICA_GRAVEL);
        adaptedOutput.accept(AVPBlocks.DEEPSLATE_TITANIUM_ORE);
        adaptedOutput.accept(AVPBlocks.ZINC_ORE);
        adaptedOutput.accept(AVPBlocks.AUTUNITE_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_BAUXITE_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_GALENA_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_MONAZITE_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_SILICA_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_TITANIUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAW_ZINC_BLOCK);

        // Unnatural blocks (Metals, machines, etc.)
        adaptedOutput.accept(AVPBlocks.ALUMINUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.BRASS_BLOCK);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        adaptedOutput.accept(AVPBlocks.CHISELED_FERROALUMINUM);
        adaptedOutput.accept(AVPBlocks.CUT_FERROALUMINUM);
        adaptedOutput.accept(AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        adaptedOutput.accept(AVPBlocks.CUT_FERROALUMINUM_SLAB);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_COLUMN);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_GRATE);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_PLATING);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_SIDING);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_STANDING);
        adaptedOutput.accept(AVPBlocks.FERROALUMINUM_TREAD);
        adaptedOutput.accept(AVPBlocks.LEAD_BLOCK);
        adaptedOutput.accept(AVPBlocks.STEEL_BLOCK);
        adaptedOutput.accept(AVPBlocks.STEEL_BARS);
        adaptedOutput.accept(AVPBlocks.STEEL_CHAIN_FENCE);
        adaptedOutput.accept(AVPBlocks.CHISELED_STEEL);
        adaptedOutput.accept(AVPBlocks.CUT_STEEL);
        adaptedOutput.accept(AVPBlocks.CUT_STEEL_STAIRS);
        adaptedOutput.accept(AVPBlocks.CUT_STEEL_SLAB);
        adaptedOutput.accept(AVPBlocks.STEEL_COLUMN);
        adaptedOutput.accept(AVPBlocks.STEEL_FASTENED_SIDING);
        adaptedOutput.accept(AVPBlocks.STEEL_FASTENED_STANDING);
        adaptedOutput.accept(AVPBlocks.STEEL_GRATE);
        adaptedOutput.accept(AVPBlocks.STEEL_PLATING);
        adaptedOutput.accept(AVPBlocks.STEEL_SIDING);
        adaptedOutput.accept(AVPBlocks.STEEL_STANDING);
        adaptedOutput.accept(AVPBlocks.STEEL_TREAD);
        adaptedOutput.accept(AVPBlocks.TITANIUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.TITANIUM_CHAIN_FENCE);
        adaptedOutput.accept(AVPBlocks.CHISELED_TITANIUM);
        adaptedOutput.accept(AVPBlocks.CUT_TITANIUM);
        adaptedOutput.accept(AVPBlocks.CUT_TITANIUM_STAIRS);
        adaptedOutput.accept(AVPBlocks.CUT_TITANIUM_SLAB);
        adaptedOutput.accept(AVPBlocks.TITANIUM_COLUMN);
        adaptedOutput.accept(AVPBlocks.TITANIUM_FASTENED_SIDING);
        adaptedOutput.accept(AVPBlocks.TITANIUM_FASTENED_STANDING);
        adaptedOutput.accept(AVPBlocks.TITANIUM_GRATE);
        adaptedOutput.accept(AVPBlocks.TITANIUM_PLATING);
        adaptedOutput.accept(AVPBlocks.TITANIUM_SIDING);
        adaptedOutput.accept(AVPBlocks.TITANIUM_STANDING);
        adaptedOutput.accept(AVPBlocks.TITANIUM_TREAD);
        adaptedOutput.accept(AVPBlocks.URANIUM_BLOCK);
        adaptedOutput.accept(AVPBlocks.ZINC_BLOCK);
        adaptedOutput.accept(AVPBlocks.RAZOR_WIRE);

        // Alien blocks
        adaptedOutput.accept(AVPBlocks.RESIN);
        adaptedOutput.accept(AVPBlocks.RESIN_VEIN);
        adaptedOutput.accept(AVPBlocks.RESIN_WEB);
        adaptedOutput.accept(AVPBlocks.NETHER_RESIN);
        adaptedOutput.accept(AVPBlocks.NETHER_RESIN_VEIN);
        adaptedOutput.accept(AVPBlocks.NETHER_RESIN_WEB);
    };
}
