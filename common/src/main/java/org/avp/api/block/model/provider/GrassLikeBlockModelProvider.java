package org.avp.api.block.model.provider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

public class GrassLikeBlockModelProvider extends BlockModelProvider {

    private final Block dirtBlock;

    private final Block grassBlock;

    public GrassLikeBlockModelProvider(Block dirtBlock, Block grassBlock) {
        this.dirtBlock = dirtBlock;
        this.grassBlock = grassBlock;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        var modelOutput = blockModelGeneratorsAccessor.getModelOutput();

        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(dirtBlock);
        TextureMapping textureMapping2 = new TextureMapping()
            .put(TextureSlot.BOTTOM, resourceLocation)
            .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(grassBlock, "_top"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(grassBlock, "_snow"));
        Variant variant = Variant.variant()
            .with(
                VariantProperties.MODEL,
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(
                    grassBlock,
                    "_snow",
                    textureMapping2,
                    modelOutput
                )
            );
        blockModelGeneratorsAccessor.createGrassLikeBlock(grassBlock, ModelLocationUtils.getModelLocation(grassBlock), variant);
    }
}