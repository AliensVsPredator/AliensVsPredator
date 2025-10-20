package com.avp.client.render.layer;

import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;

import java.util.UUID;

public class BoilGlowLayer<T> extends AzAutoGlowingLayer<UUID, T> {

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context) {
        super.render(context);
    }
}
