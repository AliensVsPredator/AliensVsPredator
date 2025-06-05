package com.avp.fabric.common;

import com.alien.common.registry.init.block.AlienResinBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {

    public static void initialize() {
        var fireBlock = (FireBlock) Blocks.FIRE;

        // TODO: Add resin slabs and resin stairs here.
        fireBlock.setFlammable(AlienResinBlocks.ABERRANT_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienResinBlocks.ABERRANT_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AlienResinBlocks.IRRADIATED_RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienResinBlocks.IRRADIATED_RESIN_NODE.get(), 1, 20);
        fireBlock.setFlammable(AlienResinBlocks.RESIN.get(), 1, 20);
        fireBlock.setFlammable(AlienResinBlocks.RESIN_NODE.get(), 1, 20);
    }
}
