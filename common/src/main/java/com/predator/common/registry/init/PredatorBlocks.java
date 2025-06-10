package com.predator.common.registry.init;

import com.predator.common.gameplay.block.TripMineBlock;
import net.minecraft.world.level.block.Block;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class PredatorBlocks {

    public static final AVPDeferredHolder<Block> TRIP_MINE_BLOCK = AVPBlocks.register(
        "trip_mine",
        () -> new TripMineBlock(BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static void initialize() {}
}
