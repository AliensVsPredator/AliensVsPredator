package com.blib.api.client.render.v1.block.pipeline;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;

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
