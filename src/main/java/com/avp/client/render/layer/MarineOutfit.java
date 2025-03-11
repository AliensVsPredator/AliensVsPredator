package com.avp.client.render.layer;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipeline;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

public class MarineOutfit implements AzRenderLayer<MarineMob> {

    @Override
    public void preRender(AzRendererPipelineContext<MarineMob> context) {
    }

    @Override
    public void render(AzRendererPipelineContext<MarineMob> context) {
        var animatable = context.animatable();
        AzRendererPipeline<MarineMob> renderPipeline = context.rendererPipeline();
        var textureMaleLocation = AVPResources.entityTextureLocation("marine_male_outfit");
        var textureFemaleLocation = AVPResources.entityTextureLocation("marine_female_outfit");
        if (Boolean.TRUE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            context.setVertexConsumer(context.multiBufferSource().getBuffer(RenderType.entityCutout(textureMaleLocation)));
        } else {
            context.setVertexConsumer(context.multiBufferSource().getBuffer(RenderType.entityCutout(textureFemaleLocation)));
        }
        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone) {

    }
}
