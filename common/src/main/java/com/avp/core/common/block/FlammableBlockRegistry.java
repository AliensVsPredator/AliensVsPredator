package com.avp.core.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {

    public static void initialize() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        // FIXME: .get()
//        fireBlock.setFlammable(AVPBlocks.RESIN.get(), 1, 20);
//        fireBlock.setFlammable(AVPBlocks.RESIN_NODE.get(), 1, 20);
    }
}
