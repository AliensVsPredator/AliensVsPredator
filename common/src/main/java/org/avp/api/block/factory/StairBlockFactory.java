package org.avp.api.block.factory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.Holder;
import org.avp.common.service.Services;

public class StairBlockFactory implements BlockFactory {

    private final Holder<Block> parent;

    public StairBlockFactory(Holder<Block> parent) {
        this.parent = parent;
    }

    @Override
    public Block create(BlockBehaviour.Properties properties) {
        return Services.BLOCK_SERVICE.createStairBlock(parent, properties);
    }
}
