package com.blib.azurelib.common.render;

import java.util.Collection;
import java.util.function.Supplier;

import com.blib.azurelib.common.model.AzBone;
import com.blib.azurelib.common.render.layer.AzRenderLayer;

public class AzLayerRenderer<K, T> {

    private final Supplier<Collection<AzRenderLayer<K, T>>> renderLayerSupplier;

    public AzLayerRenderer(Supplier<Collection<AzRenderLayer<K, T>>> renderLayerSupplier) {
        this.renderLayerSupplier = renderLayerSupplier;
    }

    protected void preApplyRenderLayers(AzRendererPipelineContext<K, T> context) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.preRender(context);
        }
    }

    public void applyRenderLayersForBone(AzRendererPipelineContext<K, T> context, AzBone bone) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.renderForBone(context, bone);
        }
    }

    protected void applyRenderLayers(AzRendererPipelineContext<K, T> context) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.render(context);
        }
    }
}
