package com.avp.client.render.layer;

import com.alien.common.gameplay.entity.living.alien.Alien;
import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;

import java.util.UUID;

public class RadiationGlowLayer<T> extends AzAutoGlowingLayer<UUID, T> {

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context) {
        var animatable = context.animatable();
        if (animatable instanceof Alien alien && alien.isIrradiated()) {
            super.render(context);
        }
    }
}
