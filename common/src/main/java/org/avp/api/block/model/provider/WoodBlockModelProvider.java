package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class WoodBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    private final Block woodBlock;

    public WoodBlockModelProvider(Block baseBlock, Block woodBlock) {
        this.baseBlock = baseBlock;
        this.woodBlock = woodBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var logMapping = TextureMapping.logColumn(baseBlock);
        var textureMapping = logMapping.copyAndUpdate(TextureSlot.END, logMapping.get(TextureSlot.SIDE));
        var resourceLocation = ModelTemplates.CUBE_COLUMN.create(woodBlock, textureMapping, modelOutput);
        blockModelGeneratorsAccessor.getBlockStateOutput().accept(MixinBlockModelsGenerator_Accessor.createAxisAlignedPillarBlock(woodBlock, resourceLocation));
    }
}