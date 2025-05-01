package com.avp.fabric.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

import com.avp.common.block.TempAVPBlocks;

public class FlammableBlockRegistry {

    public static void initialize() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        // FIXME: This will break if called in NeoForge.
        fireBlock.setFlammable(TempAVPBlocks.RESIN.get(), 1, 20);
        fireBlock.setFlammable(TempAVPBlocks.RESIN_NODE.get(), 1, 20);
    }
}
