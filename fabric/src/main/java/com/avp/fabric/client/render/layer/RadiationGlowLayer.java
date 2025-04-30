package com.avp.fabric.client.render.layer;

import mod.azure.azurelib.rewrite.render.AzRendererPipelineContext;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import com.avp.fabric.common.entity.living.alien.Alien;

public class RadiationGlowLayer<T> extends AzAutoGlowingLayer<T> {

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        if (animatable instanceof Alien alien && alien.isIrradiated()) {
            super.render(context);
        }
    }
}
