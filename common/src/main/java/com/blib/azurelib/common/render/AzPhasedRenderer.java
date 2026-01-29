package com.blib.azurelib.common.render;

public interface AzPhasedRenderer<K, T> {

    void preRender(AzRendererPipelineContext<K, T> context, boolean isReRender);

    void postRender(AzRendererPipelineContext<K, T> context, boolean isReRender);
}
