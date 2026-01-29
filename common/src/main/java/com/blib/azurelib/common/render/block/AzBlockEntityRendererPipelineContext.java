package com.blib.azurelib.common.render.block;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import com.blib.azurelib.common.render.AzRendererPipeline;
import com.blib.azurelib.common.render.AzRendererPipelineContext;

public class AzBlockEntityRendererPipelineContext<T extends BlockEntity> extends AzRendererPipelineContext<Long, T> {

    public AzBlockEntityRendererPipelineContext(AzRendererPipeline<Long, T> rendererPipeline) {
        super(rendererPipeline);
    }

    @Override
    public RenderType getDefaultRenderType(
        T animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick,
        RenderType defaultRenderType,
        float alpha
    ) {
        return defaultRenderType;
    }
}
