package com.alien.common.registry.init;

import net.minecraft.world.level.block.Block;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class AlienBlocks {

    public static final AVPDeferredHolder<Block> ROYAL_JELLY_BLOCK = AVPBlocks.register("royal_jelly_block", BlockProperties.JELLY);

    public static void initialize() {}
}
