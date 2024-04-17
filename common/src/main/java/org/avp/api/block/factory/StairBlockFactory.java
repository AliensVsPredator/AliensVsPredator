package org.avp.api.block.factory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class StairBlockFactory implements BlockFactory {

    private final GameObject<Block> parent;

    public StairBlockFactory(GameObject<Block> parent) {
        this.parent = parent;
    }

    @Override
    public Block create(BlockBehaviour.Properties properties) {
        return Services.BLOCK_SERVICE.createStairBlock(parent, properties);
    }
}
