package com.avp.fabric.common;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

import com.avp.common.block.AVPBlocks;

public class FlammableBlockRegistry {

    public static void initialize() {
        var fireBlock = (FireBlock) Blocks.FIRE;

        fireBlock.setFlammable(AVPBlocks.ABERRANT_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AVPBlocks.ABERRANT_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AVPBlocks.IRRADIATED_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AVPBlocks.IRRADIATED_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AVPBlocks.RESIN.get(), 1, 20);
        fireBlock.setFlammable(AVPBlocks.RESIN_NODE.get(), 1, 20);
    }
}
