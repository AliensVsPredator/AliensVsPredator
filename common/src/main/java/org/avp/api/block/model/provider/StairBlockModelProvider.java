package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class StairBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    private final Block stairBlock;

    public StairBlockModelProvider(Block baseBlock, Block stairBlock) {
        this.baseBlock = baseBlock;
        this.stairBlock = stairBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var straightResourceLocation =
            ModelTemplates.STAIRS_STRAIGHT.create(stairBlock, textureMapping, modelOutput);
        var innerResourceLocation =
            ModelTemplates.STAIRS_INNER.create(stairBlock, textureMapping, modelOutput);
        var outerResourceLocation =
            ModelTemplates.STAIRS_OUTER.create(stairBlock, textureMapping, modelOutput);

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createStairs(
                stairBlock,
                innerResourceLocation,
                straightResourceLocation,
                outerResourceLocation
            )
        );
    }
}