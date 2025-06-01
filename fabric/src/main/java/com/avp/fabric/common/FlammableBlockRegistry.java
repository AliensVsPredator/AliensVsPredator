package com.avp.fabric.common;

import com.alien.common.registry.init.AlienBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {

    public static void initialize() {
        var fireBlock = (FireBlock) Blocks.FIRE;

        // TODO: Add resin slabs and resin stairs here.
        fireBlock.setFlammable(AlienBlocks.ABERRANT_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienBlocks.ABERRANT_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AlienBlocks.IRRADIATED_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienBlocks.IRRADIATED_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AlienBlocks.RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienBlocks.RESIN_NODE.get(), 1, 20);
    }
}
