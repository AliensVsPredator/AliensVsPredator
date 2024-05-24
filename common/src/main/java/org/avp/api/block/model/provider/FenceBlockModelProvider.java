package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class FenceBlockModelProvider extends BlockModelProvider implements ItemModelDelegator {

    private ItemDelegateData itemDelegateData;

    private final Block baseBlock;

    private final Block fenceBlock;

    public FenceBlockModelProvider(Block baseBlock, Block fenceBlock) {
        this.baseBlock = baseBlock;
        this.fenceBlock = fenceBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.FENCE_POST.create(fenceBlock, textureMapping, modelOutput);
        var sideResourceLocation =
            ModelTemplates.FENCE_SIDE.create(fenceBlock, textureMapping, modelOutput);

        itemDelegateData = new ItemDelegateData(ModelTemplates.FENCE_INVENTORY, textureMapping);

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createFence(fenceBlock, postResourceLocation, sideResourceLocation)
        );
    }

    @Override
    public ItemDelegateData getItemModelDelegateData() {
        return itemDelegateData;
    }
}