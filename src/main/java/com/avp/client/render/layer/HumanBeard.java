package com.avp.client.render.layer;

import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

public class HumanBeard implements AzRenderLayer<MarineMob> {

    @Override
    public void preRender(AzRendererPipelineContext<MarineMob> context) {
    }

    @Override
    public void render(AzRendererPipelineContext<MarineMob> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        if (Boolean.FALSE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            return;
        }
        if (Boolean.TRUE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            context.setVertexConsumer(context.multiBufferSource().getBuffer(RenderType.entityCutout(animatable.getMaleBeardTexture())));
        }
        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone) {

    }
}
