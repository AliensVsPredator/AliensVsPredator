package com.avp.client.render.layer;

import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;
import net.minecraft.client.renderer.RenderType;

import com.avp.common.entity.living.human.AbstractHumanMob;
import com.avp.common.entity.living.human.marine.MarineMob;

public class HumanHairLayer implements AzRenderLayer<MarineMob> {

    private final String HUMAN_TYPE;

    public HumanHairLayer(String humanType) {
        HUMAN_TYPE = humanType;
    }

    @Override
    public void preRender(AzRendererPipelineContext<MarineMob> context) {}

    @Override
    public void render(AzRendererPipelineContext<MarineMob> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        if (Boolean.TRUE.equals(animatable.getEntityData().get(AbstractHumanMob.SET_GENDER))) {
            context.setVertexConsumer(
                context.multiBufferSource().getBuffer(RenderType.entityCutout(animatable.getHairManager().getMaleHairTexture(HUMAN_TYPE)))
            );
        } else {
            context.setVertexConsumer(
                context.multiBufferSource().getBuffer(RenderType.entityCutout(animatable.getHairManager().getFemaleHairTexture(HUMAN_TYPE)))
            );
        }
        renderPipeline.reRender(context);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<MarineMob> context, AzBone bone) {}
}
