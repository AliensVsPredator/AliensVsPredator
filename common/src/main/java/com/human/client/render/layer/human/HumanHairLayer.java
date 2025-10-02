package com.human.client.render.layer.human;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import mod.azure.azurelib.rewrite.model.AzBone;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzRenderLayer;

public class HumanHairLayer<T extends AbstractHuman> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        var textureLocation = animatable.getHumanFeatureManager().getHairTexture();
        HumanRenderLayerUtil.applyColorWithInvisibility(context, textureLocation, context.animatable().hairColor.get());
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}
