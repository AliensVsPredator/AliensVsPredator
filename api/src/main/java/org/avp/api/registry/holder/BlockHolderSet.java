package org.avp.api.registry.holder;

import net.minecraft.world.level.block.Block;

public record BlockHolderSet(
    BLHolder<Block> base,
    BLHolder<Block> slab,
    BLHolder<Block> stairs,
    BLHolder<Block> wall
) {}
