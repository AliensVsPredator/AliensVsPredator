package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class SlabBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    private final Block slabBlock;

    public SlabBlockModelProvider(Block baseBlock, Block slabBlock) {
        this.baseBlock = baseBlock;
        this.slabBlock = slabBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var bottomResourceLocation =
            ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, modelOutput);
        var topResourceLocation =
            ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, modelOutput);
        var doubleResourceLocation =
            ModelTemplates.CUBE.getDefaultModelLocation(baseBlock);

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createSlab(
                slabBlock,
                bottomResourceLocation,
                topResourceLocation,
                doubleResourceLocation
            )
        );
    }
}