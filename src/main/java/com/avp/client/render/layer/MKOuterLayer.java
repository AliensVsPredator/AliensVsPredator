package com.avp.client.render.layer;

import com.avp.AVPResources;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class MKOuterLayer implements AzRenderLayer {

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation("mk50_outer");

    @Override
    public void preRender(AzRendererPipelineContext context) {

    }

    @Override
    public void render(AzRendererPipelineContext context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        context.setRenderColor(-1);
        context.setVertexConsumer(
                context.multiBufferSource()
                        .getBuffer(RenderType.armorCutoutNoCull(TEXTURE))
        );
        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext context, AzBone bone) {

    }
}
