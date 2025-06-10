package com.predator.common.registry.init;

import net.minecraft.world.item.BlockItem;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPBlockItems;

public class PredatorBlockItems {

    public static final AVPDeferredHolder<BlockItem> TRIP_MINE_BLOCK = AVPBlockItems.register("trip_mine", PredatorBlocks.TRIP_MINE_BLOCK);

    public static void initialize() {}
}
