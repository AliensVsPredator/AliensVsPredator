package com.avp.fabric.client.render.layer.human;

import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

import com.avp.fabric.common.entity.living.human.AbstractHuman;

public class HumanOutfitLayer<T extends AbstractHuman> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();

        var textureLocation = animatable.getHumanFeatureManager().getOutfitTexture();
        var renderType = RenderType.entityCutout(textureLocation);
        var vertexConsumer = context.multiBufferSource().getBuffer(renderType);

        context.setVertexConsumer(vertexConsumer);

        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}
