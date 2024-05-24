package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;

public abstract class BlockModelProvider {
    public abstract void run(BlockModelGenerators blockModelGenerators);
}
