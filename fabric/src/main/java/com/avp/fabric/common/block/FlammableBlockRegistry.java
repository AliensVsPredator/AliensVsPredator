package com.avp.fabric.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

import com.avp.common.block.AVPBlocks;

public class FlammableBlockRegistry {

    public static void initialize() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.setFlammable(AVPBlocks.RESIN.get(), 1, 20);
        // TODO: Move to common and make flammable in NeoForge.
        fireBlock.setFlammable(AVPBlocks.RESIN_NODE.get(), 1, 20);
    }
}
