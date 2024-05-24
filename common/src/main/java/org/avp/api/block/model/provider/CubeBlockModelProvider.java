package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;

public class CubeBlockModelProvider extends BlockModelProvider {

    private final Block block;

    public CubeBlockModelProvider(Block block) {
        this.block = block;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createTrivialBlock(block, TexturedModel.CUBE);
    }
}