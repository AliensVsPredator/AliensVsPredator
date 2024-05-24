package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class RotatedVariantBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    public RotatedVariantBlockModelProvider(Block baseBlock) {
        this.baseBlock = baseBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        ResourceLocation resourceLocation = TexturedModel.CUBE.create(baseBlock, modelOutput);
        blockModelGeneratorsAccessor.getBlockStateOutput().accept(MixinBlockModelsGenerator_Accessor.createRotatedVariant(baseBlock, resourceLocation));
    }
}