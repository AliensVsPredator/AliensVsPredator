package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class FenceGateBlockModelProvider extends BlockModelProvider {

    private final Block baseBlock;

    private final Block fenceGateBlock;

    public FenceGateBlockModelProvider(Block baseBlock, Block fenceGateBlock) {
        this.baseBlock = baseBlock;
        this.fenceGateBlock = fenceGateBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        var textureMapping = TexturedModel.CUBE.get(baseBlock).getMapping();
        var openResourceLocation =
            ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
        var closeResourceLocation =
            ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);
        var wallOpenResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, textureMapping, modelOutput);
        var wallCloseResourceLocation =
            ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, textureMapping, modelOutput);

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(
            MixinBlockModelsGenerator_Accessor.createFenceGate(
                fenceGateBlock,
                openResourceLocation,
                closeResourceLocation,
                wallOpenResourceLocation,
                wallCloseResourceLocation,
                true
            )
        );
    }
}