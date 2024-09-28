package org.avp.api.common.registry.holder;

import net.minecraft.world.level.block.Block;

@Deprecated(forRemoval = true)
public record BlockHolderSet(
    BLHolder<Block> base,
    BLHolder<Block> slab,
    BLHolder<Block> stairs,
    BLHolder<Block> wall
) {}
