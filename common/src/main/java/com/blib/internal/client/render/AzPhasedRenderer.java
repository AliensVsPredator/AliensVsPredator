package com.blib.internal.client.render;

public interface AzPhasedRenderer<K, T> {

    void preRender(AzRendererPipelineContext<K, T> context, boolean isReRender);

    void postRender(AzRendererPipelineContext<K, T> context, boolean isReRender);
}
