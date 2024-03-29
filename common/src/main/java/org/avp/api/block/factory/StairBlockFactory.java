package org.avp.api.block.factory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.common.service.Services;
import org.avp.api.GameObject;

public class StairBlockFactory implements BlockFactory {

    private final GameObject<Block> parent;

    public StairBlockFactory(GameObject<Block> parent) {
        this.parent = parent;
    }

    @Override
    public Block create(BlockBehaviour.Properties properties) {
        return Services.BLOCK_REGISTRY.createStairBlock(parent, properties);
    }
}
