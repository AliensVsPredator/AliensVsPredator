package com.avp.client.render.layer;

import com.avp.common.entity.living.alien.Alien;
import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

public class RadiationGlowLayer<T> extends AzAutoGlowingLayer<T> {

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        if (animatable instanceof Alien alien && alien.isIrraiated()) {
            super.render(context);
        }
    }
}
