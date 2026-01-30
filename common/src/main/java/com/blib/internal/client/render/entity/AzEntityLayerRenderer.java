package com.blib.internal.client.render.entity;

import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import com.blib.api.client.render.v1.layer.AzRenderLayer;
import com.blib.internal.client.render.AzLayerRenderer;
import com.blib.internal.client.render.AzRendererPipelineContext;

public class AzEntityLayerRenderer<T extends Entity> extends AzLayerRenderer<UUID, T> {

    public AzEntityLayerRenderer(Supplier<Collection<AzRenderLayer<UUID, T>>> renderLayerSupplier) {
        super(renderLayerSupplier);
    }

    @Override
    public void applyRenderLayers(AzRendererPipelineContext<UUID, T> context) {
        var animatable = context.animatable();

        if (!animatable.isSpectator()) {
            super.applyRenderLayers(context);
        }
    }
}
