package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class WallBlockModelProvider extends BlockModelProvider implements ItemModelDelegator {

    private ItemModelDelegator.ItemDelegateData itemDelegateData;

    private final Block baseBlock;

    private final Block wallBlock;

    public WallBlockModelProvider(Block baseBlock, Block wallBlock) {
        this.baseBlock = baseBlock;
        this.wallBlock = wallBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var postResourceLocation =
            ModelTemplates.WALL_POST.create(wallBlock, textureMapping, modelOutput);
        var lowResourceLocation =
            ModelTemplates.WALL_LOW_SIDE.create(wallBlock, textureMapping, modelOutput);
        var tallResourceLocation =
            ModelTemplates.WALL_TALL_SIDE.create(wallBlock, textureMapping, modelOutput);

        itemDelegateData = new ItemDelegateData(ModelTemplates.WALL_INVENTORY, textureMapping);

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createWall(wallBlock, postResourceLocation, lowResourceLocation, tallResourceLocation)
        );
    }

    @Override
    public ItemDelegateData getItemModelDelegateData() {
        return itemDelegateData;
    }
}