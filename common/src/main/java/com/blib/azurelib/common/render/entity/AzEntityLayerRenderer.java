package com.blib.azurelib.common.render.entity;

import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import com.blib.azurelib.common.render.AzLayerRenderer;
import com.blib.azurelib.common.render.AzRendererPipelineContext;
import com.blib.azurelib.common.render.layer.AzRenderLayer;

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
