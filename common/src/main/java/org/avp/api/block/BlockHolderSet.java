package org.avp.api.block;

import net.minecraft.world.level.block.Block;
import org.avp.api.Holder;

public record BlockHolderSet(
    Holder<Block> base,
    Holder<Block> slab,
    Holder<Block> stairs,
    Holder<Block> wall
) {}
