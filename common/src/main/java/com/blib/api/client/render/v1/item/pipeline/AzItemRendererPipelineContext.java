package com.blib.api.client.render.v1.item.pipeline;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.api.client.render.v1.AzRendererPipeline;
import com.blib.api.client.render.v1.AzRendererPipelineContext;

public class AzItemRendererPipelineContext extends AzRendererPipelineContext<UUID, ItemStack> {

    private boolean translucent = false;

    private ItemDisplayContext transformType;

    public AzItemRendererPipelineContext(AzRendererPipeline<UUID, ItemStack> rendererPipeline) {
        super(rendererPipeline);
    }

    public ItemDisplayContext getTransformType() {
        return transformType;
    }

    public void setTransformType(ItemDisplayContext transformType) {
        this.transformType = transformType;
    }

    public void setTranslucent(boolean translucent) {
        this.translucent = translucent;
    }

    @Override
    public RenderType getDefaultRenderType(
        ItemStack animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick,
        RenderType defaultRenderType,
        float alpha
    ) {
        return translucent
            ? RenderType.itemEntityTranslucentCull(texture)
            : defaultRenderType;
    }
}
