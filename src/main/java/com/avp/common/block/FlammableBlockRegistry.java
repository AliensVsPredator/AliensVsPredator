package com.avp.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {

    public static void initialize() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.setFlammable(AVPBlocks.RESIN, 1, 20);
        fireBlock.setFlammable(AVPBlocks.RESIN_NODE, 1, 20);
    }
}
