package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class RotatedPillarBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    public RotatedPillarBlockModelProvider(Block baseBlock) {
        this.baseBlock = baseBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var logMapping = TextureMapping.logColumn(baseBlock);
        var columnResourceLocation = ModelTemplates.CUBE_COLUMN.create(baseBlock, logMapping, modelOutput);
        var horizontalResourceLocation = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(baseBlock, logMapping, modelOutput);
        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createRotatedPillarWithHorizontalVariant(
                baseBlock,
                columnResourceLocation,
                horizontalResourceLocation
            )
        );
    }
}